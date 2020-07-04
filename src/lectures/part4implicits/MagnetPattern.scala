package lectures.part4implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object MagnetPattern extends App {

  // method overloading
  class P2PRequest
  class P2PResponse
  class Serializer[T]

  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResponse): Int
    def receive[T: Serializer](message:T) // context bound. a serializer will be injected
    def receive[T: Serializer](message: T, statusCode: Int): Int
    def receive(future: Future[P2PRequest])
  }

  /**
   *
   * Lots of overloads
   *
   * 1 - type erasure - generic types are erased at compile time so these two do not count as overloads
   *        def receive(future: Future[P2PRequest])
   *        def receive(future: Future[Int])
   *
   * 2 - lifting doesn't work for all overloads
   *        val receiveFV = receive _ // what type is underscore
   *
   * 3 - code duplication
   * 4 - type inference and default args
   *        actor.receive(?!)
   *
   */

  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    def apply(): Int = {
      // logic for handling a P2PRequest
      println("Handling P2P request")
      42
    }
  }

  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {
    def apply(): Int = {
      println("Handling P2P response")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  // no more type erasure
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends  MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends  MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  //2 - lifting
  trait MathLib {
    def add1(x: Int) = x + 1
    def add1(s: String) = s.toInt + 1
    // add1 overloads
  }

  // "magnetize"
  trait AddMagnet { // this does not have a type parameter cause then lifting wouldn't work cause compiler gets confused
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x + 1
  }

  implicit class AddString(s: String) extends AddMagnet {
    override def apply(): Int = s.toInt + 1
  }

  val addFV = add1 _ // FV = function value
  println(addFV(1))
  println(addFV("3"))

  // DRAWBACKS of the magnet pattern
  // VERBOSE level 9000
  // very hard to read
  // you can't name or place default arguments
  // call by name doesn't work correctly

  class Handler {
    def handle(s: => String): Unit = {
      println(s)
      println(s)
    }

    // other overloads
  }

  trait HandleMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet): Unit = magnet()

  implicit class StringHandle(s: => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectsMethod(): String = {
    println("Hello, Scala")
    "magnet"
  }

//  handle(sideEffectsMethod())
  handle {
    println("Hello, Scala")
    "magnet" // new StringHandle("magnet")
  }
  // careful !

}

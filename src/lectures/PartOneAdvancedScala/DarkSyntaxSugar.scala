package lectures.PartOneAdvancedScala

import scala.util.Try

object DarkSyntaxSugar extends App {

  // sugar 1 - methods with single parameters
  def singleArgMethod(arg: Int): String = s"$arg dogs"

  val description = singleArgMethod {
    // write some code
    42
  }

  val aTryInstance = Try {
    throw new RuntimeException
  }

  List(1, 2, 3).map { x =>
    x + 1
  }

  /**
   *
   * sugar 2 - single abstract method pattern
   * works for any class or trait that has a single unimplemented method
   *
   */
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  // same as above
  val aFunkyInstance: Action = (x: Int) => x + 1

  // example: Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit =  {
      println("hello, Scala")
    }
  })

  // same as above
  val aSweeterThread = new Thread(() => println("sweet, Scala!"))

  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }

  val anAbstractInstance: AnAbstractType = (a: Int) => println("sweet")

  // syntax sugar #3 the :: and #:: methods are special
  // last character of a method decides the associativity
  // if it ends in a : it means it's right associative otherwise normal left associative
  val prependedList = 2 :: List(3, 4)

  // is actually
  val samePrependedList = List(3, 4).::(2)

  // another example
  val sugarList = 1 :: 2 :: 3 :: List(4, 5)
  val noSugarList = List(4, 5).::(3).::(2).::(1)

  class MyStream[T] {
    def -->: (value: T): MyStream[T] = this // an implementation here
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  /**
   *
   * syntax sugar number 4
   *
   * multi-word method naming
   *
   */
  class TeenGirl(name: String) {
    def `and then said` (gossip: String): Unit = println(s"$name said $gossip")
  }

  val  lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is so sweet!"  // this is actually a method name LOL

  /**
   *
   * syntax sugar #5: infix types
   *
   */
  class Composite[A, B]
  val composite: Composite[Int, String] = ???
  // same as
  val sameComposite: Int Composite String = ???

  class -->[A, B]
  val towards: Int --> String = ???

  //syntax sugar #6: update() is very spacial, much like apply()
  val anArray = Array(1, 2, 3)
  anArray(2) = 7 // rewritten to anArray.update(2, 7)
  // used in mutable collections
  // remember apply() AND update()!

  /*
   *
   * syntax sugar #7: setters for mutable containers
   *
   */
  class Mutable {
    private var internalMember: Int = 0 // private for OO encapsulation
    def member: Int = internalMember // getter
    def member_=(value: Int): Unit = { // the setter
      internalMember = value
    }
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // rewritten as aMutableContainer.member._=(42)

}

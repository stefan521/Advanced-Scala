package lectures.part2afp

import scala.annotation.tailrec

object LazyEvaluation extends App {

  // lazy delays the evaluation of values. only evaluated on a by-need basis
  lazy val x: Int = {
    println("hello")

    42
  }

  println(x)
  println(x)

  // examples of implications:

  // side effects might not happen
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition

  println(
    if (simpleCondition && lazyCondition) "yes"
    else "no"
  )

  // in conjunction wih call by name
  def byNameMethod(n: => Int): Int = n + n + n + 1
  def retrieveMagicValue: Int = {
    // side effect or long computation
    println("waiting")
    Thread.sleep(1000)
    42
  }

//  println(byNameMethod(retrieveMagicValue))

  def goodByNameMethod(n: => Int): Int = {
    lazy val t = n // this technique is CALLED BY NEED

    t + t + t + 1
  }

//  println(goodByNameMethod(retrieveMagicValue))

  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  val lt30Lazy = numbers.withFilter(lessThan30) // lazy values under the hood
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)

  println
  println(gt20Lazy)
  gt20Lazy.foreach(println)

  // for-comprehensions use withFilter with guards
  for {
    a <- List(1, 2, 3) if a % 2 == 0 // uses lazy values
  } yield a + 1
  // becomes
  List(1, 2, 3).withFilter(_ % 2 == 0).map(_ + 1) // List[Int]

  /*
   * Exercise: implement a lazily evaluated, singly linked STREAM of elements.
   *
   * naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite)
   *
   * naturals.take(100) // lazily evaluated stream of the first 100 naturals (finite stream, still lazily evaluated)
   *
   * naturals.take(100).foreach(println) // works fine
   *
   * natural.foreach(println) // crashes cause stream is infinite
   *
   * naturals.map(_ * 2) // stream of all even numbers (potentially infinite)
   *
   */
  abstract class MyStream[+A] {
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]

    def #::[B >: A](element: B): MyStream[B] //prepend operator
    def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] // concatenates two streams

    def foreach(f: A => Unit): Unit
    def map[B](f: A => B): MyStream[B]
    def flatMap[B](f: A => MyStream[B]): MyStream[B]
    def filter(predicate: A => Boolean): MyStream[A]

    def take(n: Int): MyStream[A] // takes the first n elements out of this stream
    def takeAsList(n: Int): List[A] = take(n).toList()

    @tailrec
    final def toList[B >: A](acc: List[B] = Nil): List[B] = // final so it can't be overridden with a non-tailrec implementation
      if (isEmpty) acc.reverse
      else tail.toList(head :: acc)
  }

  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] =
      new Cons(
        start,
        MyStream.from(generator(start))(generator) // call by name - this is only evaluated if the Cons tried to read it
      )
  }

  object EmptyStream extends MyStream[Nothing] {
    override def isEmpty: Boolean = true

    override def head: Nothing = throw new NoSuchElementException

    override def tail: MyStream[Nothing] = throw new NoSuchElementException

    override def #::[B >: Nothing](element: B): MyStream[B] = new Cons(element, this)

    override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

    override def foreach(f: Nothing => Unit): Unit = ()

    override def map[B](f: Nothing => B): MyStream[B] = this

    override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

    override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

    override def take(n: Int): MyStream[Nothing] = this
  }

  class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
    override def isEmpty: Boolean = false

    override val head: A = hd

    override lazy val tail: MyStream[A] = tl // call by need

    override def #::[B >: A](element: B): MyStream[B] = new Cons(element, this)

    override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons(head, tail ++ anotherStream)

    override def foreach(f: A => Unit): Unit = {
      f(hd)
      tail.foreach(f)
    }

    /*
        s = new Cons(1, ?)
        mapped = s.map(_ + 1) = new Cons(2, s.tail.map(_ + 1))
          ... mapped.tail
       */
    override def map[B](f: A => B): MyStream[B] = new Cons(f(hd), tl.map(f)) // preserves lazy evaluation

    override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

    override def filter(predicate: A => Boolean): MyStream[A] =
      if (predicate(head))
        new Cons(head, tail.filter(predicate))
      else
        tail.filter(predicate)

    override def take(n: Int): MyStream[A] =
      if (n <= 0) EmptyStream
      else if (n == 1) new Cons(head, EmptyStream)
      else new Cons(head, tail.take(n - 1))
  }


  // playground
  println("\n Streams \n")
  val naturals = MyStream.from(1)(_ + 1)
  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val startFrom0 = 0 #:: naturals  // naturals.#::(0)
  println(startFrom0.head)

//  startFrom0.take(10000).foreach(println)

  // map, flatMap
  println(startFrom0.map(_ * 2).take(100).toList())
  println(startFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
  println(startFrom0.filter(_ < 10).take(10).toList())

  // Exercises on streams
  // 1 - stream of Fibonacci numbers
  // 2 - stream of prime numbers with Eratosthenes' sieve
  /*
     [2, 3, 4,  ...]
     filter out all numbers divisible by 2
     [2, 3, 5, 7, 9, 11 ...]
     filter out all numbers divisible by 3
     [2, 3, 5, 7, 11, 13, 17 ...]
     filter out numbers divisible by 5 and so on
   */
  def fibonacci(first: BigInt, second: BigInt): MyStream[BigInt] = {
    new Cons(first, fibonacci(second, first + second))
  }

  /*
    [2, 3, 4, 5, 6, 7, 8, 9, 11, 12 ...]
   */
  def sieve(numbers: MyStream[Int]): MyStream[Int] = {
    if (numbers.isEmpty)
      numbers
    else
      new Cons(numbers.head, sieve(numbers.tail.filter(_ % numbers.head != 0)))
  }

  println(fibonacci(1, 1).take(100).toList())
  println(sieve(MyStream.from(2)(_ + 1)).take(100).toList())
}

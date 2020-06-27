package lectures.part2afp

// monads are an abstract type with some fundamental operations
trait Monad[A] {
  def unit(value: A): Monad[A] // also called pure or apply
  def flatMap[B](f: A => Monad[B]): Monad[B] // also called bind
}

// List, Option, Try, Future, Stream, Set are all monads

// monad laws

// 1. left identity  unit(x).flatMap(f) == f(x)
// 2. right identity aMonadInstance.flatMap(unit) == aMonadInstance
// 3. associativity  m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))

trait Attempt[+A] {
  def flatMap[B](f: A => Attempt[B]): Attempt[B]
}
object Attempt {
  // the unit function
  def apply[A](a: => A): Attempt[A] = // call by name to avoid hitting exceptions
    try {
      Success(a)
    } catch {
      case e: Throwable => Failure(e)
    }
}

case class Success[+A](value: A) extends Attempt[A] {
  override def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
    try {
      f(value)
    } catch {
      case e: Throwable => Failure(e)
    }
  }
}

case class Failure(e: Throwable) extends Attempt[Nothing] {
  override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
}

object Monads extends App {

  /*
    left identity

    unit.flatMap(f) = f(x)
    Attempt(x).flatMap(f) = f(x) // Success case!
    Success(x).flatMap(f) = f(x) // proved.


    right identity

    attempt.flatMap(unit) = attempt
    Success(x).flatMap(x => Attempt(x)) = Attempt(x) = Success(x)
    Fail(e).flatMAp(...) = Fail(e)


    associativity

    // LHS                                // RHS
    attempt.flatMap(f).flatMap(g) == attempt.flatMap(x => f(x).flatMap(g))
    Fail(e).flatMap(f).flatMap(g) = Fail(e)
    Fail(e).flatMap(x => f(x).flatMap(g)) = Fail(e)

    Success(v).flatMap(f).flatMap(g) = // LHS
      f(v).flatMap(g) OR Fail(e)

    Success(v).flatMap(x => f(x).flatMap(g)) = // RHS
      f(v).flatMap(g) OR Fail(e)
   */

  val attempt = Attempt {
    throw new RuntimeException("My own monad, yes!")
  }

  println(attempt)

  /*
    EXERCISE:
      1) implement a Lazy[T] monad = computation which will only be executed when it is needed

      unit/ apply in a companion object for Lazy
      flatMap = transforms a value into another lazy instance

      2) Monads = unit + flatMap
         Monads = unit + map + flatten

         Monad[T] {
          def flatmap[B](f: T => Monad[B]): Monad[B] = ... (implemented already)

          def map[B](f: T => B): Monad[B] = flatMap(v => unit(f(v)))

          def flatten(m: Monad[Monad[T]]): Monad[T] = m.flatMap(identity)
         }
   */

  class Lazy[+T](expression: => T) {
    def value: T = expression

    //call by need
    lazy val internalValue: T = expression

                      // this craziness means f receives its parameter by name
    def flatMap[B](f: ( => T) => Lazy[B]): Lazy[B] = f(internalValue)
  }
  object Lazy extends {
    def apply[T](v: => T): Lazy[T] = new Lazy(v) // it's also a factory method
  }

  val mapNum  = (n: Int) => n + 1
  val mapWithFlatMap = List(1, 2, 3).flatMap(n => List(mapNum(n)))

  println(mapWithFlatMap)

  // the IDE is smart enough to suggest replacing this call with flatten lol
  val flattenWithFlatMap = List(List(7), List(8), List(9)).flatMap(identity)

  println(flattenWithFlatMap)

  val maiLazy = Lazy {
    println("blalbalbla")

    2 + 6
  }

  val printingLazyThing = maiLazy.flatMap(v => Lazy(v * 2))

  // if you comment this out the lazy expression is never evaluated
  println(printingLazyThing.value)
}

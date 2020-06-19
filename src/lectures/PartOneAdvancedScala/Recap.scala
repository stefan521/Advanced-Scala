package lectures.PartOneAdvancedScala

object Recap extends App {

  val aCondition: Boolean = false
  val aConditionedVal = if (aCondition) 42 else 65
  val aCodeBlock = {
    if (aCondition) 54
    56
  }

  // instructions vs expressions
  // Unit = void
  val theUnit: Unit = println("hello, Scala")

  // functions
  def aFunction(x: Int): Int = x + 1

  // recursion: stack and tail
  @scala.annotation.tailrec
  def factorial(n: Int, accumulator: Int): Int =
    if (n <= 0) accumulator
    else factorial(n -1, n * accumulator)

  // object-oriented programming
  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog // object-oriented polymorphism by subtyping

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("crunch")
  }

  // method notations
  val aCroc = new Crocodile
  aCroc.eat(aDog) // dot notation

  aCroc eat aDog // natural language - infix notation

  // all the operators are actually methods in scala
  1 + 2
  1.+(2)

  // anonymous classes - for ad-hoc instantiation of traits
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("yum")
  }

  // generics
  abstract class MyList[+A]

  // singletons and companions
  object MyList

  // case classes
  case class Person(name: String, age: Int)

  // exceptions and try/ catch/ finally

  val throwsException: Nothing = throw new RuntimeException
  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    println("some logs")
  }

  // functional programming
  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  incrementer(1)

  val anonymousIncrementer = (x: Int) => x + 1

  List(1, 2, 3).map(anonymousIncrementer) // HOF
  // map, flatMap, filter

  // for-comprehension
  val pairs = for {
    num <- List(1, 2, 3)
    char <- List('a','b', 'c')
  } yield num + "-" + char

  val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )

  val anOption = Some(2)

  // pattern matching
  val x = 2

  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(name, _) => s"Hi, $name"
  }
}

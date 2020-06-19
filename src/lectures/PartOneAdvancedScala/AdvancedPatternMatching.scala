package lectures.PartOneAdvancedScala

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description: Unit = numbers match {
    case head :: Nil => println(s"the only element is $head.")
    case _ =>
  }

  /**
   * what we can pattern match:
   * - constants
   * - wildcards _
   * - tuples
   * - case classes
   * - some special magic like above
   */

  class Person(val name: String, val age: Int) // not a case class but we want pattern matching

  object Person {
    def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))

    def unapply(age: Int) : Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 20)
  val greeting: String = bob match {
    case Person(name, age) => s"Hi, my name is $name and I am $age years old"
  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }

  println(legalStatus)

  /*
    Exercise. This is ugly. Make a custom pattern matching for these conditions.
   */
  val n: Int = 45
  val matchProperty = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 == 0 => "an even number"
    case _ => "no property"
  }

  // Stefan's solution
  object CustomIntMatcher {
    def unapply(num: Int): Option[String] =
      if (num > -10 && num < 10)
        Some("single digit")
      else if (num % 2 == 0)
        Some("an even number")
      else
        Some("no property")
  }

  val matchFancyProperty = n match {
    case CustomIntMatcher(property) => property
  }

  // Daniel's solution
  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val danFancyMatch = n match {
    case singleDigit() => "it's single digit"
    case even() => "it's even"
    case _ => "no property"
  }

  println(danFancyMatch)

  // custom infix patterns
  case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")

  val humanDescription = either match {
    case number Or string=> s"$number is written as $string"
  }

  println(humanDescription)

  // decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A] (
    override val head: A,
    override val tail: MyList[A]
  ) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed: String = myList match {
    case MyList(1, 2, _*) => "starting with 1, 2"
    case _ => "something else"
  }

  println(decomposed)

  // custom return types for unapply
  // define isEmpty which returns a boolean an get: something

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false
      def get: String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(name) => s"This person is named $name"
    case _ => "An alien"
  })
}

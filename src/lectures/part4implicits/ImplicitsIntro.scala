package lectures.part4implicits

object ImplicitsIntro extends App {

  val pair = "Daniel" -> "555" // there's no arrow method on a string
  val intPair = 1 -> 2 // or on int

  case class Person(name: String) {
    def greet = s"Hi, my name is $name!"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet)

  // the compiler assumes there is only one implicit that matches
  // uncommenting A will make this code not compile
//  class A {
//    def greet: Int = 2
//  }
//  implicit def fromStringToA(str: String): A = new A

  // implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount: Int = 10

  increment(2)
  increment(2)(5)

  // not the same thing as default arguments because the value is found by the compiler in its search scope

}

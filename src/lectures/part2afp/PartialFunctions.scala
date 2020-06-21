package lectures.part2afp

object PartialFunctions extends App {

  // this is defined on the whole Int domain (input)
  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int

  // oh boy -_-
  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 3 => 999
    // otherwise match error
  }
  /*
   Partial function = the domain is a subset of stuff
   {1, 2, 5} => Int
   */

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial function value. it's equivalent to the definition above

  println(aPartialFunction(2))
  // println(aPartialFunction(5215)) // crashes with a match error because partial functions are based on pattern matching

  // PF utilities
  println(aPartialFunction.isDefinedAt(67))

  // lift
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(412))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  } // expands the domain of the function

  // PF extends normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOFs accept partial functions as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  } // the thing map takes is actually a partial function

  /*
    Note: PF can only have ONE parameter type
   */

  /**
   * Exercises
   *
   * 1 - construct a PF instance with an anonymous class
   * 2 - implement a chatbot as a partial function
   */

  val unintelligence = new PartialFunction[String, String] {
    override def isDefinedAt(x: String): Boolean =
      x == "hi" || x == "bye" || x == "name"

    override def apply(message: String): String = message match {
      case "hi" => "Hello :D"
      case "bye" => "Goodbye"
      case "name" => "Chat-o-Bot"
    }
  }

  scala.io.Source.stdin.getLines().foreach(l => println(unintelligence(l)))
}

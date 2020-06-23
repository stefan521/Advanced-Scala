package lectures.part2afp

object CurryAndPartiallyAppliedFunc extends App {

  // curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3)
  println(add3(5))
  println(superAdder(3)(5))

  def curriedAdder(x: Int)(y: Int): Int = x + y // curried method
  val add4: Int => Int = curriedAdder(4) // we want the partially applied function. that's why you need the a notation. this is called lifting

  /*
    methods are not instances of functions. they are members of objects.
    functions are instances of the FunctionX class. essentially objects with an apply method.
    if you want to make a function form a method you need to lift it (also called Eta-Expansion)
    methods can't be functions by default cause JVM is limited in that way
   */
  def inc(x: Int): Int = x + 1
  List(1, 2, 3).map(x => inc(x))

  //Partial function applications
  val add5 = curriedAdder(5) _ // _ means do an ETA-Expansion and convert this expressions into an Int => Int

  /**
   * EXERCISE
   *
   * implement add7: Int => Int = y => 7 + y
   *
   * implement as many add7 variations as you can using these 3 concepts below
   */
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int): Int = x + y
  def curriedAddMethod(x: Int)(y: Int): Int = x + y

  val add7_v1 = (to: Int) => simpleAddFunction(7, to)
  val add7_v2 = curriedAddMethod(7) _
  val add7_v3: Int => Int = curriedAddMethod(7)
  val add7_v4 = (to: Int) => simpleAddMethod(7, to)
  val add7_v5 = simpleAddFunction.curried(7)
  val add7_v6 = curriedAddMethod(7)(_)
  val add7_v7 = simpleAddMethod(7, _: Int)
  val add7_v8 = simpleAddFunction(7, _: Int)

  // underscores are powerful
  def concatenator(a: String, b: String, c: String): String = a + b + c

  val insertName: String => String = concatenator("Hello, I'm ", _: String, ", how are you?")
  // this becomes x: String => concatenator(hello, x, howAreYou)
  println(insertName("Stefan"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String)
  // becomes (x, y) => concatenator("Hello, ", x, y)
  println(fillInTheBlanks("Daniel", " Scala is awesome!"))

  /**
   *
   * Exercises
   *
   * 1 - process a list of numbers and return their string representations with different formats
   * Use the %4.2f, %8.6f, %14.12f with a curried formatter function
   *
   * "%4.2f".format(Math.PI)
   *
   * 2 - dive into the difference between
   *  - functions vs methods
   *  - parameters: by-name vs 0-lambda
   *
   */

  def formatListOfNums(lst: List[Double], form: String): List[String] = lst.map(num => form.format(num))

  val formatNums_42f = formatListOfNums(_: List[Double], "%4.2f")
  val formatNums_86f = formatListOfNums(_: List[Double], "%8.6f")
  val formatNums_1412f = formatListOfNums(_: List[Double], "%14.12f")

  def byName(n: => Int): Int = n + 1
  def byFunction(f: () => Int): Int = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  byName(23) // ok
  byName(method) // ok
  byName(parenMethod()) // ok
  byName(parenMethod) // ok but this is equivalent with byName(parenMethod()). not a HOF
  // byName(() => 42) // not ok cause this is a function, not a by-name int
  byName((() => 42)()) // ok cause we can evaluate the function to an int

  // byFunction(45) // not ok. we need a function that returns an int
  // byFunction(method) // not ok!! the compiler does not do eta expansion here
  byFunction(parenMethod) // the compiler does ETA-expansion
  byFunction(parenMethod _) // underscore kind of useless. compiler will do ETA-expansion anyway ^
  byFunction(() => 46)
}

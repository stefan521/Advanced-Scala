package playground

class ApplyAndUpdateClass {
  def apply(str: String): Unit = println(s"apply $str")
  def update(str: String): Unit = println(s"update $str")
}

object ScalaPlayground extends App {
  val applyAndUpdateClass = new ApplyAndUpdateClass

  applyAndUpdateClass("hello")

  val numbers = List(1, 2, 3, 4)

  val result = numbers.flatMap(x => Set(x, x * 10))

  val optionRes= Some(2).flatMap(_ => Some(4 * 5))

  println(result)
  println(optionRes)
}

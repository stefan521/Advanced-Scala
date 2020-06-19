package playground

class ApplyAndUpdateClass {
  def apply(str: String): Unit = println(s"apply $str")
  def update(str: String): Unit = println(s"update $str")
}

object ScalaPlayground extends App {
  val applyAndUpdateClass = new ApplyAndUpdateClass

  applyAndUpdateClass("hello")
}

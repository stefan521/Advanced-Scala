package lectures.part5typesystem

object RockingInheritance extends App {

  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closeable { // closeable resources
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    // some methods
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(status = 0)
  }

  // diamond problem
  trait Animal {
    def name: String
  }

  trait Lion extends Animal {
    override def name: String = "Lion"
  }

  trait Tiger extends Animal {
    override def name: String = "Tiger"
  }

  class Mutant extends Lion with Tiger

  val mutant = new Mutant

  println(mutant.name)

  /*
    Mutant extends Animal with { override def name: String = "Lion" }
    with { override def name: String = "Tiger" }

     The last override always gets picked - that is how scala resolves the diamond problem
   */

  // the super problem + type linearization
  trait Cold {
    def print(): Unit = println("cold")
  }

  trait Green extends Cold {
    override def print(): Unit = {
      println("green")
      super.print()
    }
  }

  trait Blue extends Cold {
    override def print(): Unit = {
      println("blue")
      super.print()
    }
  }

  class Red {
    def print(): Unit = println("red")
  }

  // White = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
  class White extends Red with Green with Blue {
    override def print(): Unit = {
      println("white")
      super.print()
    }
  }

  val color = new White
  color.print() // white, blue, green, cold

}

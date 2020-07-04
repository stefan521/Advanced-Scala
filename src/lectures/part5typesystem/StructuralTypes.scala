package lectures.part5typesystem

object StructuralTypes extends App {

  // structural types
  type JavaClosable = java.io.Closeable

  class HipsterCloseable {
    def close(): Unit = println("yeah yeah I'm closing")
    def closeSilently(): Unit = println("not making a sound")
  }

//  def closeQuietly(closeable: JavaCloseable OR HipsterCloseable) ??
  type UnifiedCloseable = {
    def close(): Unit
  } // STRUCTURAL TYPE

  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit = unifiedCloseable.close()

  closeQuietly(new JavaClosable{
    override def close(): Unit = ???
  })

  closeQuietly(new HipsterCloseable)

  // TYPE REFINEMENTS
  type AdvancedCloseable = JavaClosable {
    def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaClosable {
    override def close(): Unit = println("Java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }

  def closeShhh(advCloseable: AdvancedJavaCloseable): Unit = advCloseable.closeSilently()

  closeShhh(new AdvancedJavaCloseable)
//  closeShhh(new HipsterCloseable) -- won't work

  // using structural types as standalone types. the parameter thing is its own type
  def altClose(closeable: { def close(): Unit }): Unit = {
    closeable.close()
  }

  // type-checking => duck typing
  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark!")
  }

  class Car {
    def makeSound(): Unit = println("vrooom!")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car

  // static duck typing ----> DUCK TEST (if it looks like a duck, it's a duck)

  // CAVEAT: based on reflection. Reflection is really bad on performance. Try to avoid

  /*
  Exercises
   */
  trait CBL[+T] {
    def head: T
    def tail: CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString: String = "BRAINZ"
  }

  def f[T](somethingWithAHead: { def head: T }): Unit = println(somethingWithAHead.head)

  /**
   * is f compatible with a Cons Based List (CBL) and with a Human? yea
   */

  object  HeadEqualizer {
    type Headable[T] = { def head: T }

    def ===[T](a: Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }
}

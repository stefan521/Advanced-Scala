package lectures.part5typesystem

import lectures.PartOneAdvancedScala.Recap.MyList

object TypeMembers extends App {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    // mostly for type inference purposes to help the compiler
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal

    type AnimalC = Cat // type alias
  }

  val ac = new AnimalCollection
//  val dog: ac.AnimalType = ???
//  val cat: ac.BoundedAnimal = new Cat
  val pup: ac.SuperBoundedAnimal = new Dog
  val cat: ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  // an alternative to Generics
  trait MyList {
    type T
    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends MyList {
    override type T = Int

    override def add(element: Int): MyList = new NonEmptyList(0)
  }

  /*
  Exercise - enforce a type to be applicable to SOME TYPES only
   */

  // LOCKED and should only be applicable to numbers
  trait MyLockedList {
    type A
    def head: A
    def tail: MyLockedList
  }


  trait MyLockedNumberList {
    type A <: AnyVal
  }

  // should not compile
//  class CustomList(hd: String, tl: CustomList) extends MyLockedNumberList {
//    type A = String
//    def head: String = hd
//    def tail: CustomList = tl
//  }

  class IntList(hd: Int, tl: IntList) extends MyLockedList with MyLockedNumberList {
    type A = Int
    def head: Int = hd
    def tail: IntList = tl
  }

  object PathDependentTypes extends App {

    class Outer {
      class Inner
      object InnerObject
      type InnerType

      def print(i: Inner): Unit = println(i)
    }

    def aMethod: Int = {
      class HelperClass
      type HelperType = String
      2
    }

    // per-instance
    val outer = new Outer
    val inner = new outer.Inner // o.Inner is a Type

    val oo = new Outer
    outer.print(inner)

    // path-dependent types

    // all the inner types have a common supertype Outer#Inner

    /*
    Exercise
    DB keyed by Int or String, but maybe others
     */

    trait ItemLike{
      type Key
    }

    trait Item[K] {
      type Key = K
    }

    trait IntItem extends Item[Int]

    trait StringItem extends Item[String]

//    def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

//    get[IntItem](42) //ok
//    get[StringItem]("home") // ok
    // get[IntItem]("scala") // not ok

  }

}

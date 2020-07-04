package lectures.part5typesystem

object FBoundedPolymorphism extends App {

//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  class Cat extends Animal {
//    override def breed: List[Animal] = ???
//  }
//
//  class Dog extends Animal {
//    override def breed: List[Animal] = ???
//  }

  // naive solution
//  trait Animal {
//    def breed: List[Animal]
//  }
//
//  trait Cat extends Animal {
//    override def breed: List[Cat] = ???
//  }
//
//  trait Dog extends Animal {
//    override def breed: List[Dog] = ???
//  }


  // solution 2
//  trait Animal[A <: Animal[A]] { // recursive type: F-Bounded Polymorphism
//    def breed: List[Animal[A]]
//  }
//
//  class Cat extends Animal[Cat] {
//    override def breed: List[Animal[Cat]] = ???
//  }
//
//  class Dog extends Animal[Dog] {
//    override def breed: List[Animal[Dog]] = ???
//  }
//
//  // F-Bounded polymorphism often used in database APIs
//
//  // still can make mistakes
//  class Crocodile extends Animal[Dog] {
//    override def breed: List[Animal[Dog]] = ??? // List[Dog] !!!
//  }

//  // solution 3 FBP + self types
//    trait Animal[A <: Animal[A]] { self: A => // whatever extend this with Animal[A] must also be an A
//      def breed: List[Animal[A]]
//    }
//
//    class Cat extends Animal[Cat] {
//      override def breed: List[Animal[Cat]] = ???
//    }
//
//    class Dog extends Animal[Dog] {
//      override def breed: List[Animal[Dog]] = ???
//    }

//    class Crocodile extends Animal[Dog] {
//      override def breed: List[Animal[Dog]] = ???
//    }

  // once we bring our class hierarchy down one level the FBP is useless
//    trait Fish extends Animal[Fish]
//    class Shark extends Fish {
//      override def breed: List[Animal[Fish]] = List(new Cod)
//    }
//
//    class Cod extends Fish {
//      override def breed: List[Animal[Fish]] = ???
//    }

// Solution # 4 type classes
//  trait Animal
//  trait CanBreed[A] {
//    def breed(a: A): List[A]
//  }
//
//  class Dog extends Animal
//  object Dog {
//    implicit object DogsCanBreed extends CanBreed[Dog] {
//      def breed(a: Dog): List[Dog] = List()
//    }
//  }
//
//  implicit class CanBreedOps[A](animal: A) {
//    def breed(implicit canBreed: CanBreed[A]): List[A] = canBreed.breed(animal)
//  }
//
//  val dog = new Dog
//
//  dog.breed
//
//  class Cat extends Animal
//  object Cat {
//    implicit object CatsCanBreed extends CanBreed[Dog] {
//      def breed(a: dog): List[Dog] = List()
//    }
//  }
//
//  val cat = new Cat
//  cat.breed


  // solution 5
  trait Animal[A] { // pure type classes - the animal is the type class itself
    def breed(a: A): List[A]
  }

  class Dog
  object Dog {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  class Cat
  object Cat {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class AnimalOps[A](animal: A) {
    def breed(implicit animalTypeClassInstance: Animal[A]): List[A] =
      animalTypeClassInstance.breed(animal)
  }

  val dog = new Dog
  dog.breed

  val cat = new Cat
//  cat.breed // fails to compile cause no implicit available
}

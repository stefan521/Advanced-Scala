package lectures.part5typesystem

object Variance extends App {

  // CONTRAVARIANT = A or subtypes of A (sane)
  // INVARIANT = exactly A
  // COVARIANCE = A or supertypes of A

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // Variance = "inheritance" (type substitution) of generics

  class Cage[T]

  // covariance
  class CCage[+T]
  val covariantCage: CCage[Animal] = new CCage[Cat]

  // invariance
  class ICage[T]
  // line below won't compile
  // val invariantCage: ICage[Animal] = new ICage[Cat]

  // contravariance
  class XCage[-T]
  val contravariantCage: XCage[Cat] = new XCage[Animal]

  class InvariantCage[T](val animal: T) // invariant

  // covariant positions
  class CovariantCage[+T](val animal: T) // vals are in COVARIANT POSITION

  //  class ContravariantCage[-T](val animal: T) // no good

  //  class CovariantVariableCage[+T](var animal: T) // types of vars are in CONTRAVARIANT POSITION
  // variables are both in COVARIANT AND CONTRAVARIANT positions which means the only acceptable type for var is INVARIANT
  class InvariantVariableCage[T](var animal: T) // ok

  trait AnotherCovariantCage[+T] {
//    def addAnimal(animal) // CONTRAVARIANT POSITION -- method arguments are in contravariant position
  }

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }

  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)
  class Kitty extends Cat // cat or below are good
  acc.addAnimal(new Kitty)

  class MyList[+A] { // wants A or above
    def add[B >: A](element: B): MyList[B] = new MyList[B] // we take B which is a supertype of A. This is widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)  // list of Kitty
  val moreAnimals = animals.add(new Cat) // list of Cats
  val evenMoreAnimals = moreAnimals.add(new Dog) // list of Animals ( next lowest ancestor of both Cat and Dog)

  // the goal is that all the elements in the list have a common type

  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION.

  // return types
  class PetShop[-T] {
//    def get(isItAPuppy: Boolean): T // method return types are in covariant positions
    def get[S <: T](isItaPutty: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  /**
   *
   * Invariant, covariant, contravariant
   *
   * Parking[T](List[T]) {
   *  park(vehicle: T)
   *  impound(vehicles: List[T])
   *  checkVehicles(conditions: String): List[T]
   * }
   *
   * 2. someone else's API: IList[T]
   *
   *
   * 3. Parking = monad!
   *  - flatMap
   */
  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  // V1 --- doesn't really work since we can only park one type of vehicle
  class InvariantParking[T](parkedThings: List[T]){
    def park(vehicle: T): InvariantParking[T] = ???
    def impound(vehicles: List[T]): InvariantParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => InvariantParking[S]): InvariantParking[S] = ???
  }

  // V2
  class CovariantParking[+T](parkedThings: List[T]) { // T or subtype of T
    def park[S >: T](vehicle: S):  CovariantParking[T] = ???
    def impound[S >: T](vehicles: List[S]): CovariantParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => CovariantParking[S]): ContravariantParking[S] = ???
  }

  // V3
  class ContravariantParking[-T](vehicles: List[T]) {
    def park(vehicle: T): ContravariantParking[T] = ???
    def impound(vehicles: List[T]): ContravariantParking[T] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???

    def flatMap[R <: T, S](f: R => ContravariantParking[S]): ContravariantParking[S] = ???
  }

  /**
   *
   * Rule of thumb
   *
   *  - use covariance = Collection of things
   *  - use contravariance = Group of actions
   */
}

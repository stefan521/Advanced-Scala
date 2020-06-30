package lectures.part4implicits

object OrganizingImplicits extends App {

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
//  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)

  println(List(1, 4, 5, 3, 2).sorted)

  /*
  Implicits (used as implicit parameters):
    - val/ var
    - objects
    - accessor methods = defs with not parentheses
   */
  case class Person(name: String, age: Int)

  val persons= List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 66)
  )

  implicit val alphabeticalPersonOrdering: Ordering[Person] = Ordering.fromLessThan((p1: Person, p2: Person) => {
    p1.name.compareTo(p2.name) < 0
  })

  println(persons.sorted)

  /**
   *
   * Implicit scope = where the compiler will look for implicits
   *
   * scopes are made of different parts. some parts are higher priority
   *
   * - normal/ local scope/ where you write code = highest priority
   * - imported scope
   * - companion objects of all types involved in the method signature
   *
   * Best practices:
   *
   * if you can define the implicits in the companion objects for that type (i.e. don't block higher priorities)
   *
   * if you do really need multiple implicits values:
   * define the best implicit in the companion object
   * have more options packaged in objects that you can import
   *
   */

  /**
   *
   * Exercise.
   *
   * totalPrice = most used (50%)
   * by unit count = 25%
   * by unit price = 25%
   *
   */
  case class Purchase(nUnit: Int, unitPrice: Double)
  object Purchase {
    implicit val ordering: Ordering[Purchase] = Ordering.fromLessThan((a: Purchase, b: Purchase) => {
      a.nUnit * a.unitPrice < b.nUnit * b.unitPrice
    })
  }

  object OrderByUnitCount {
    implicit val ordering: Ordering[Purchase] = Ordering.fromLessThan((a: Purchase, b: Purchase) => {
      a.nUnit < b.nUnit
    })
  }

  object OrderByUnitPrice {
    implicit val ordering: Ordering[Purchase] = Ordering.fromLessThan((a: Purchase, b: Purchase) => {
      a.unitPrice < b.unitPrice
    })
  }

  println(List(Purchase(2, 4), Purchase(4, 6), Purchase(2, 1)).sorted)
}

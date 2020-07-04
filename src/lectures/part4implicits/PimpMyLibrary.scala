package lectures.part4implicits

import scala.util.{Success, Try}

object PimpMyLibrary extends App {

  // 2.isPrime

  // implicit classes take one and only argument
  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(something: Int): Int = value * something

    def *[A](lst: List[A]): List[Any] = {
      @scala.annotation.tailrec
      def concatenate(count: Int, result: List[A]): List[A] = {
        if (count > 1)
          concatenate(count - 1, result ::: lst)
        else
          result
      }

      concatenate(value, lst)
    }
  }

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }

  new RichInt(42).sqrt

  42.isEven // rewritten as new RichInt(42).isEven

  // the thing above is called type enrichment = pimping

  1 to 10

  import scala.concurrent.duration._
  3.seconds

  // the compiler will not do multiple implicit searches
  //  42.isOdd

  /**
   *
   * Enrich the String class
   * - as Int
   * - encrypt method
   *
   *
   * Keep enriching the Int class with
   * - times (function)
   * 3.times(() => ...)
   *
   * - * which will take a list as an argument
   * 3 * List(1, 2) => List(1, 2, 1, 2, 1, 2)
   *
   */

  implicit class RicherString(val s: String) extends AnyVal {
    def asIntOption: Option[Int] = Try {
      s.toInt
    } match {
      case Success(int) => Some(int)
      case _ => None
    }

    def encryptCaesar(step: Int): String = {
      s.map(c => (c + step).toChar)
    }
  }

  println(3 times 6)
  println(5 * List(1, 8))

  val encrypted = "abc".encryptCaesar(1)

  println(encrypted)
}

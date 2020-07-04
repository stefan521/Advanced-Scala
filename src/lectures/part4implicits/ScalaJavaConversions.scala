package lectures.part4implicits

import java.{util => ju}

object ScalaJavaConversions extends App {
  import scala.jdk.CollectionConverters._

  val javaSet: ju.Set[Int] = new ju.HashSet[Int]
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  val scalaSet = javaSet.asScala

  /*

  Iterator
  Iterable
  ju.List - scala.collection.mutable.Buffer
  ju.Set - scala.collection.mutable.Set
  ju.Map - scala.collection.mutable.Map

   */

  import collection.mutable._
  val numbersBuffer = ArrayBuffer[Int](1, 2, 3)
  val juNumbersBuffer = numbersBuffer.asJava

  println(juNumbersBuffer.asScala eq numbersBuffer) // eq compares references - this is the same reference

  val numbers = List( 1, 2, 3)
  val juNumbers = numbers.asJava // not immutable anymore
  val backToScala = juNumbers.asScala
  println(backToScala eq numbers) // false not the same reference
  println(backToScala == numbers)

  /**
   *
   * Exercise
   *
   * create a Scala-Java conversion between a Java Optional and a Scala Option
   *
   */

  import java.util.Optional

  implicit class OptionAsOptional[T](opt: Option[T]) {
    def asJava = opt match {
      case None => Optional.of(null)
      case Some(v) => Optional.of(v)
    }
  }

  implicit class OptionalAsOption[T](optional: Optional[T]) {
    def asScala: Option[T] =
      if (optional.isPresent)
        Some(optional.get)
      else
        None
  }

  val stefan = Some("Stefan")
  val javaStefan = stefan.asJava

  println(javaStefan.get())

  val backToScalaStefan = javaStefan.asScala

  println(backToScalaStefan.getOrElse("no stefan"))

}

package lectures.part5typesystem

object Reflection extends App {

  // reflection --- call methods at runtime
  // reflection + macros + quasiquotes => METAPROGRAMMING

  case class Person(name: String) {
    def sayMyName(): Unit = println(s"Hi, my name is $name")
  }

  // step 0 - do an import
  import scala.reflect.runtime.{universe => ru}

  // step 1 - instantiate a MIRROR
  val m = ru.runtimeMirror(this.getClass.getClassLoader)

  // step 2 - create a class object
  // creating a class object by name
  val clazz = m.staticClass("lectures.part5typesystem.Reflection.Person") // fully qualified name of pe Person class

  // step 3 - create a reflected mirror = "can DO things"
  val cm = m.reflectClass(clazz)

  // step 4 - get the constructor
  val constructor = clazz.primaryConstructor.asMethod

  // step 5 - reflect the constructor
  val constructorMirror = cm.reflectConstructor(constructor)

  // 6 - invoke the constructor
  var instance = constructorMirror.apply("John")

  println(instance)




  // I have an instance
  val p = Person("Mary") // from the wire as serialized object
  // method name computed from somewhere else
  val methodName = "sayMyName"

  // 1 - we already have the general mirror
  // 2 - reflect the instance
  val reflected = m.reflect(p)

  // 3 - method symbol
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod

  // 4 - reflect the method
  val method = reflected.reflectMethod(methodSymbol)

  // 5 - invoke the method
  method.apply()

  // type erasure
  // generic types are erased at compile time for historical reason for backwards compatibility

  // pp #1: cannot differentiate between generic types at runtime
  val numbers = List(1, 2, 3)

  numbers match {
    case listOfStrings: List[String] =>
      println("list of strings") // at runtime you don't have type parameters so you will see this

    case listOfNumbers: List[Int] =>
      println("list of numbers")
  }

  //  pp #2: limitations on overloads --- these two will have an identical definition post-erasure
  //  def processList[list: List[Int]]: Int = 43
  //  def processList(list: List[String]): Int = 45

  //  TypeTags to the rescue
  import ru._

  val ttag = typeTag[Person]
  println(ttag.tpe)

  class MyMap[K, V]

  def getTypeArguments[T](value: T)(implicit typeTag: TypeTag[T]) = typeTag.tpe match {
    case TypeRef(_, _, typeArguments) => typeArguments
    case _ => List()
  }

  val myMap = new MyMap[Int, String]
  val typeArgs = getTypeArguments(myMap) // (typeTag: TypeTag[MyMap[Int, String]]
  println(typeArgs)

  def isSubtype[A, B](implicit ttagA: TypeTag[A], ttagB: TypeTag[B]): Boolean = {
    ttagA.tpe <:< ttagB.tpe
  }

  class Animal
  class Dog extends Animal
  println(isSubtype[Dog, Animal])
}

package lectures.part4implicits

object TypeClasses extends App {

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div> $name ($age yo) <a href=$email/> </div>"
  }

  val john = User("John", 32, "john@tockthejvm.com")

  john.toHtml
  /**
   *
   * this only works for the types we write
   *
   */

  // option 2 - pattern matching
  object HTMLSerializerPM {
    def serializeToHtml(value: Any): Unit = value match {
      case User(n, a, e) =>
      case _ =>
    }
  }

  /**
   *
   * 1 - we lost type safety
   * 2 - need to modify this code
   * 3 - still ONE implementation for each given type
   *
   */

  // this is a type class
  /*
    it specifies which operations can be applied to a given type
   */
  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  // this is a type class instance
  implicit object UserSerializer extends HTMLSerializer[User] {
    def serialize(user: User): String = s"<div> ${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  println(UserSerializer.serialize(john))

  // we can then define serializers for other types
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString}</div>"
  }

  // we can define MULTIPLE serializers for a given type
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}</div>"
  }

  // TYPE CLASS
  trait MyTypeClassTemplate[T] {
    def action(value: T): String
  }

  object MyTypeClassTemplate {
    def apply[T](implicit instance: MyTypeClassTemplate[T]): MyTypeClassTemplate[T] = instance
  }

  /**
   * Equality
   */
  trait Equal[T] {
    def apply(lhs: T, rhs: T): Int
  }

  object CompareUserByName extends Equal[User] {
    override def apply(lhs: User, rhs: User): Int = {
      lhs.name.compareTo(rhs.name)
    }
  }

  object CompareUSerByEmail extends Equal[User] {
    override def apply(lhs: User, rhs: User): Int = {
      lhs.email.compareTo(rhs.email)
    }
  }

  val sam = User("sam", 30, "sam@sam.com")
  val tara = User("tara", 25, "tara@tara.com")

  println(CompareUserByName(tara, sam))


  // part 2
  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]): HTMLSerializer[T] = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style: color = blue>$value</div>"
  }

  println(HTMLSerializer.serialize(42))
  println(HTMLSerializer.serialize(john))

  // access to the entire type class interface
  println(HTMLSerializer[User].serialize(john))

  def myImplicitly[T](implicit arg: T): T = arg
}

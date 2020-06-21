package lectures.part2afp

trait MySet[A] extends (A => Boolean) {
  /*
   * Exercise: implement a functional set
   */
  def apply(elem: A): Boolean = contains(elem)
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A] // union

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  /*
    Exercise
    - removing of an element
    - intersection of another set
    - difference with another set
   */
  def -(elem: A): MySet[A]
  def &(anotherSet: MySet[A]): MySet[A] // intersection
  def --(anotherSet: MySet[A]): MySet[A] // difference

  /*
    Exercise
   */
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = new NonEmptySet(elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = Unit

  override def -(elem: A): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}

// all elements of type A which satisfy a property
// maths talk  { x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(elem: A): Boolean = property(elem)

  override def +(elem: A): MySet[A] = new PropertyBasedSet[A](
    el => property(el) || el == elem
  )

  override def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](
    el => property(el) || anotherSet.contains(el)
  )

  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](
    el => property(el) && predicate(el)
  )

  override def -(elem: A): MySet[A] = filter(_ != elem)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](
    el => !property(el)
  )

  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def foreach(f: A => Unit): Unit = politelyFail

  // Ugh?
  def politelyFail = throw new IllegalArgumentException("really deep rabbit hole")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean =
    if (head == elem)
      true
    else
      tail.contains(elem)

  override def +(elem: A): MySet[A] =
    if (this.contains(elem))
      this
    else
      new NonEmptySet(elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if(predicate(head))
      filteredTail + head
    else
      filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)

    tail.foreach(f)
  }

  override def -(elem: A): MySet[A] =
    if (head == elem)
      tail
    else
      tail - elem + head

  override def &(anotherSet: MySet[A]): MySet[A] =
    filter(anotherSet)

  override def --(anotherSet: MySet[A]): MySet[A] =
    filter(!anotherSet(_))

  override def unary_! : MySet[A] = new PropertyBasedSet[A](!contains(_))
}

object MySet {
  // vararg
  def apply[A](values: A*): MySet[A] = {
    @scala.annotation.tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if (valSeq.isEmpty)
        acc
      else
        buildSet(valSeq.tail, acc + valSeq.head)
    }

    buildSet(values.toSeq, new EmptySet[A])
  }
}

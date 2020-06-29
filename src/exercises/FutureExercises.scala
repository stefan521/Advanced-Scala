package exercises

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

object FutureExercises extends App {

  /**
   *
   * 1) Write a future that returns immediately with a value
   *
   * 2) A function called inSequence(futureA, futureB) runs futureB after it makes sure futureB is done
   *
   * 3) A future returning the earliest value returned by two futures
   *
   * 4) Last returns a new future with the last value
   *
   * 5) retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T]
   *
   */

  // utility for exercises
  def resolvePromiseFromFuture[T](promise: Promise[T], future: Future[T]): Promise[T] = {
    future.map {
      case Success(result: T) =>
        promise.success(result)

      case Failure(err) =>
        promise.failure(err)
    }

    promise
  }

  // 1)
  val valueNow = Future.successful {
    42
  }

  // 2)
  def inSequence[A](futureA: Future[A], futureB: Future[A]): Future[A] = {
    futureA.flatMap(_ => futureB)
  }

  // 3)
  def first[A](futureA: Future[A], futureB: Future[A]): Future[A] = {
    val promise = Promise[A]

    futureA.onComplete(promise.tryComplete)
    futureB.onComplete(promise.tryComplete)

    promise.future
  }

  // 4)
  def last[A](futureA: Future[A], futureB: Future[A]): Future[A] = {
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]
    val checkAndComplete = (result: Try[A]) =>
      if (!bothPromise.tryComplete(result))
        lastPromise.complete(result)

    futureA.onComplete(checkAndComplete)

    futureB.onComplete(checkAndComplete)

    lastPromise.future
  }

  // 5)
  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] = {
//    Try {
//      action().filter(condition)
//    } match {
//      case Success(value) =>
//        value
//
//      case Failure(_: NoSuchElementException) =>
//        retryUntil(action, condition)
//
//      case Failure(err) =>
//        Future.failed(err)
//    }
//
    // or
    action()
      .filter(condition)
      .recoverWith {
        case _ => retryUntil(action, condition)
      }
  }
}

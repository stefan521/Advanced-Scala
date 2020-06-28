package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {

  /*
    interface Runnable {
      public void run()
    }
   */
  // JVM threads
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Running in parallel")
  })

  aThread.start() // gives the single to the JVM to start a JVM thread which runs on top of a OS thread
  aThread.join() // blocks until aThread finishes running

  // different runs produce different results
//  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
//  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
//  threadHello.start()
//  threadGoodbye.start()

  // executors
  val pool = Executors.newFixedThreadPool(10)
//  pool.execute(() => println("something in the thread pool"))

  pool.execute(() => {
    Thread.sleep(1000)
//    println("done after 1 second")
  })

  pool.execute(() => {
    Thread.sleep(1000)
//    println("almost done")
    Thread.sleep(1000)
//    println("done after 2 seconds")
  })

  //  pool.shutdown()
  //  pool.execute(() => println("should not appear"))
  // pool.shutdownNow() // interrupts sleeping threads
  println(pool.isShutdown) // true even if actions are still executing more ~ like pool accepts more actions

  def runInParallel = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    // race condition. both threads are trying to modify the same memory
    thread1.start()
    thread2.start()
//    println(x)
  }

  for (_ <- 1 to 100) runInParallel

  class BankAccount(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int): Unit = {
    account.amount -= price // account = account.amount - price
//    println("I've bought " + thing)
//    println("my account is now " + account)
  }

//  for (_ <- 1 to 1000) {
//    val account = new BankAccount(50000)
//    val thread1 = new Thread(() => buy(account, "shoes", 3000))
//    val thread2 = new Thread(() => buy(account, "iPhone12", 4000))
//
//    thread1.start()
//    thread2.start()
//    Thread.sleep(10)
//
//    if (account.amount != 43000) println("Aha " + account.amount)
//    println()
//  }

  // options #1: use synchronized()
  def buysSafe(account: BankAccount, thing: String, price: Int): Unit =
    account.synchronized {
      // no two threads can evaluate this at the same time
      account.amount -= price // account = account.amount - price
      System.out.println("I've bought " + thing)
      println("my account is now " + account)
    }

  // options #2 use @volatile

  /**
   *
   * Exercises
   *
   * 1) Construct 50 inception threads
   *    Thread1 -> thread2 -> thread3
   *    println("hello from thread #3")
   *
   *    print these greetings in REVERSE ORDER
   *
   *
   *  2) var x = 0
   *     val threads = (1 to 100).map(_ => new Thread(() => x += 1))
   *
   *     what is the biggest value possible for x? 100
   *     what is the smallest possible value for x? 1
   *
   *
   *  3) var message = ""
   *     val awesomeThread = new Thread(() => {
   *        Thread.sleep(1000)
   *        message = "Scala is awesome"
   *     })
   *
   *     message = "Scala sucks"
   *     awesomeThread.start()
   *     Thread.sleep(20000)
   *     println(message)
   *     sleeping does not guarantee that exact number of milliseconds but AT LEAST that number
   *
   */

  class InceptionThread(val sequenceNumber: Int = 1, val limit: Int = 50) extends Thread {
    override def run(): Unit = {
      if (sequenceNumber < limit) {
        val spawnedThread = new InceptionThread(sequenceNumber + 1, limit)

        spawnedThread.start()
        spawnedThread.join()
      }

      println(s"hello from $sequenceNumber")
    }
  }

  val inception = new InceptionThread()
  inception.start()
}

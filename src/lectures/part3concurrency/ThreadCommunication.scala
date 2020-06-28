package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  /*
    the producer-consumer problem

    [ x ] - container wrapping a value

    producer - sets a value in the container

    consumer - extracts a value from the container

    they run in parallel.
   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0

    // consuming method
    def get: Int = {
      val result = value
      value = 0

      result
    }

    // producer method
    def set(newValue: Int): Unit = value = newValue
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      while(container.isEmpty) {   // busy loop. busy waiting. (Waste)
        println("[consumer] actively waiting...")
      }

      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println(s"[producer] I have produced the value $value")
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

//  naiveProdCons()

  def smartProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      container.synchronized {
        container.wait()
      }

      // at this point the container must have some value
      println(s"[consumer] I have consumes ${container.get}")
    })

    val producer = new Thread(() => {
      println("[producer] Hard at work...")
      Thread.sleep(2000)
      val value = 42

      container.synchronized {
        println(s"[producer] I'm producing $value")
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

//  smartProdCons()

    /*
      producer -> [ ? ? ? ? ] -> consumer
     */

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 10

    def makeConsumer: Thread = new Thread(() => {
      val random = new Random

      while (true) {
        buffer.synchronized {
          while (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }

          // there must be at least one value in the buffer at this point
          val x = buffer.dequeue()
          println(s"[consumer] I have consumer $x")

          buffer.notifyAll()
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    def makeProducer: Thread = new Thread(() => {
      val random = new Random
      var i = 0

      while(true) {
        buffer.synchronized {
          while (buffer.size == capacity) {
            println("[producer] buffer is full, waiting...")
            buffer.wait()
          }

          // there must be an empty slot in the queue/ buffer so we can produce a value

          println(s"[producer] producing $i")
          buffer.enqueue(i)
          i += 1

          // notify is enough here cause the producers can't be sleeping since the buffer had at least one empty slot
          buffer.notifyAll()
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    val consumers = List.fill(3)(makeConsumer)
    val producers = List.fill(3)(makeProducer)

    consumers.foreach(_.start)
    producers.foreach(_.start)
  }

  prodConsLargeBuffer()
}

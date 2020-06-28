package exercises

object JvmThreads extends App {
  /**
   *
   * 1) Can you think of a situation where notify and notifyAll are different?
   *
   * 2) Create a deadlock. (hopefully not in production)
   *
   * 3) Create a livelock. (threads are active but they cannot continue.)
   *
   */

  def testNotifyAll(): Unit = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(()=> {
      bell.synchronized {
        println(s"[thread $i] waiting...")
        bell.wait()
        println(s"[thread $i] hooray!")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(2000)
      println("[announcer] Rock 'n roll!")
      bell.synchronized {
        bell.notifyAll() // change this to notify => only one thread wakes up
      }
    }).start()
  }

  //testNotifyAll()

  // 2 - deadlock
  case class Friend(name: String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this: I am bowing to my friend $other")
        other.rise(this)
        println(s"$this: my friend $other has risen")
      }
    }

    def rise(other: Friend): Unit = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"
    def switchSide(): Unit = {
      if (side == "right")
        side = "left"
      else
        side = "right"
    }

    def pass(other: Friend): Unit = {
      while (this.side == other.side) {
        println(s"$this: Oh, but please, $other, feel free to pass...")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("Sam")
  val pierre = Friend("Pierre")

//  deadlock
//  new Thread(() => sam.bow(pierre)).start()
//  new Thread(() => pierre.bow(sam)).start()

  // livelock - threads are not blocked but they still can't progress (in this case unless OS does some magic scheduling to fix it by coincidence)
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()
}

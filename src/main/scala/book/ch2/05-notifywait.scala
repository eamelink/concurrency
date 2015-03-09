package book.ch2

import scala.collection.mutable.Queue

// Jobs using a busy-wait worker. This eats a lot of CPU
object JobsBusyWait extends App {
  type Task = () => Unit

  val tasks: Queue[Task] = Queue()

  val worker = new Thread {
    def poll(): Option[Task] = tasks.synchronized {
      if(tasks.nonEmpty) Some(tasks.dequeue()) else None
    }

    override def run = while(true) {
      poll() match {
        case Some(task) => task()
        case None =>
      }
    }
  }

  worker.setDaemon(true)
  worker.start()

  def addTask(t: => Unit) = tasks.synchronized {
    tasks.enqueue(() => t)
  }

  addTask { println("Jo!") }
  addTask { println("Hello, world!") }

  worker.join(500)

}


// Jobs using a notify-wait worker. This is much cheaper on the CPU
object JobsNotifyWait extends App {
  type Task = () => Unit

  val tasks: Queue[Task] = Queue()

  val worker = new Thread {
    def poll(): Task = tasks.synchronized {
      while(tasks.isEmpty) tasks.wait()
      tasks.dequeue()
    }

    override def run = while(true) {
      val task = poll()
      task()
    }
  }

  worker.setDaemon(true)
  worker.start()

  def addTask(t: => Unit) = tasks.synchronized {
    tasks.enqueue(() => t)
    tasks.notify()
  }

  addTask { println("Jo!") }
  addTask { println("Hello, world!") }

  worker.join(500)

}
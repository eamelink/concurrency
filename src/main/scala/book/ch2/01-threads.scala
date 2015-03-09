package book.ch2

// Nondeterministic program; the text printed
// by the new thread may happen before the two
// prints by the main thread that are after it in
// the code.
object Threads extends App {
  val thread = new Thread {
    override def run() =
      println("New thread running!")

  }

  println("Starting new thread!")
  thread.start()
  println("New thread started!")

  println("Waiting for new thread to complete.")
  thread.join()
  println("New thread has completed!")
}

// This will never show '0', because the write to x in the thread
// is guaranteed to happen before the join() returning.
object ThreadsJoin extends App {
  var x: Int = 0
  val thread = new Thread {
    override def run() = {
      x = 42
    }
  }

  thread.start()
  thread.join()

  println(x)
}
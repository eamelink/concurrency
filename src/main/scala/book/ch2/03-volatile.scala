package book.ch2

// Infinite loop
object Infinite extends App {

  var stop = false

  var count = 0
  val t1 = Thread {
    // The write to 'stop' in the other thread might never become visible
    // in this thread.

    // Basically, because 'stop' is not volatile, the JVM is allowed to
    // optimize this to while(true), if the initial value is true.
    while(!stop)
      count = count + 1
  }

  val t2 = Thread {
    println("Setting stop flag")
    stop = true
  }

  t1.start()

  // We need to wait a bit, because the while loop in
  // t1 only get's optimized after many runs. If we don't
  // sleep, it (usually?) stops.
  java.lang.Thread.sleep(10)
  t2.start()

  t1.join
  t2.join
  println("Finished, after " + count + " iterations")

}

// Fixed the loop, by making it volatile.
object InfiniteFixed extends App {

  @volatile var stop = false

  var count = 0
  val t1 = Thread {
    // The write to 'stop' in the other thread might never become visible
    // in this thread.

    // Basically, because 'stop' is not volatile, the JVM is allowed to
    // optimize this to while(true), if the initial value is true.
    while(!stop)
      count = count + 1
  }

  val t2 = Thread {
    println("Setting stop flag")
    stop = true
  }

  t1.start()

  // We need to wait a bit, because the while loop in
  // t1 only get's optimized after many runs. If we don't
  // sleep, it (usually?) stops.
  java.lang.Thread.sleep(10)
  t2.start()

  t1.join
  t2.join
  println("Finished, after " + count + " iterations")

}
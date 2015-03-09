package book.ch2

import java.util.concurrent.atomic.AtomicInteger

// Retrieving an incrementing a value.
// Breaks, because
object BrokenIds1 extends App {

  var current = 0
  def next() = {
    current = current + 1
    current
  }

  def take1000 = (1 to 1000).foreach(_ => next())

  val t1 = Thread(take1000)
  val t2 = Thread(take1000)

  t1.start()
  t2.start()

  t1.join()
  t2.join()

  require(current == 2000, s"counter == $current, expected 2000")
  println("It's okay!")

}

object FixedIds1 extends App {
  var current = 0
  def next() = this.synchronized {
    current = current + 1
    current
  }

  def take1000 = (1 to 1000).foreach(_ => next())

  val t1 = Thread(take1000)
  val t2 = Thread(take1000)

  t1.start()
  t2.start()

  t1.join()
  t2.join()

  require(current == 2000, s"counter == $current, expected 2000")
  println("It's okay!")
}

object FixedIds2 extends App {
  var current = new AtomicInteger(0)
  def next() = current.incrementAndGet()

  def take1000 = (1 to 1000).foreach(_ => next())

  val t1 = Thread(take1000)
  val t2 = Thread(take1000)

  t1.start()
  t2.start()

  t1.join()
  t2.join()

  require(current.get == 2000, s"counter == $current, expected 2000")
  println("It's okay!")
}

object Thread {
  def apply(fn: => Any) = new Thread {
    override def run = fn
  }
}
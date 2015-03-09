package book.ch2

// Unsafe money transfer, no synchronization
object TransfersUnsafe extends App {

  case class Account(owner: String, var amount: Int)
  case class Payment(sender: Account, recipient: Account, amount: Int)

  val a1 = Account("Joe", 300)
  val a2 = Account("Jane", 5000)

  val p1 = Payment(a1, a2, 100)
  val p2 = Payment(a2, a1, 100)

  def doPayment(payment: Payment) = {
    payment.recipient.amount = payment.recipient.amount + payment.amount
    payment.sender.amount = payment.sender.amount - payment.amount
  }

  val t1 = Thread {
    (1 to 1000).foreach { _ => doPayment(p1) }
  }

  val t2 = Thread {
    (1 to 1000).foreach { _ => doPayment(p2) }
  }

  t1.start()
  t2.start()

  t1.join()
  t2.join()

  assert(a1.amount == 300, s"Account 1 amount is ${a1.amount}, expected 300")
  assert(a1.amount == 300, s"Account 2 amount is ${a2.amount}, expected 5000")
  println("All is okay!")
}

// Money transfer, prone to deadlock. We lock on both the
// sender and recipient, in that order, so on concurrent processing
// of payments in both directions, a deadlock can happen
object TransfersDeadLock extends App {

  case class Account(owner: String, var amount: Int)
  case class Payment(sender: Account, recipient: Account, amount: Int)

  val a1 = Account("Joe", 300)
  val a2 = Account("Jane", 5000)

  val p1 = Payment(a1, a2, 100)
  val p2 = Payment(a2, a1, 100)

  def doPayment(payment: Payment) =
    payment.sender.synchronized {
      payment.recipient.synchronized {
        payment.recipient.amount = payment.recipient.amount + payment.amount
        payment.sender.amount = payment.sender.amount - payment.amount
      }
    }

  val t1 = Thread {
    (1 to 1000).foreach { _ => doPayment(p1) }
  }

  val t2 = Thread {
    (1 to 1000).foreach { _ => doPayment(p2) }
  }

  t1.start()
  t2.start()

  t1.join()
  t2.join()

  assert(a1.amount == 300, s"Account 1 amount is ${a1.amount}, expected 300")
  assert(a1.amount == 300, s"Account 2 amount is ${a2.amount}, expected 5000")
  println("All is okay!")
}


// Money transfer, prone to deadlock. We lock on both the
// sender and recipient, in that order, so on concurrent processing
// of payments in both directions, a deadlock can happen
object TransfersSafe extends App {

  case class Account(owner: String, var amount: Int)
  case class Payment(sender: Account, recipient: Account, amount: Int)

  val a1 = Account("Joe", 300)
  val a2 = Account("Jane", 5000)

  val p1 = Payment(a1, a2, 100)
  val p2 = Payment(a2, a1, 100)

  def doPayment(payment: Payment) = {
    import payment._
    def execute() = {
      recipient.amount = recipient.amount + amount
      sender.amount = sender.amount - amount
    }

    if(sender.owner < recipient.owner)
      sender.synchronized {
        recipient.synchronized {
          execute()
        }
      }
    else
      recipient.synchronized {
        sender.synchronized {
          execute()
        }
      }

  }

  val t1 = Thread {
    (1 to 1000).foreach { _ => doPayment(p1) }
  }

  val t2 = Thread {
    (1 to 1000).foreach { _ => doPayment(p2) }
  }

  t1.start()
  t2.start()

  t1.join()
  t2.join()

  assert(a1.amount == 300, s"Account 1 amount is ${a1.amount}, expected 300")
  assert(a1.amount == 300, s"Account 2 amount is ${a2.amount}, expected 5000")
  println("All is okay!")
}

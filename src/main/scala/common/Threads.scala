package common

import util.{AkkaApp, Logger}

import scala.annotation.tailrec

trait Exercises {
  def parallel[A, B](a: => A, b: => B): (A, B)
  def periodically(duration: Long)(b: => Unit): Unit
}

object Threads extends Exercises with Logger {

  private class ThreadResult[A](val body: () => A) extends Thread {
    private var _result: A = _

    def result: A = {
      if (_result == null) throw new Exception("result is null")
      else _result
    }

    override def run(): Unit = {
      val tname = Thread.currentThread().getName
      log.info(s"start thread $tname")
      _result = body()
      log.info(s"finish thread $tname")
    }
  }

  private class ThreadRepeat(duration: Long, task: () => Unit) extends Thread {
    override def run(): Unit = repeat()

    @tailrec private def repeat(): Unit = {
      val executionTime = System.currentTimeMillis() + duration
      synchronized {
        while (System.currentTimeMillis() < executionTime) {
          wait(duration - 1)
        }
        task()
      }
      repeat()
    }
  }

  class SyncVar[T] {
    private var value: T = _
    private var isEmpty  = true

    def get(): T = {
      if (isEmpty) throw new Exception("is empty")
      else {
        isEmpty = true
        value
      }
    }

    def put(x: T): Unit = {
      if (!isEmpty) throw new Exception("isn't empty")
      else {
        isEmpty = false
        value = x
      }
    }
  }

  private def thread(body: => Unit): Thread = {
    val t = new Thread {
      override def run(): Unit = body
    }
    t.start()
    t
  }

  override def parallel[A, B](a: => A, b: => B): (A, B) = {
    val t = new ThreadResult(() => {
      val t1 = new ThreadResult(() => a)
      val t2 = new ThreadResult(() => b)
      t1.join()
      t2.join()
      (t1.result, t2.result)
    })
    t.start()
    t.result
  }

  override def periodically(duration: Long)(b: => Unit): Unit = {
    val t = new ThreadRepeat(duration, () => b)
    t.start()
  }

  def rep(n: Int)(f: => Unit): Unit = {
    for (i <- 1 to n) {
      log.info(s"rep $i -------------------------------------------------|")
      f
    }
  }

  def test(): Unit = {

    val sv = new SyncVar[Long]

    rep(10) {
      val t1 = thread {
        sv.put(System.currentTimeMillis())
      }
      val t2 = thread {
        val r = sv.get()
        log.info(r.toString)
      }

//      t2.join()
//      t1.join()
    }
  }

}

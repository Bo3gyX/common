package scheduler

import util.{AkkaApp, Logger}

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object Main extends AkkaApp("Scheduler") {

  trait TaskScheduler extends Runnable with Logger {
    def name: String
    def initial: FiniteDuration
    def delay: FiniteDuration
  }

  object TaskScheduler {
    def apply(n: String, i: FiniteDuration, d: FiniteDuration)(f: =>Unit): TaskScheduler = {
      new TaskScheduler {
        override def name: String            = n
        override def initial: FiniteDuration = i
        override def delay: FiniteDuration   = d
        override def run(): Unit             = f
      }
    }

    def apply(n: String, i: FiniteDuration, d: FiniteDuration, r: Runnable): TaskScheduler = {
      apply(n, i, d)(r.run())
    }
  }

  class MyScheduler(tasks: Seq[TaskScheduler]) {
    private var isStart = false
    def start(): Unit = {
      if (!isStart) {
        for (task <- tasks) {
          scheduler.scheduleWithFixedDelay(task.initial, task.delay)(() => execute(task))
        }
        isStart = true
      } else {
        throw new Exception("Scheduler is already!")
      }
    }

    def execute(task: TaskScheduler): Unit = {
      log.info(s"start task ${task.name}")
      Try(task.run()) match {
        case Success(_) =>
          log.info(s"finish task ${task.name} success")
        case Failure(exception) =>
          log.error(s"finish task ${task.name} failure. ${exception.getMessage}")
          exception.printStackTrace()
      }
    }
  }

  class Task3 extends Runnable {
    private var c = 0
    override def run(): Unit = {
      c = c + 1
      if (c > 3) {
        c = 0
        throw new RuntimeException("AAA! Я упал")
      }
      log.info("I sleep")
      Thread.sleep(5000)
      log.info("I wake up")
    }
  }

  val tasks: Seq[TaskScheduler] = Seq(
    TaskScheduler("task1", 1.second, 1.second) {
      log.info("I work")
    },
    TaskScheduler("task2", 1.second, 1.second) {
      log.info("I sleep")
      Thread.sleep(1000)
      log.info("I wake up")
    },
    TaskScheduler("task3", 2.second, 1.second, new Task3)
  )

  val myScheduler = new MyScheduler(tasks)

  myScheduler.start()

  scheduler.scheduleOnce(2.second){
    actorSystem.terminate()
  }

}

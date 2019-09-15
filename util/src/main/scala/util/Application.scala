package util

trait Application extends App with Logger {

  println(s"OS: ${scala.util.Properties.osName}")
  println(s"java version ${scala.util.Properties.javaVersion}")
  println(s"scala ${scala.util.Properties.versionString}")

  println("------------")

  private var stopHandlers: Seq[() => Unit] = Seq.empty

  def onStop(f: => Unit): Unit = {
    stopHandlers = (() => f) +: stopHandlers
  }

  def stop(): Unit = {
    sys.exit()
  }

  sys.addShutdownHook {
    log.info("Wait stopping...")
    stopHandlers.foreach(_.apply())
    log.info("...stop!")
  }

}

package common

import util.AkkaApp

import scala.concurrent.duration._

object Main extends AkkaApp("CommonApp") {

  log.info("Hello World!!!")

  scheduler.schedule(3.second, 5.second) {
    log.info("Ky!")
  }
}

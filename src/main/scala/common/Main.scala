package common

import util.{AkkaApp, Logger}

import scala.concurrent.Future

object Main extends AkkaApp("Common") with Logger {

  trait Item

  trait Dao {
    def get(id: String): Future[Item]
    def set(item: Item): Future[Boolean]
  }

}

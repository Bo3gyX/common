package common

import _root_.util.AkkaApp
import io.circe.Encoder
import io.circe.derivation.deriveEncoder
import io.circe.syntax._

import scala.reflect.ClassTag
import com.typesafe.config.ConfigFactory
object Main extends AkkaApp("Common") {

  val config = ConfigFactory.load
  println(config)

}

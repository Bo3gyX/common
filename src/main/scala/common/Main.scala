package common

import _root_.util.AkkaApp
import io.circe.Encoder
import io.circe.derivation.deriveEncoder
import io.circe.syntax._

import scala.reflect.ClassTag
import com.typesafe.config.ConfigFactory

import java.time.{Instant, ZoneOffset}
import java.time.format.DateTimeFormatter
object Main extends AkkaApp("Common") {

  val in = "2021-06-28T01:02:45.054000+00:00"
  val i = Instant.parse(in)
  println(i)
  val out = i.toString
  println(out)


}

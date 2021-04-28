package common

import _root_.util.AkkaApp

import scala.util.matching.Regex

object Main extends AkkaApp("Common") {

  val d: Seq[Int] = Seq(
    100,
    500
  )

  println(d)

}

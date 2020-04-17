package common

import java.io.OutputStream

import util.AkkaApp


object Main extends AkkaApp("Common") {

  case class Foo(os: OutputStream, name: String) {

  }

  val f1 = new Foo(_: OutputStream, "f1")
  val f2 = new Foo(_: OutputStream, "f2")

  println(f1)
  println(f2)

}

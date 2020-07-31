package common

import _root_.util.AkkaApp
import common.proto.Syntax._

object Main extends AkkaApp("Common") with ProtoSupport {

  abstract class Status(val name: String)

  object Status {
    case object Complete extends Status("complete")
    case object Failed   extends Status("failed")
  }

  case class Auth(login: String, password: String)

  val s: Status.Failed.type = Status.Failed
  val a: Auth               = Auth("xxx", "yyyy")

  val ps1 = s.toProto
  val ps2 = s.toProto
  val pa  = a.toProto

  println(ps1)
  println(pa)
}

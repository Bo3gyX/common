package common

import common.Main.{Auth, Status}
import common.proto.Writes
import protobuf.Models

trait ProtoSupport {

  implicit val protoStatus: Writes[Status, Models.Status] = new Writes[Status, Models.Status] {

    override def write(obj: Status): Models.Status = obj match {
      case Status.Complete => Models.Status.COMPLETED
      case Status.Failed   => Models.Status.FAILED
    }
  }

  implicit val protoAuth: Writes[Auth, Models.Auth] = new Writes[Auth, Models.Auth] {

    override def write(obj: Auth): Models.Auth =
      Models.Auth
        .newBuilder()
        .setLogin(obj.login)
        .setPassword(obj.password)
        .build()
  }

}

package proto

import com.google.protobuf.Internal.EnumLite
import com.google.protobuf.Message

object Syntax {
  sealed trait ProtoType[-T]
  object ProtoType {
    implicit object EnumLite extends ProtoType[EnumLite]
    implicit object Message  extends ProtoType[Message]
  }
  implicit class ProtoOps[T](val v: T) extends AnyVal {
    def toProto[MSG](implicit writers: Writes[T, MSG], protoType: ProtoType[MSG]): MSG = {
      writers.write(v)
    }
  }
}

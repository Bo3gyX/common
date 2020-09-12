package common.schema

import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}

trait JsTypes {

  sealed trait JsType[In] extends ColumnType[In] {
    type Out <: JsValue
  }

  implicit val jsTypeBoolean: JsType[Boolean] = new JsType[Boolean] {
    override type Out = JsBoolean
    override def name: String            = "bool"
    override def to(value: Boolean): Out = JsBoolean(value)
  }

  implicit val jsTypeInt: JsType[Int] = new JsType[Int] {
    override type Out = JsNumber
    override def name: String        = "int"
    override def to(value: Int): Out = JsNumber(value)
  }

  implicit val jsTypeLong: JsType[Long] = new JsType[Long] {
    override type Out = JsNumber
    override def name: String         = "long"
    override def to(value: Long): Out = JsNumber(value)
  }

  implicit val jsTypeFloat: JsType[Float] = new JsType[Float] {
    override type Out = JsNumber
    override def name: String          = "float"
    override def to(value: Float): Out = JsNumber(value)
  }

  implicit val jsTypeDouble: JsType[Double] = new JsType[Double] {
    override type Out = JsNumber
    override def name: String           = "double"
    override def to(value: Double): Out = JsNumber(value)
  }

  implicit val jsTypeString: JsType[String] = new JsType[String] {
    override type Out = JsString
    override def name: String           = "str"
    override def to(value: String): Out = JsString(value)
  }
}

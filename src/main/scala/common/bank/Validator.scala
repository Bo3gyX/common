package common.bank

import cats.data.ValidatedNec

trait Validator {
  type IN
  type OUT
  def valid(in: IN): ValidatedNec[Validator.Error, OUT]
}

object Validator {

  trait Error {
    def name: String
    def message: String
  }

  case class InvalidValue[T](name: String, value: String, msg: Option[String] = None) extends Error {
    override def message: String = s"invalid value `$value` of $name. ${msg.getOrElse("")}"
  }

  case class MissingValue[T](name: String) extends Error {
    override val message: String = s"missing value of $name"
  }
}

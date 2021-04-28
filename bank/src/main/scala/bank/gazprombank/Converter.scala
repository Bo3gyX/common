package bank.gazprombank

import bank.gazprombank.Converter.{MissingValue, ValidError}
import cats.data.Validated.Valid
import cats.data.ValidatedNec
import cats.implicits._

trait Converter[IN, OUT] {

  def convert(in: IN): ValidatedNec[ValidError, OUT]

  def sequence[V](seq: Seq[ValidatedNec[ValidError, V]]): ValidatedNec[ValidError, List[V]] =
    seq.toList.sequence

  def require[T, V](name: String, t: T)(f: T => ValidatedNec[ValidError, V]): ValidatedNec[ValidError, V] =
    f(t)

  def require[T, V](name: String, t: Option[T])(f: T => ValidatedNec[ValidError, V]): ValidatedNec[ValidError, V] = {
    t match {
      case None    => MissingValue(name).invalidNec
      case Some(v) => f(v)
    }
  }

  def optional[T, V](name: String, t: T)(f: T => ValidatedNec[ValidError, Option[V]]): ValidatedNec[ValidError, Option[V]] =
    f(t)

  def optional[T, V](name: String, t: Option[T])(f: T => ValidatedNec[ValidError, Option[V]]): ValidatedNec[ValidError, Option[V]] =
    t match {
      case None    => Valid(None)
      case Some(v) => f(v)
    }
}

object Converter {

  trait ValidError {
    def field: String
    def message: String
  }

  case class MissingValue(field: String) extends ValidError {
    override def message: String = s"missing value of $field"
  }

  case class InvalidValue(field: String, value: String, msg: String = "") extends ValidError {
    override def message: String = s"invalid value of $field. $value. $msg"
  }

  case class EmptyValue(field: String) extends ValidError {
    override def message: String = s"empty value of $field"
  }
}

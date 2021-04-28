package bank.gazprombank

import bank.gazprombank.Converter.{EmptyValue, InvalidValue, ValidError}
import bank.gazprombank.Entities._
import cats.data.ValidatedNec
import cats.implicits._

object OfferConverter extends Converter[Offer, Request] {

  override def convert(in: Offer): ValidatedNec[ValidError, Request] = {

    val mark = require("mark", in.mark) {
      case mark if mark.isEmpty    => EmptyValue("mark").invalidNec
      case mark if mark.length > 5 => InvalidValue("mark", mark, "length > 5").invalidNec
      case mark                    => Mark(mark).validNec
    }

    val user = require("user", in.owner.map(_.name)) {
      case name if name.isEmpty    => EmptyValue("user.name").invalidNec
      case name if name.length > 5 => InvalidValue("user.name", name, "length > 5").invalidNec
      case name                    => User(name).validNec
    }

    val valuePrice = require("price", in.price) {
      case price if price < 10000 || price > 1000000 =>
        InvalidValue("price", price.toString, "price < 10000 or price > 1000000").invalidNec
      case price =>
        Value("price", price.toString).validNec
    }

    val valuePhone = require("phone", in.owner.flatMap(_.phone)) { phone =>
      def checkFormat(phone: String): Boolean = true
      phone match {
        case p if !checkFormat(p) => InvalidValue("phone", p, "format").invalidNec
        case p                    => Value("phone", p).validNec
      }
    }

    val valueMark = optional("mark", in.mark) { mark =>
      def find(mark: String): Option[String] = Some(mark)
      find(mark).map(Value("mark", _)).validNec
    }

    val valueName = optional("name", in.owner.map(_.name)) {
      case name if name.isEmpty    => EmptyValue("user.name").invalidNec
      case name if name.length > 5 => InvalidValue("user.name", name, "length > 5").invalidNec
      case name                    => Some(Value("name", name)).validNec
    }

    val requiredValues = sequence(List(valuePrice, valuePhone))
    val optionalValues = sequence(List(valueMark, valueName)).map(_.flatMap(_.toSeq))
    val totalValues    = requiredValues.combine(optionalValues)

    (mark, user, totalValues).mapN(Request(_, _, _))
  }
}

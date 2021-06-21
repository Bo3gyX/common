package common.bank.tinkoff

import cats.data.ValidatedNec
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import common.bank.Shark.{SharkConverter, SharkConverterContext, SharkModel}
import common.bank.Validator
import common.bank.Validator.InvalidValue
import common.bank.tinkoff.TinkoffBank.{TinkoffData, TinkoffValidData}

class TinkoffBankConverter extends SharkConverter {
  override type IN  = TinkoffBankConverter.Context
  override type OUT = TinkoffData

  override def convert(in: TinkoffBankConverter.Context): ValidatedNec[Validator.Error, TinkoffData] = {
    val vs4 = in.model.s4 match {
      case s if s < 1 => InvalidValue("s4", s.toString).invalidNec
      case s          => s.toString.validNec
    }

    vs4.map { s4 =>
      TinkoffData(in.validated.a1, in.validated.a2, in.validated.a3, s4)
    }
  }

}

object TinkoffBankConverter {
  case class Context(model: SharkModel, validated: TinkoffValidData) extends SharkConverterContext
}

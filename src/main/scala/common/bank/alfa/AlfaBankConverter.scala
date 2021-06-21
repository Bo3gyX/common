package common.bank.alfa

import cats.data.ValidatedNec
import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxValidatedIdBinCompat0}
import common.bank.Shark.{SharkConverter, SharkConverterContext, SharkModel}
import common.bank.Validator
import common.bank.Validator.InvalidValue
import common.bank.alfa.AlfaBank.{AlfaData, AlfaValidData}

class AlfaBankConverter extends SharkConverter {
  override type IN  = AlfaBankConverter.Context
  override type OUT = AlfaData

  override def convert(in: AlfaBankConverter.Context): ValidatedNec[Validator.Error, AlfaData] = {
    val vs2 = in.model.s2 match {
      case s if s < 100 => InvalidValue("s2", s.toString).invalidNec
      case s            => s.toString.validNec
    }

    val vs4 = in.model.s4 match {
      case s if s < 1 => InvalidValue("s4", s.toString).invalidNec
      case s          => s.toString.validNec
    }

    (vs2, vs4).mapN {
      case (s2, s4) =>
        AlfaData(in.validated.a1, s2, in.validated.a3, s4)
    }
  }

}

object AlfaBankConverter {
  case class Context(model: SharkModel, validated: AlfaValidData) extends SharkConverterContext
}

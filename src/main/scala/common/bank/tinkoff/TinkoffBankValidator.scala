package common.bank.tinkoff

import cats.data.ValidatedNec
import cats.implicits.{catsSyntaxTuple3Semigroupal, catsSyntaxValidatedIdBinCompat0}
import common.bank.Shark.SharkValidator
import common.bank.Validator.{InvalidValue, MissingValue}
import common.bank.tinkoff.TinkoffBank.TinkoffValidData
import common.bank.{Shark, Validator}

class TinkoffBankValidator extends SharkValidator {
  override type OUT = TinkoffValidData

  override def valid(in: Shark.SharkModel): ValidatedNec[Validator.Error, TinkoffValidData] = {
    val vs1 = in.s1 match {
      case s if s.isEmpty                 => MissingValue("s1").invalidNec
      case s if s.equalsIgnoreCase("xxx") => InvalidValue("s1", s).invalidNec
      case s                              => s.validNec
    }

    val vs2 = in.s2 match {
      case s if s < 200 => InvalidValue("s2", s.toString).invalidNec
      case s            => s.toString.validNec
    }

    val vs3 = in.s3 match {
      case s if !s => InvalidValue("s3", s.toString).invalidNec
      case s       => s.toString.validNec
    }

    (vs1, vs2, vs3).mapN(TinkoffValidData)
  }
}

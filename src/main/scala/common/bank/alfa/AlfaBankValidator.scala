package common.bank.alfa

import cats.data.ValidatedNec
import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxValidatedIdBinCompat0}
import common.bank.Shark.SharkValidator
import common.bank.Validator.{InvalidValue, MissingValue}
import common.bank.alfa.AlfaBank.AlfaValidData
import common.bank.{Shark, Validator}

class AlfaBankValidator extends SharkValidator {
  override type OUT = AlfaValidData

  override def valid(in: Shark.SharkModel): ValidatedNec[Validator.Error, AlfaValidData] = {
    val vs1 = in.s1 match {
      case s if s.isEmpty                 => MissingValue("s1").invalidNec
      case s if s.equalsIgnoreCase("xxx") => InvalidValue("s1", s).invalidNec
      case s                              => s.validNec
    }

    val vs3 = in.s3 match {
      case s if !s => InvalidValue("s3", s.toString).invalidNec
      case s       => s.toString.validNec
    }

    (vs1, vs3).mapN(AlfaValidData)
  }
}

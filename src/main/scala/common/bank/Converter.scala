package common.bank

import cats.data.ValidatedNec

trait Converter {
  type IN
  type OUT
  def convert(in: IN): ValidatedNec[Validator.Error, OUT]
}

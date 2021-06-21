package common.bank.tinkoff

import common.bank.BankProduct

trait TinkoffBank extends BankProduct {
  override def name: String = "tinkoff"
  case class TinkoffValidData(a1: String, a2: String, a3: String)
  case class TinkoffData(a1: String, a2: String, a3: String, a4: String)
}

object TinkoffBank extends TinkoffBank

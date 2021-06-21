package common.bank.alfa

import common.bank.BankProduct

trait AlfaBank extends BankProduct {
  override def name: String = "alfa"
  case class AlfaValidData(a1: String, a3: String)
  case class AlfaData(a1: String, a2: String, a3: String, a4: String)
}

object AlfaBank extends AlfaBank

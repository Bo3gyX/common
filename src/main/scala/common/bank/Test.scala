package common.bank

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNec
import common.bank.Shark.{SharkConverter, SharkModel, SharkValidator}
import common.bank.alfa.{AlfaBank, AlfaBankConverter, AlfaBankValidator}
import common.bank.tinkoff.{TinkoffBank, TinkoffBankConverter, TinkoffBankValidator}

object Test {
  val vAlfa = new AlfaBankValidator
  val cAlfa = new AlfaBankConverter

  val vTinkoff = new TinkoffBankValidator
  val cTinkoff = new TinkoffBankConverter

  trait ProductConfig[P <: BankProduct] {
    val product: P
    val validator: SharkValidator
    val converter: SharkConverter
    def context(model: SharkModel, validated: validator.OUT): converter.IN
  }

  implicit val cfgAlfa: ProductConfig[AlfaBank] = new ProductConfig[AlfaBank] {
    override val product: AlfaBank            = AlfaBank
    override val validator: AlfaBankValidator = vAlfa
    override val converter: AlfaBankConverter = cAlfa

    override def context(model: SharkModel, validated: AlfaBank.AlfaValidData): AlfaBankConverter.Context =
      AlfaBankConverter.Context(model, validated)
  }

  implicit val cfgTinkoff: ProductConfig[TinkoffBank] = new ProductConfig[TinkoffBank] {
    override val product: TinkoffBank            = TinkoffBank
    override val validator: TinkoffBankValidator = vTinkoff
    override val converter: TinkoffBankConverter = cTinkoff

    override def context(model: SharkModel, validated: TinkoffBank.TinkoffValidData): TinkoffBankConverter.Context =
      TinkoffBankConverter.Context(model, validated)
  }

  def printValid(product: BankProduct, valid: ValidatedNec[Validator.Error, _], from: String): Unit = valid match {
    case Valid(a)   => println(s"$from: ${product.name} -> $a")
    case Invalid(e) => e.toNonEmptyList.toList.foreach(e => println(s"$from: ${product.name} -> $e"))
  }

  def valid(model: SharkModel): Unit =
    for (cfg <- validators) {
      val valid = cfg.validator.valid(model)
      printValid(cfg.product, valid, "valid")
    }

  def convert[P <: BankProduct](model: SharkModel)(implicit cfg: ProductConfig[P]): Unit = {
    val valid = cfg.validator.valid(model)
    printValid(cfg.product, valid, "valid")
    val context = valid.map(d => cfg.context(model, d))
    val convert = context.andThen { d =>
      val valid = cfg.converter.convert(d)
      printValid(cfg.product, valid, "convert")
      valid
    }
  }

  val model: SharkModel =
    SharkModel("Ваянов Васян Васяныч", 10, false, 0.3f)

  val validators: Seq[ProductConfig[_ <: BankProduct]] =
    Seq(cfgAlfa, cfgTinkoff)

  println("Valid:")
  valid(model)
  println("Convert:")
  convert[AlfaBank](model)
  convert[TinkoffBank](model)
}

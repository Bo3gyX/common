package common.bank

object Shark {
  case class SharkModel(s1: String, s2: Int, s3: Boolean, s4: Float)

  trait SharkConverterContext {
    def model: SharkModel
  }

  trait SharkValidator extends Validator {
    override type IN = SharkModel
  }

  trait SharkConverter extends Converter {
    override type IN <: SharkConverterContext
  }
}

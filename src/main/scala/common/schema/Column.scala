package common.schema

trait Column[In, Out] {
  def name: String
  def columnType: TypeOut[Out]
  def extractor: In => Out
}

object Column {

  def apply[In, Out](n: String, ct: TypeOut[Out])(f: In => Out): Column[In, Out] =
    new Column[In, Out] {
      override def name: String             = n
      override def columnType: TypeOut[Out] = ct
      override def extractor: In => Out     = f
    }
}

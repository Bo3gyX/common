package common.schema

trait ColumnType[In] {
  type Out
  def name: String
  def to(value: In): Out
}

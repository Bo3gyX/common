package common.proto

trait Writes[-F, T] {
  def write(obj: F): T
}

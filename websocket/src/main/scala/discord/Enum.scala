package discord

import cats.data.Validated
import cats.implicits.catsSyntaxValidatedId

trait Enum[K, V] {
  def values: Seq[V]
  def find(value: K): Option[V]

  def valid(value: K): Validated[Throwable, V] =
    find(value) match {
      case Some(v) => v.valid
      case None =>
        val className = this.getClass.getSimpleName.replace("$", "")
        new NoSuchElementException(s"Undefined value $value of $className").invalid
    }
}

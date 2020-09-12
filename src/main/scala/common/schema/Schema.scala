package common.schema

trait Schema[A, B] {
  def columns: Seq[Column[A, B]]

  def column[R](name: String)(f: A => R)(implicit ct: TypeInOut[R, B]): Column[A, B] =
    Column[A, B](name, ct)(v => ct.to(f(v)))
}

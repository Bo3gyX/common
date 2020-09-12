package common

package object schema {

  type TypeInOut[A, B] = ColumnType[A] {
    type Out <: B
  }

  type TypeOut[B] = TypeInOut[_, B]
}

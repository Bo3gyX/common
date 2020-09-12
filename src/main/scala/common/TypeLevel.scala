package common

object TypeLevel {

  trait Nat
  trait _0             extends Nat
  class Succ[N <: Nat] extends Nat

  type _1 = Succ[_0]
  type _2 = Succ[_1]
  type _3 = Succ[_2]
  type _4 = Succ[_3]
  type _5 = Succ[_4]

  trait <[A <: Nat, B <: Nat]

  object < {

    implicit def ltBasic[B <: Nat]: <[_0, Succ[B]] =
      new <[_0, Succ[B]] {}

    implicit def inductive[A <: Nat, B <: Nat](implicit lt: <[A, B]): <[Succ[A], Succ[B]] =
      new <[Succ[A], Succ[B]] {}

    def apply[A <: Nat, B <: Nat](implicit lt: <[A, B]): <[A, B] = lt
  }

  val comparison: <[_2, _3] = <[_2, _3]

  //apply[_1, _3] -> require <[Succ[_0], Succ[_2]]
  //inductive[Succ[_0], Succ[_2]] -> require <[_0, _2]

  //apply[_2, _3] -> require <[Succ[Succ[_0]], Succ[Succ[Succ[_0]]
  //inductive[Succ[_1], Succ[_2]] -> require <[Succ[_0], _2]
  //inductive[Succ[_1], Succ[_2]] -> require <[_1, _2]

}

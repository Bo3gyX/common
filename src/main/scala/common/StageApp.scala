package common

import util.Application

object StageApp extends Application {

  type ~>:[I <: Stage, O <: Stage] = Step[I, O]

  trait Stage {
    def name: String
  }

  trait Pipeline {
    def ~>:[S <: Stage](stage: S): Stage
  }

  trait Value[T] {
    def value: T
  }

  trait Processor[I, O] {
    def process(v: I): O
  }

  type Final = Final.type

  object Final extends Stage with Pipeline {
    override def name: String = "final"

    override def ~>:[S <: Stage](stage: S): S ~>: Final = new Step(stage, this)
  }

  class Step[I <: Stage, O <: Stage](val in: I, val out: O) extends Stage with Pipeline {
    override def name: String = s"${in.name} ~> ${out.name}"

    override def ~>:[S <: Stage](stage: S): S ~>: I ~>: O = new Step(stage, this)
  }

  class Download extends Stage with Processor[String, Int] {
    override def name: String = "download"

    override def process(v: String): Int = {
      v.toInt
    }
  }

  class Resize extends Stage with Processor[Int, Boolean] {
    override def name: String = "resize"

    override def process(v: Int): Boolean = {
      v > 0
    }
  }

  val pipeline = new Download ~>: new Resize ~>: Final

  println(pipeline.name)

  def process[I, O](v: I, processor: Processor[I, O]): O = {
    processor.process(v)
  }

  val res1 = process("100500", pipeline.in)
  val res2 = process(res1, pipeline.out.in)

  println(res1)
  println(res2)


}

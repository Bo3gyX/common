package dictionary

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.Random

object SimplePlay {

  case class State(verbs: Seq[SimpleVerb.Irregular], success: Int, failure: Int, attempts: Int)

  @tailrec def start(state: State): Unit = {
    val len = state.verbs.size

    if (state.attempts > 0 && len != 0) {

      println(s"/ ${state.attempts} : $len /----------------------------")

      val verb  = getVerb(state.verbs)
      val input = inputWord(verb)
      val newState = if (check(input, verb.translation)) {
        println("Success!")
        state.copy(
          verbs = state.verbs.filterNot(_ == verb),
          success = state.success + 1
        )
      } else {
        println(s"Failure! ${verb.translation}")
        state.copy(
          failure = state.failure + 1
        )
      }

      start(
        newState.copy(
          attempts = newState.attempts - 1
        )
      )

    } else {
      finish(state)
    }
  }

  def inputWord(verb: SimpleVerb.Irregular): String = {
    val idx = Random.nextInt(2)
    val word = idx match {
      case 0 => verb.infinitive
      case 1 => verb.pastSimple
      case 2 => verb.pastParticiple
    }
    println(s"word: $word")
    StdIn.readLine()
  }

  def getVerb(verbs: Seq[SimpleVerb.Irregular]): SimpleVerb.Irregular = {
    val idx = Random.nextInt(verbs.size)
    //    println(s"idx: $idx")

    val verb = verbs(idx)
    //    println(s"verb: $verb")
    verb
  }

  def check(input: String, expectation: String): Boolean = {
    val trimInput = input.trim
    trimInput == expectation
  }

  def finish(state: State): Unit = {
    println("++++++++++++++++++++++")
    println(s"success: ${state.success}")
    println(s"failure: ${state.failure}")
    println("finish")
  }

  start(
    State(
      verbs = SimpleVerb.irregulars,
      success = 0,
      failure = 0,
      attempts = 100
    )
  )

}

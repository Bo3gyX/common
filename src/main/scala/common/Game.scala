package common

import scala.annotation.tailrec
import scala.io.StdIn
import scala.util.Random
import scala.util.control.NonFatal

object Game {
  case class Player(id: Int, name: String)
  case class Context(player: Player, value: Int, isGameOver: Boolean)
  val player1: Player = Player(1, "Вася")
  val player2: Player = Player(2, "Петя")
  val maxValue: Int   = 5

  def run(): Unit = {
    val rnd           = Random.between(5, 10)
    val startContext  = Context(player1, rnd, isGameOver = false)
    val finishContext = play(startContext)
    println(s"${finishContext.player.name} молодцом! Ты победил!")
  }

  @tailrec
  def play(implicit context: Context): Context = {
    if (!context.isGameOver) {
      println(s"RND: ${context.value}")
      val inputValue = readValue(math.min(maxValue, context.value))
      val ctx        = gameLogic(inputValue)
      play(ctx)
    } else context
  }

  def gameLogic(value: Int)(implicit context: Context): Context = {
    val diff = context.value - value
    if (diff == 0) {
      context.copy(isGameOver = true)
    } else if (diff < 0) {
      println(s"${context.player.name}, ты проиграл :(")
      toggleContext(value).copy(isGameOver = true)
    } else {
      println("Переход хода -------------")
      toggleContext(value)
    }
  }

  private def toggleContext(value: Int)(implicit context: Context): Context = {
    val nextPlayer = if (context.player == player1) player2 else player1
    val nextValue  = context.value - value
    context.copy(player = nextPlayer, value = nextValue)
  }

  @tailrec
  private def readValue(max: Int)(implicit context: Context): Int = {
    val value = {
      try {
        val in    = StdIn.readLine(s"${context.player.name} введи значение от 1 до $max: ")
        val value = in.toInt
        if (value < 1 || value > max) {
          println(s"Значение должно быть в диапозоне от 1 до $max")
          -1
        } else value
      } catch {
        case _: NumberFormatException =>
          println("Веденно не корректное значение")
          -1
        case NonFatal(err) =>
          println(s"Произошла какая то дичь. ${err.getMessage}")
          -1
      }
    }

    if (value < 0) readValue(max) else value
  }

}

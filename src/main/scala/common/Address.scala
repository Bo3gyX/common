package common

import scala.annotation.tailrec
import scala.util.matching.Regex

object Address {

  val short = Seq(
    "\\bобл\\.?".r,
    "\\bр-н".r,
    "\\bг\\.?".r,
    "\\bпос\\.?".r,
    "\\bул\\.?".r,
    "\\bпр-т".r,
    "\\bпр\\.?".r,
    "\\bпер\\.?".r,
    "\\bнаб\\.?".r,
    "\\bпл\\.?".r,
    "\\bб-р".r,
    "\\bлиния".r,
    "\\bш\\.?".r,
    "\\bд\\.?".r,
    "\\bкорп\\.?".r,
    "\\bстр\\.?".r,
    "\\bэтаж".r,
    "\\bэт\\.?".r,
    "\\bкв\\.?".r,
  )

  def run(adr: String): Unit = {

    var count = 0

    @tailrec
    def replace(it: Iterator[Regex]): Option[String] = {
      if (!it.hasNext) None
      else {
        val pattern = it.next()
        val r = pattern.replaceAllIn(adr, "")
        if (adr.length != r.length) {
          Some(r.trim)
        }
        else {
          count += 1
          replace(it)
        }
      }
    }
    val res = replace(short.iterator)
    println(res)
    println(count)
  }
}

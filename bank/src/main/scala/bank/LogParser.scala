package bank

import play.api.libs.json.{Json, Reads}

import scala.collection.mutable
import scala.io.Source

object LogParser {

  case class LogRow(
      _time: String,
      _time_nano: Long,
      _layer: String,
      _service: String,
      _version: String,
      _branch: String,
      _canary: Long,
      _dc: String,
      _allocation_id: String,
      _context: String,
      _thread: String,
      _level: String,
      _request_id: String,
      _message: String,
      _rest: String)

  val source = Source.fromResource("logs/60c4ab9a124a7d960caf6188.json")

  val txt =
    try source.getLines().toList
    finally source.close

  implicit val jsonLog: Reads[LogRow] = Json.format[LogRow]

  val rows = for (row <- txt) yield Json.parse(row).as[LogRow]

  val MissingValue = """MissingValue\((.*)\)""".r
  val InvalidValue = "InvalidValue\\((.*),(.*),(.*)\\)".r

  case class Field(name: String, reason: String)

  def run: Unit = {

    val map: mutable.Map[String, mutable.Map[String, Int]] = mutable.Map.empty

    for ((r, idx) <- rows.zipWithIndex) {
      val msg     = r._message
      val fields1 = MissingValue.findAllMatchIn(msg).map(math => Field(math.group(1), "missing value")).toSeq.distinct
      val fields2 = InvalidValue.findAllMatchIn(msg).map(math => Field(math.group(1), math.group(3))).toSeq.distinct
      for (field <- fields1 ++ fields2) {
        val f   = map.getOrElse(field.name, mutable.Map(field.reason -> 0))
        val cnt = f.getOrElse(field.reason, 0)
        f.update(field.reason, cnt + 1)
        map.update(field.name, f)
      }
    }

    val sorted = map.toSeq.sortBy(-_._2.values.sum)

    var ct = 0
    for ((name, reasons) <- sorted) {
      var c = 0
      println(s"field: $name:")
      for ((reason, cnt) <- reasons) {
        println(s"reason: $reason, count: $cnt")
        c += cnt
      }
      println(s"count: $c")
      println("--------------------------")
      ct += c
    }
    println(s"total count: $ct")
  }
}

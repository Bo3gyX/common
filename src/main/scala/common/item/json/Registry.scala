package common.item.json

import common.item.{Category, Description}
import play.api.libs.json.{Format, JsResult, JsValue}

import scala.collection.mutable

object Registry {

  trait Open {

    private val readers = mutable.Map.empty[Category, JsValue => JsResult[Description]]
    private val writers = mutable.Map.empty[Category, Description => JsValue]

    protected def register[D <: Description, C <: D#CAT](cat: C, f: Format[D]): Unit = {
      val r = (json: JsValue) => f.reads(json)
      val w = (desc: Description) => f.writes(desc.asInstanceOf[D])
      readers.addOne(cat, r)
      writers.addOne(cat, w)
    }

    def finale(): Finalized = Finalized(readers.toMap, writers.toMap)
  }

  case class Finalized(
      readers: Map[Category, JsValue => JsResult[Description]],
      writers: Map[Category, Description => JsValue])
}

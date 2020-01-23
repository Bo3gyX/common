package dictionary

trait Phrase {
  val words: Seq[Word]

  override def toString: String = {

    val (values, trans) = words.foldLeft((Seq.empty[String], Seq.empty[String])){
      case ((values, trans), v) => (values :+ v.value, trans :+ v.transcription.getOrElse(""))
    }

    def build(seq: Seq[String]): String = {
      if (seq.exists(_.nonEmpty)) {
        seq.map{ v =>
          if (v.isEmpty) "???"
          else v
        }.mkString(" ")
      } else {
        ""
      }
    }

    val part1 = build(values)
    val part2 = {
      val s = build(trans)
      if (s.nonEmpty) s"[$s]"
      else s
    }

    s"$part1$part2"
  }

}

object Phrase {
  def apply(w1: Word, wa: Word*): Phrase = new Phrase {
    override val words: Seq[Word] = w1 +: wa
  }
}

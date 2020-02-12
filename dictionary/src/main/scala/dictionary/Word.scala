package dictionary

trait Word {
  def value: String
  def transcription: Option[String]

  override def toString: String = {
    val t = transcription.map(v => s"[$v]").getOrElse("")
    s"$value$t"
  }
}

object Word {
  def apply(v: String): Word = new Word {
    override def value: String                 = v
    override def transcription: Option[String] = None
  }

  def apply(v: String, t: String): Word = new Word {
    override def value: String                 = v
    override def transcription: Option[String] = Some(t)
  }

  val empty: Word = new Word {
    override def value: String                 = null
    override def transcription: Option[String] = None
  }
}

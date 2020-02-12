package dictionary

trait Verb extends Word {
  def pronouns: Seq[Pronoun]

  def toStringWithPronouns: String = {
    val p = pronouns.map(_.toString).mkString("/")
    s"$toString with $p"
  }

  def phrases: Seq[Phrase] = {
    if (pronouns.isEmpty) Seq(Phrase(Word.empty, this))
    else
      pronouns.map { p =>
        Phrase(p, this)
      }
  }

  def phrase: Phrase = {
    phrases.head
  }

}

object Verb {
  def apply(v: String): Verb = new Verb {
    override def pronouns: Seq[Pronoun]        = Pronoun.Words.all
    override def value: String                 = v
    override def transcription: Option[String] = None
  }

  def apply(v: String, t: String): Verb = new Verb {
    override def pronouns: Seq[Pronoun]        = Pronoun.Words.all
    override def value: String                 = v
    override def transcription: Option[String] = Some(t)
  }

  def apply(v: String, t: String, p: Seq[Pronoun]): Verb = new Verb {
    override def pronouns: Seq[Pronoun]        = p
    override def value: String                 = v
    override def transcription: Option[String] = Some(t)
  }

  def apply(v: String, t: String, p1: Pronoun, pa: Pronoun*): Verb = new Verb {
    override def pronouns: Seq[Pronoun]        = p1 +: pa
    override def value: String                 = v
    override def transcription: Option[String] = Some(t)
  }

  object Words {
    import dictionary.Pronoun.Words._

    val am  = apply("am", "æm", singular1st)
    val is  = apply("is", "ɪz", singular3rd)
    val are = apply("are", "ɑː(r)", plural)

    val be   = apply("be", "bi")
    val was  = apply("was", "wəz", singular)
    val were = apply("were", "wə(r)", plural)
    val been = apply("been", "biːn")

    val bear  = apply("bear", "beə(r)", singular1stAndPlural)
    val bears = apply("bears", "beəz", singular3rd)
    val bore  = apply("bore", "bɔː(r)")
    val borne = apply("borne", "bɔːn")

    val become  = apply("become", "bɪˈkʌm", singular1stAndPlural)
    val becomes = apply("becomes", "bɪˈkʌmz", singular3rd)
  }

}

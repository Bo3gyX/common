package dictionary

import dictionary.Pronoun.Words.singular1st

trait Pronoun extends Word

object Pronoun {
  def apply(v: String): Pronoun = new Pronoun {
    override def value: String                 = v
    override def transcription: Option[String] = None
  }

  def apply(v: String, t: String): Pronoun = new Pronoun {
    override def value: String                 = v
    override def transcription: Option[String] = Some(t)
  }

  object Words {
    val i: Pronoun = apply("i", "aɪ")
    val you: Pronoun = apply("you", "ju")
    val he: Pronoun = apply("he", "hi")
    val she: Pronoun = apply("she", "ʃi")
    val it: Pronoun = apply("it", "ɪt")
    val we: Pronoun = apply("we", "wi")
    val they: Pronoun = apply("they", "ðeɪ")

    val singular = singular1st ++ singular3rd
    val singular1st = Seq(i)
    val singular2nd = Seq(you)
    val singular3rd = Seq(he, she, it)

    val plural = plural1st ++ plural2nd ++ plural3rd
    val plural1st = Seq(we)
    val plural2nd = Seq(you)
    val plural3rd = Seq(they)

    val singular1stAndPlural = singular1st ++ plural

    val all: Seq[Pronoun] = singular ++ plural
  }
}

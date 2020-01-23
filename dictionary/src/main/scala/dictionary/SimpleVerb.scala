package dictionary

trait SimpleVerb {

}

object SimpleVerb {

  case class Irregular(
    infinitive: String,
    pastSimple: String,
    pastParticiple: String,
    translation: String
  )

  val irregulars: Seq[Irregular] = Seq(
    Irregular("be", "was, were", "been", "быть"),
    Irregular("bear", "bore", "born", "носить, рождать"),
    Irregular("become", "became", "become", "становиться"),
    Irregular("begin", "began", "begun", "начинаться"),
    Irregular("break", "broke", "broken", "ломать"),
    Irregular("bring", "brought", "brought", "приносить"),
    Irregular("buy", "bought", "bought", "покупать"),
    Irregular("catch", "caught", "caught", "ловить"),
    Irregular("choose", "chose", "chosen", "выбирать"),
    Irregular("come", "came", "come", "приходить"),
    Irregular("do", "did", "done", "делать"),
    Irregular("draw", "drew", "drawn", "рисовать"),
    Irregular("drink", "drank", "drunk", "пить"),
    Irregular("drive", "drove", "driven", "ехать"),
    Irregular("eat", "ate", "eaten", "есть"),
    Irregular("fall", "fell", "fallen", "падать"),
    Irregular("feel", "felt", "felt", "чувствовать"),
    Irregular("find", "found", "found", "находить"),
    Irregular("fly", "flew", "flown", "летать"),
    Irregular("forget", "forgot", "forgotten", "забывать"),
    Irregular("get", "got", "got", "получать"),
    Irregular("give", "gave", "given", "давать"),
    Irregular("go", "went", "gone", "идти"),
    Irregular("grow", "grew", "grown", "расти, выращивать"),
    Irregular("have", "had", "had", "иметь"),
    Irregular("hear", "heard", "heard", "слышать"),
    Irregular("keep", "kept", "kept", "хранить"),
    Irregular("know", "knew", "known", "знать"),
    Irregular("learn", "learnt", "learnt", "учить"),
    Irregular("leave", "left", "left", "оставлять, покидать"),
    Irregular("let", "let", "let", "разрешать"),
    Irregular("lose", "lost", "lost", "терять"),
    Irregular("make", "made", "made", "делать"),
    Irregular("meet", "met", "met", "встречать"),
    Irregular("put", "put", "put", "класть"),
    Irregular("read", "read", "read", "читать"),
    Irregular("run", "ran", "run", "бежать"),
    Irregular("say", "said", "said", "сказать"),
    Irregular("see", "saw", "seen", "видеть"),
    Irregular("send", "sent", "sent", "посылать"),
    Irregular("show", "showed", "shown", "показывать"),
    Irregular("sing", "sang", "sung", "петь"),
    Irregular("sit", "sat", "sat", "сидеть"),
    Irregular("sleep", "slept", "slept", "спать"),
    Irregular("speak", "spoke", "spoken", "говорить"),
    Irregular("spend", "spent", "spent", "проводить, тратить"),
    Irregular("swim", "swam", "swum", "плавать"),
    Irregular("take", "took", "token", "брать"),
    Irregular("teach", "taught", "taught", "преподовать"),
    Irregular("tell", "told", "told", "рассказать"),
    Irregular("think", "thought", "thought", "думать"),
    Irregular("understand", "understood", "understood", "понимать"),
    Irregular("write", "wrote", "written", "писать")
  )
}

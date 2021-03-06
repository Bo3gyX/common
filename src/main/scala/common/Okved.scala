package common

object Okved {

  val okvedByValue: Map[String, Seq[String]] = Map(
    "Сельское и лесное хозяйство" -> Seq("01", "02", "03"),
    "Производство. Добывающая промышленность" -> Seq(
      "03", "05", "06", "07", "08", "09", "10", "11", "12", "13",
      "14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
      "24", "25", "26", "27", "28", "29", "30", "31", "32", "33"
    ),
    "Коммунальное хозяйство" -> Seq("35", "36", "37", "38", "39", "81"),
    "Строительство" -> Seq("41", "42", "43"),
    "Оптовая торговля" -> Seq("45", "46"),
    "Розничная торговля / Общ.питание / Сфера услуг" -> Seq("47", "55", "56", "82", "92", "94", "95", "96"),
    "Транспорт / Логистика / Складское хранение" -> Seq("49", "50", "51", "52", "53"),
    "Пресса / ТВ / Радио / Сфера искусств" -> Seq("58", "59", "60", "61", "90", "91"),
    "Информационные технологии" -> Seq("62", "63"),
    "Финансовое дело / Банки" -> Seq("64"),
    "Страхование" -> Seq("65", "66"),
    "Наука. Образование" -> Seq("69", "70", "71", "72", "73", "74", "75", "85"),
    "Услуги по продаже / аренде недвижимости" -> Seq("68", "77"),
    "Туризм / Развлечения" -> Seq("79", "93"),
    "Государственная служба" -> Seq("84"),
    "Правоохр. органы / силовые структуры" -> Seq("80", "84.23", "84.24"),
    "Здравоохранение" -> Seq("86", "87", "88")
  )

  val okvedByCode: Map[String, String] = {
    okvedByValue
      .foldLeft(Map.empty[String, String]) {
        case (ac, (k, v)) =>
          ac ++ v.map(v => v -> k)
      }
  }

}

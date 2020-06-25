package common.item

object Raw {

  val descriptions: String =
    """
      |[
      |  {
      |    "category": "food",
      |    "data": {
      |      "name": "apple",
      |      "portion": 2,
      |      "restore": 2
      |    }
      |  },
      |  {
      |    "category": "food",
      |    "data": {
      |      "name": "orange",
      |      "portion": 8,
      |      "restore": 1
      |    }
      |  },
      |  {
      |    "category": "weapon",
      |    "data": {
      |      "name": "rifle",
      |      "damage": 10,
      |      "rate": 3,
      |      "capacity": 30
      |    }
      |  },
      |  {
      |    "category": "weapon",
      |    "data": {
      |      "name": "pistol",
      |      "damage": 5,
      |      "rate": 1,
      |      "capacity": 8
      |    }
      |  },
      |  {
      |    "category": "tool",
      |    "data": {
      |      "name": "wrench",
      |      "repair": 5,
      |      "durability": 100
      |    }
      |  },
      |  {
      |    "category": "tool",
      |    "data": {
      |      "name": "screwdriver",
      |      "repair": 1,
      |      "durability": 20
      |    }
      |  }
      |]
      |""".stripMargin
}

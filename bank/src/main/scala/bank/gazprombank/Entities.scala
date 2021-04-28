package bank.gazprombank

object Entities {

  //has
  case class Owner(name: String, phone: Option[String])
  case class Offer(mark: String, price: Option[Int], owner: Option[Owner])

  //need
  case class Mark(name: String)
  case class User(name: String)
  case class Value(id: String, value: String)
  case class Request(mark: Mark, user: User, values: Seq[Value])

}

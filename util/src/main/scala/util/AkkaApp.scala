package util

import akka.stream.{ActorMaterializer, Materializer}

abstract class AkkaApp(val actorSystemName: String) extends Application with ActorSupport {

  implicit lazy val materializer: Materializer = ActorMaterializer()

  onStop {
//    actorSystem.terminate()
  }
}

package common

import _root_.util.AkkaApp
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.language.implicitConversions
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object Main extends AkkaApp("Common") {

  val routes = get {
    complete("Hello world!")
  }

  val serverSource =
    Http().newServerAt(interface = "localhost", port = 8080).bind(routes)


  StdIn.readLine("xxx:")
  for {
    binding   <- serverSource
    done      <- binding.unbind()
    terminate <- binding.terminate(10.seconds)
    wt        <- binding.whenTerminated
    wts       <- binding.whenTerminationSignalIssued
  } yield {
    println(s"unbind: ${binding.localAddress} $done")
    println(s"terminate $terminate")
    println(s"whenTerminated ${wt}")
    println(s"whenTerminationSignalIssued ${wts.timeLeft.toSeconds}")
  }

}

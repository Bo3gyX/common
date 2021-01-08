package common.zioapp

import java.io.FileNotFoundException

import zio.{Managed, Task, TaskManaged, UIO}

import scala.io.Source

trait FileLoader[T] {
  def load(resource: String): Task[T]
}

object FileLoader {

  object ResourceFileLoader extends FileLoader[String] {

    override def load(resource: String): Task[String] = {
      make(resource).use(extract).mapError {
        case _: NullPointerException => new FileNotFoundException(s"resource $resource not found")
        case e => e
      }
    }

    private def make(resource: String): TaskManaged[Source] = {
      val acquire: Task[Source]        = Task(Source.fromResource(resource))
      val release: Source => UIO[Unit] = (s: Source) => UIO(s.close())
      Managed.make(acquire)(release)
    }

    private def extract(source: Source): Task[String] = {
      Task.effect(source.getLines().mkString)
    }
  }
}

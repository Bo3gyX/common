package common.tests

import common.zioapp.{Echo, MainProgram}
import zio.test.Assertion.{anything, isTrue}
import zio.test.environment.TestEnvironment
import zio.test.mock.{mockable, Expectation}
import zio.test.{assertM, DefaultRunnableSpec, ZSpec}

object ExampleProgramSpec extends DefaultRunnableSpec {

  @mockable[Echo.Service]
  object EchoServiceMock

  val mock =
    (EchoServiceMock.Repeated(anything, Expectation.value("test")).atMost(2) &&
      EchoServiceMock.Echo(anything, Expectation.value("test")).atMost(2)).optional

  val layer = mock >>> MainProgram.live

  override def spec: ZSpec[TestEnvironment, Any] = suite("ExampleProgram")(
    testM("test1") {
      val t = for {
        res <- MainProgram.testEcho("xxx")
      } yield {
        println(res)
        res.equalsIgnoreCase("test -> test|test")
      }
      assertM(t)(isTrue).provideLayer(layer)
    }
  )
}

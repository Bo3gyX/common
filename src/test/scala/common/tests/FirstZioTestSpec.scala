package common.tests

import zio._
import zio.test.Assertion._
import zio.test._
import zio.test.environment.TestEnvironment

object FirstZioTestSpec extends DefaultRunnableSpec {

  case class User(name: String, age: Int, isMan: Boolean)

  case class TestCase(name: String, user: User, cases: Seq[TestCase => ZSpec[TestEnvironment, Any]])

  val user1 = User("Вася", 17, isMan = true)
  val user2 = User("Петя", 15, isMan = false)
  val user3 = User("Петя", 15, isMan = false)

  val cases = Seq(
    TestCase("User success", user1, Seq(testNameSuccess, testAgeSuccess, testIsManSuccess)),
    TestCase("User failure", user2, Seq(testNameFailure, testAgeFailure, testIsManFailure)),
    TestCase("User name only", user1, Seq(testNameSuccess))
  )

  override def spec: ZSpec[TestEnvironment, Any] =
    suite("Test!")((for (ts <- cases) yield suite(ts.name)(ts.cases.map(_.apply(ts)): _*)): _*)

  def testNameSuccess(ts: TestCase) = testM("check name success") {
    val v = getName(ts).map(_ == "Вася")
    assertM(v)(isTrue)
  }

  def testNameFailure(ts: TestCase) = testM("check name failure") {
    val v = getName(ts).map(_ == "Вася")
    assertM(v)(isFalse)
  }

  def testAgeSuccess(ts: TestCase) = testM("check age success") {
    val v = getAge(ts).map(_ > 16)
    assertM(v)(isTrue)
  }

  def testAgeFailure(ts: TestCase) = testM("check age failure") {
    val v = getAge(ts).map(_ > 16)
    assertM(v)(isFalse)
  }

  def testIsManSuccess(ts: TestCase) = testM("check is man success") {
    val v = isMan(ts)
    assertM(v)(isTrue)
  }

  def testIsManFailure(ts: TestCase) = testM("check is man failure") {
    val v = isMan(ts)
    assertM(v)(isFalse)
  }

  def getName(ts: TestCase): Task[String] = zio.Task(ts.user.name)
  def getAge(ts: TestCase): Task[Int]     = zio.Task(ts.user.age)
  def isMan(ts: TestCase): Task[Boolean]  = zio.Task(ts.user.isMan)

}

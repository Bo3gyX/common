package util

import org.slf4j
import org.slf4j.LoggerFactory

trait Logger {
  val log: slf4j.Logger = LoggerFactory.getLogger(getClass)
}

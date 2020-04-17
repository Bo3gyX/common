package util.queue

import java.util.concurrent.atomic.AtomicReference

import util.Queue

import scala.collection.immutable

class MemoryQueue[T] extends Queue[T] {

  val queue: AtomicReference[immutable.Queue[T]] = new AtomicReference(immutable.Queue.empty)

  def appendedAll(e: T*): this.type = {
    queue.updateAndGet(_.appendedAll(e))
    this
  }

  override def appended(e: T): this.type = {
    queue.updateAndGet(_.appended(e))
    this
  }

  override def dequeue: T = {
    val (res, _) = queue.getAndUpdate { queue =>
      val (_, q) = queue.dequeue
      q
    }.dequeue
    res
  }

  override def size: Int = queue.get.size

  override def isEmpty: Boolean = queue.get().isEmpty

  override def nonEmpty: Boolean = queue.get().nonEmpty

  override def toString: String = queue.get.toString

}

object MemoryQueue {
  def apply[T](v: T*): MemoryQueue[T] = new MemoryQueue[T]().appendedAll(v: _*)
  def empty[T]: MemoryQueue[T]        = new MemoryQueue[T]()
}

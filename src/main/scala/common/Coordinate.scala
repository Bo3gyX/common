package common

object Coordinate {
  val iw = 256 * 3
  val ih = 256 * 4
  val tw = 256
  val th = 256

  println(iw)
  println(ih)

  val fx = iw / tw
  val fy = ih / th

  val frames = (fx * fy) - 1

  //crop=iw/3:ih/4:mod(n, 3)*iw/3:trunc(n/3)*ih/4

  for (f <- 0 to frames) {
    val mx = f % (iw / tw)
    val my = f / (iw / tw)
    val x = (f % (iw / tw)) * tw
    val y = (f / (iw / tw)) * th
    println(s"f: $f, mx: $mx, my: $my, x: $x, y: $y")
  }
}
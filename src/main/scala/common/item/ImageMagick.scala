package common.item

import java.io.File
import java.net.{URI, URL}
import java.nio.file.Paths

import org.im4java.core.{ConvertCmd, IMOperation, Info}
import org.im4java.utils.{FilenameLoader, FilenamePatternResolver}

import scala.jdk.CollectionConverters._

object ImageMagick {
  // create command// create command
  val cmd = new ConvertCmd

  def info(sourceFile: File): Unit = {
    val imageInfo = new Info(sourceFile.getAbsolutePath, true)
    System.out.println("Format: " + imageInfo.getImageFormat)
    System.out.println("Width: " + imageInfo.getImageWidth)
    System.out.println("Height: " + imageInfo.getImageHeight)
    System.out.println("Geometry: " + imageInfo.getImageGeometry)
    System.out.println("Depth: " + imageInfo.getImageDepth)
    System.out.println("Class: " + imageInfo.getImageClass)
  }

  def resize(sourceFile: File, targetFile: File, width: Int, height: Int): Unit = {
    val op = new IMOperation
    op.addImage(sourceFile.getAbsolutePath)
    op.resize(width, height)
    op.addImage(targetFile.getAbsolutePath)
    println(op)
    cmd.run(op)
  }

  def crop(sourceFile: File, targetFile: File, width: Int, height: Int): Seq[String] = {
    val ext = new FilenamePatternResolver("%e").createName(sourceFile.getAbsolutePath)
    val op  = new IMOperation
    op.addImage(sourceFile.getAbsolutePath)
    op.crop(width, height)
    op.set("filename:tile", s"%[fx:page.x/$width]-%[fx:page.y/$height]")
    op.addImage(targetFile.getAbsolutePath + "/%[filename:tile]." + ext)
    cmd.run(op)
    new FilenameLoader().loadFilenames(targetFile.getAbsolutePath).asScala.toList
  }

  def test: Unit = {
    val sourceFile = new File("/Users/ridrisov/work/html/src/img/tiles/rav4-hq.jpg")
    val targetFile = new File("/Users/ridrisov/work/html/src/img/tiles/rav4-hq-test.jpg")
    val targetDir  = new File("/Users/ridrisov/work/html/src/img/tiles/ffmpeg")
    val videoFile  = new File("/Users/ridrisov/Downloads/autoru_video_1594874213842.mp4")

    //      info(sourceFile)
    val res = crop(sourceFile, targetDir, 256, 256).map(fn => Paths.get(fn))
    for (r <- res){
      println("-------------")
      println(r.getFileName)
      println(r.getNameCount)
      println(r.getParent)
      println(r.getRoot)
    }
    //      resize(sourceFile, targetFile, , 256)

  }
}
package panoramas

import java.io.File

import org.im4java.core.{IMOperation, Info}
import panoramas.ImageMagick.{convert, montage}

object Simple2 {

  def cut(sourceFile: File, cropImages: File, width: Int, height: Int): Unit = {
    val op = new IMOperation
    op.addImage(sourceFile.getAbsolutePath)
    op.crop(width, height)
    op.addImage(cropImages.getAbsolutePath)
    convert(op)
  }

  def merge(leftImage: File, rightImage: File, mergeImage: File): Unit = {
    val op = new IMOperation
    op.addImage(rightImage.getAbsolutePath)
    op.mode("concatenate")
    op.tile(2)
    op.addImage(leftImage.getAbsolutePath)
    op.addImage(mergeImage.getAbsolutePath)
    montage(op)
  }

  def center(mergeImage: File, centerImage: File, width: Int, height: Int, x: Int, y: Int): Unit = {
    val op = new IMOperation
    op.addImage(mergeImage.getAbsolutePath)
    op.gravity("center")
    op.crop(width, height, x, y)
    op.addImage(centerImage.getAbsolutePath)
    convert(op)
  }

  def resize(centerImage: File, resizeImage: File, width: Int, height: Int): Unit = {
    val op = new IMOperation
    op.addImage(centerImage.getAbsolutePath)
    op.resize(width, height)
    op.addImage(resizeImage.getAbsolutePath)
    convert(op)
  }

  def make(names: String*): Unit = {
    val rootDir   = new File("/Users/ridrisov/my/common/panoramas/src/main/resources")
    val targetDir = new File(rootDir, "result")
    targetDir.mkdir()

    val cropImages = new File(targetDir, "crop_%d.jpg")
    val leftImage  = new File(targetDir, "crop_0.jpg")
    val rightImage = new File(targetDir, "crop_1.jpg")

    for (name <- names) {
      val sourceFile  = new File(rootDir, s"$name-hq.jpg")
      val mergeImage  = new File(targetDir, s"merge-$name.jpg")
      val centerImage = new File(targetDir, s"center-$name.jpg")
      val resizeImage = new File(targetDir, s"resize-$name.jpg")

      val info   = new Info(sourceFile.getAbsolutePath, true)
      val width  = info.getImageWidth
      val height = info.getImageHeight

      cut(sourceFile, cropImages, width / 2, height)
      merge(leftImage, rightImage, mergeImage)
      center(mergeImage, centerImage, width / 3, height / 3, 0, height / 3 / 4)
      resize(centerImage, resizeImage, 320, 240)
    }

  }

  def run(): Unit = make("rav4", "demo", "demo2")
}

package panoramas

import java.io.File

import org.im4java.core._
import org.im4java.utils.FilenameLoader
import util.Logger

import scala.jdk.CollectionConverters._

object ImageMagick extends Logger {

  def resize(sourceFile: File, targetFile: File, width: Int, height: Int): (Int, Int) = {
    val op = new IMOperation
    op.addImage(sourceFile.getAbsolutePath)
    op.resize(width, height)
    op.addImage(targetFile.getAbsolutePath)
    convert(op)
    val info = new Info(targetFile.getAbsolutePath, true)
    (info.getImageWidth, info.getImageHeight)
  }

  def crop(sourceFile: File, targetDir: File, width: Int, height: Int): Seq[File] = {
    val op = new IMOperation
    op.addImage(sourceFile.getAbsolutePath)
    op.crop(width, height)
    op.set("filename:tile", s"%[fx:page.x/$width]-%[fx:page.y/$height]")
    op.addImage(targetDir.getAbsolutePath + "/%[filename:tile].jpg")
    convert(op)
    new FilenameLoader().loadFilenames(targetDir.getAbsolutePath).asScala.toList.map(new File(_))
  }

  private val cmdConvert = new ConvertCmd()
  private val cmdMontage = new MontageCmd()

  def convert(operation: Operation): Unit = {
    exe(cmdConvert, operation)
  }

  def montage(operation: Operation): Unit = {
    exe(cmdMontage, operation)
  }

  private def exe(command: ImageCommand, operation: Operation): Unit = {
    log.info(s"${command.getCommand.asScala.mkString(" ")} ${operation.toString}")
    command.run(operation)
  }
}

package co.ledger.cal

import better.files._

object Utils {

  def unzip(file: File, targetFileName: String): Dispose[List[File]] = {
    val tmpDir = File.temporaryDirectory()
    tmpDir
      .map(file.unzipTo(_))
      .map(f => lookup(File(f.path)).filter(_.name == targetFileName))
  }

  def lookup(file: File): List[File] = {
    val c = if (file.isDirectory) file.children.toList else List.empty[File]
    c ++ c.flatMap(f => lookup(f))
  }

}

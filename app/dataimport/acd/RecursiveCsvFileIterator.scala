package dataimport.acd

import java.io.File
import collection.mutable
import mutable.{Stack => MStack}

class RecursiveCsvFileIterator(path: String) extends Iterator[File] {
  private var fileQueue: MStack[File] = null
  private var dirQueue: MStack[File] = null

  fileQueue = new MStack[File]
  dirQueue = new MStack[File]

  dirQueue push new File(path)

  def hasNext: Boolean = {
    if (!dirQueue.isEmpty) {
      scanPath()
    }

    !fileQueue.isEmpty
  }

  def next(): File = {
    if (!dirQueue.isEmpty) {
      scanPath()
    }

    fileQueue.pop()
  }

  @SuppressWarnings(Array("unchecked"))
  private def scanPath() {
    while (!dirQueue.isEmpty) {
      val dir: File = dirQueue.pop()
      scanDir(dir)
    }
  }

  private def scanDir(dir: File) {
    for (file <- dir.listFiles) {
      if (file.isDirectory) {
        dirQueue.push(file)
      }
      else if (file.isFile && file.getName.toLowerCase.endsWith(".csv")) {
        fileQueue.push(file)
      }
    }
  }
}

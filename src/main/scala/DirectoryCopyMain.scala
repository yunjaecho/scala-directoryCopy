import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.nio.file.StandardCopyOption._
import scala.annotation.tailrec

object DirectoryCopyMain {
  val SOURCE_FILE_PATH = "/home/comp1/copyTest/source/"
  val TARGER_FILE_PATH = "/home/comp1/copyTest/targer/"


  /**
    * 하위 폴더 포함 파일 목록
    * @param file
    * @return
    */
  def listAllFiles(file: File): Seq[File] = {
    if (file.isDirectory) {
      (file +: file.listFiles(_.isDirectory).toSeq.flatMap(x => listAllFiles(x))) ++ file.listFiles(_.isFile)
    } else {
      Seq(file)
    }
  }

  /*def listAllFiles2(file: File): Seq[File] = {
    def listAll(dirs: Seq[File], acc: Seq[File]): Seq[File] = dirs match {
      case x +: xs if x.isDirectory =>

    }
  }*/

  /**
    * java7 새로운 기능을 사용해서 폴더 복사(하위 폴더 포함)
    * Directory Copy (sub directory)
    * @param source
    * @param targer
    */
  def directoryCopy(source: Path, targer: Path): Unit = {
    Files
      // Return a Stream that is lazily populated with Path by walking the file tree rooted at a given starting file.
      .walk(source)
      .forEach(src => copy(src, targer.resolve(source.relativize(src))))


    def copy(source: Path, targer: Path): Unit = {
      Files.copy(source, targer, REPLACE_EXISTING)
    }
  }

  /**
    * 디렉토리 하위 포함 파일 및 디레토리 목록 위한
    * 꼬리 재귀 호출을 위한 object 생성을 위한 case class
    * @param dir
    * @param file
    */
  case class DirAndFiles(dir: Option[File], file: Seq[File])

  /**
    * 디렉토리 하위 포함 파일 및 디레토리 목록 위한 함수
    * @param file
    * @return
    */
  def getAllDirsAndFiles(file: File): Seq[DirAndFiles] = {
    @tailrec
    def getAll(dirs: Seq[File], acc: Seq[DirAndFiles]): Seq[DirAndFiles] = dirs match {
      case x +: xs =>
        getAll(
          xs ++ x.listFiles(_.isDirectory),
          acc :+ DirAndFiles(Some(x), x.listFiles(_.isFile).toSeq)
        )
      case Seq() =>
        acc
    }

    if (file.isDirectory)
      getAll(Seq(file), Seq.empty)
    else
      Seq(DirAndFiles(None, Seq(file)))

  }

  /**
    * 파일 복사하기
    * @param source
    * @param targer
    * @return
    */
  def copy(source: File, targer: File) = {
    val allDirAndFiles = getAllDirsAndFiles(source)
    for {
      DirAndFiles(dir, files) <- allDirAndFiles
      dirPath = dir.fold("")(_.getCanonicalPath.drop(source.getCanonicalPath.length + 1))
      targetDir = new File(targer, dirPath)
      _ = targetDir.mkdirs
      file <- files
      copiedFile = Files.copy(file.toPath, new File(targetDir, file.getName).toPath)
    } yield copiedFile.toFile
  }"finish"//


  def main(args: Array[String]): Unit = {
    val source = Paths.get(SOURCE_FILE_PATH)
    val targer = Paths.get(TARGER_FILE_PATH)

    //directoryCopy(Paths.get(SOURCE_FILE_PATH), Paths.get(TARGER_FILE_PATH))

    //listAllFiles(new File(SOURCE_FILE_PATH))
    //  .foreach(println)

    //directoryCopy(Paths.get(SOURCE_FILE_PATH), Paths.get(TARGER_FILE_PATH))

    //getAllDirsAndFiles(new File(SOURCE_FILE_PATH)).foreach(println)


    copy(new File(SOURCE_FILE_PATH), new File(TARGER_FILE_PATH))

 }


}

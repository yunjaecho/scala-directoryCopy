# Directory 폴더 및 파일 복사

java.nio.Files copy 메소드는 하위 경로에 디렉토리의 파일은 복사하지 않는다.

그래서 별도의 구현이 필요한다.


def directoryCopy(source: Path, targer: Path): Unit = {
 Files
   // Return a Stream that is lazily populated with Path by walking the file tree rooted at a given starting file.
   .walk(source)
   .forEach(src => copy(src, targer.resolve(source.relativize(src))))


 def copy(source: Path, targer: Path): Unit = {
   Files.copy(source, targer, REPLACE_EXISTING)
 }
}

위에 소스를 보면 java.nio.Files.walk(path: Path) 메서드가 해당 경로에 있는 하위 디렉토리 포함해서 
파일 목록을 Stream<Path> 으로 return 한다.

해당 스트림을 순회하면서 파일을 복사한다.

Constructs a relative path between this path and a given path.
Relativization is the inverse of resolution. 
This method attempts to construct a relative path that when resolved against this path, 
yields a path that locates the same file as the given path. For example, 
on UNIX, if this path is "/a/b" and the given path is "/a/b/c/d" then 
the resulting relative path would be "c/d". 
Where this path and the given path do not have a root component, 
then a relative path can be constructed. 
A relative path cannot be constructed if only one of the paths have a root component. 
Where both paths have a root component then it is implementation dependent 
if a relative path can be constructed. If this path and the given path are equal then 
an empty path is returned.

source.relativize(src) : source 패스와 src 지정된 패스와의 사이의 상대 패스를 구축합니다.

Resolve the given path against this path.
If the other parameter is an absolute path then this method trivially returns other. 
If other is an empty path then this method trivially returns this path. 
Otherwise this method considers this path to be a directory and resolves the given path against this path. 
In the simplest case, the given path does not have a root component, 
in which case this method joins the given path to this path and 
returns a resulting path that ends with the given path. 
Where the given path has a root component then resolution is highly implementation dependent and 
therefore unspecified.

targer.resolve(source.relativize(src))) : source 패스에 대해 src 지정된 패스를 해결합니다.


함수형의 하위 디렉토리 포함한 목록 조회
디렉토리 구조가 매우 깊은 경우 스택오버플로어 오류 발생할수 있음
flatMap 처리

def listAllFiles(file: File): Seq[File] = {
  if (file.isDirectory) {
    (file +: file.listFiles(_.isDirectory).toSeq.flatMap(x => listAllFiles(x))) ++ file.listFiles(_.isFile)
  } else {
    Seq(file)
  }
}

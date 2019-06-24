
/**
  * Option[A] 是一个可能有值也可能没值的容器，
  * Try[A] 则表示一种计算：这种计算在成功的情况下，返回类型为A的值，在出错的情况下，返回 Throwable 。
  * 这种可以容纳错误的容器可以很轻易的在并发执行的程序之间传递。
  * Try 有两个子类型：
  * Success[A]：代表成功的计算。
  * 封装了 Throwable 的 Failure[A]：代表出了错的计算。
  */
object program_gitbook_try {

  import scala.util.Try
  import java.net.URL
  def parseURL(url: String): Try[URL] = Try(new URL(url))

  //try的使用，isSuccess 检查是否成功
  val url = parseURL(Console.readLine("URL: ")) getOrElse new URL("http://duckduckgo.com")


  /**
    * 链式操作，Try 最重要的特征是，它也支持高阶函数，就像 Option 一样。 在下面的示例中，你将看到，在 Try 上也进行链式操作，捕获可能发生的异常，而且代码可读性不错。
    */

  /**
    * Mapping 和 Flat Mapping
    */
  parseURL("http://danielwestheide.com").map(_.getProtocol)
  // results in Success("http")
  parseURL("garbage").map(_.getProtocol)
  // results in Failure(java.net.MalformedURLException: no protocol: garbage)

  //嵌套的try结构，不是我们想要的
  import java.io.InputStream
  def inputStreamForURL(url: String): Try[Try[Try[InputStream]]] = parseURL(url).map { u =>
    Try(u.openConnection()).map(conn => Try(conn.getInputStream))
  }

  //flatMap重写上面的例子，最终的结果可能是Success，也可能是Failure
  def inputStreamForURL1(url: String): Try[InputStream] =
    parseURL(url).flatMap { u =>
      Try(u.openConnection()).flatMap(conn => Try(conn.getInputStream))
    }

  /**
    * 过滤器和 foreach
    */

  //filter
  //当一个Try已经是Failure了，或者传递给它的谓词函数返回假值，filter就返回Failure（如果是谓词函数返回假值，那 Failure 里包含的异常是 NoSuchException ）
  //否则的话， filter 就返回原本的那个 Success ，什么都不会变：
  def parseHttpURL(url: String) = parseURL(url).filter(_.getProtocol == "http")
  parseHttpURL("http://apache.openmirror.de") // results in a Success[URL]
  parseHttpURL("ftp://mirror.netcologne.de/apache.org") // results in a Failure[URL]

  //foreach
  //当 Try 是 Failure 时， foreach 不会执行，返回 Unit 类型。
  parseHttpURL("http://danielwestheide.com").foreach(println)


  /**
    * for 语句中的 Try
    * 代码的可读性会更高
    */

  import scala.io.Source
  //返回指定网页的内容
  def getURLContent(url: String): Try[Iterator[String]] =
    for {
      url <- parseURL(url)
      connection <- Try(url.openConnection())
      is <- Try(connection.getInputStream)
      source = Source.fromInputStream(is)
    } yield source.getLines()


  /**
    * 模式匹配
    * Success 和 Failure 都是样例类。
    */
  import scala.util.Success
  import scala.util.Failure
  getURLContent("http://danielwestheide.com/foobar") match {
    case Success(lines) => lines.foreach(println)
    case Failure(ex) => println(s"Problem rendering URL content: ${ex.getMessage}")
  }

  /**
    * 从故障中恢复 recover
    * 接受一个偏函数，并返回另一个 Try。
    * 如果 recover 是在 Success 实例上调用的，那么就直接返回这个实例，否则就调用偏函数
    */

  import java.net.MalformedURLException
  import java.io.FileNotFoundException

  val content = getURLContent("garbage") recover {
    case e: FileNotFoundException => Iterator("Requested page does not exist")
    case e: MalformedURLException => Iterator("Please make sure to enter a valid URL")
    case _ => Iterator("An unexpected error has occurred. We are so sorry!")
  }
  //此时可以在返回值 content 上安全的使用 get 方法了，因为它一定是一个 Success

}

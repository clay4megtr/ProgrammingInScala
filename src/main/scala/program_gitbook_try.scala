
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

  //Mapping 和 Flat Mapping
  parseURL("http://danielwestheide.com").map(_.getProtocol)
  // results in Success("http")
  parseURL("garbage").map(_.getProtocol)
  // results in Failure(java.net.MalformedURLException: no protocol: garbage)

  //嵌套的try结构，不是我们想要的
  import java.io.InputStream
  def inputStreamForURL(url: String): Try[Try[Try[InputStream]]] = parseURL(url).map { u =>
    Try(u.openConnection()).map(conn => Try(conn.getInputStream))
  }

  //flatMap重写上面的例子
  def inputStreamForURL1(url: String): Try[InputStream] =
    parseURL(url).flatMap { u =>
      Try(u.openConnection()).flatMap(conn => Try(conn.getInputStream))
    }

}


object program_gitbook_Either {

  /**
    * 创建 Either
    */
  import scala.io.Source
  import java.net.URL
  def getContent(url: URL): Either[String, Source] =
    if(url.getHost.contains("google"))
      Left("Requested URL is blocked for the good of the people!")
    else
      Right(Source.fromURL(url))


  /**
    * 用法:模式匹配
    * 调用 isLeft （或 isRight ）方法询问一个 Either,判断它是 Left 值，还是 Right 值
    */
  getContent(new URL("http://google.com")) match {
    case Left(msg) => println(msg)
    case Right(source) => source.getLines.foreach(println)
  }

  /**
    * 立场(映射)
    * Either 是 无偏(unbiased) 的。
    * Try 偏向 Success：map、flatMap以及其他一些方法都假设 Try 对象是一个 Success 实例， 如果是 Failure，那这些方法不做任何事情，直接将这个 Failure 返回。
    * 调用 left 或 right 方法，就能得到 Either 的 LeftProjection 或 RightProjection实例， 这就是 Either 的 立场(Projection) ，它们是对 Either 的一个左偏向的或右偏向的封装。
    */
  val content: Either[String, Iterator[String]] =
    getContent(new URL("http://danielwestheide.com")).right.map(_.getLines())
  // content is a Right containing the lines from the Source returned by getContent

  //注意，map方法是定义在 Projection 上的，而不是 Either，但其返回类型是 Either（重要），而不是 Projection。
  val moreContent: Either[String, Iterator[String]] =
    getContent(new URL("http://google.com")).right.map(_.getLines)  //返回的是Left，所以 .right.map(_.getLines) 不会执行, 直接返回Left的String字符串；
  // moreContent is a Left, as already returned by getContent

  // content: Either[String,Iterator[String]] = Right(non-empty iterator)
  // moreContent: Either[String,Iterator[String]] = Left(Requested URL is blocked for the good of the people!)


  val content1: Either[Iterator[String], Source] =
    getContent(new URL("http://danielwestheide.com")).left.map(Iterator(_))
  // content is the Right containing a Source, as already returned by getContent
  val moreContent1: Either[Iterator[String], Source] =
    getContent(new URL("http://google.com")).left.map(Iterator(_))
  // moreContent is a Left containing the msg returned by getContent in an Iterator

  // content: Either[Iterator[String],scala.io.Source] = Right(non-empty iterator)
  // moreContent: Either[Iterator[String],scala.io.Source] = Left(non-empty iterator)

  /**
    * Flat Mapping
    */
  //计算两篇文章的平均行数
  //返回的结果值类型为 Either[String, Either[String, Int]]
  val part5 = new URL("http://t.co/UR1aalX4")
  val part6 = new URL("http://t.co/6wlKwTmu")
  val content2 = getContent(part5).right.map(a =>
    getContent(part6).right.map(b =>
      (a.getLines().size + b.getLines().size) / 2))
  // => content: Product with Serializable with scala.util.Either[String,Product with Serializable with scala.util.Either[String,Int]] = Right(Right(537))

  //使用flatmap来对 里层Either的值 自动解包
  val content3 = getContent(part5).right.flatMap(a =>
    getContent(part6).right.map(b =>
      (a.getLines().size + b.getLines().size) / 2))
  // => content: scala.util.Either[String,Int] = Right(537)


  /**
    * for语句
    * 在不同类型上的一致性表现很好用
    */
  //重写上面的例子
  def averageLineCount(url1: URL, url2: URL): Either[String, Int] =
    for {
      source1 <- getContent(url1).right
      source2 <- getContent(url2).right
    } yield (source1.getLines().size + source2.getLines().size) / 2

  //无法编译
  /*def averageLineCountWontCompile(url1: URL, url2: URL): Either[String, Int] =
    for {
      source1 <- getContent(url1).right
      source2 <- getContent(url2).right
      lines1 = source1.getLines().size
      lines2 = source2.getLines().size
    } yield (lines1 + lines2) / 2*/

  //上面的代码实际上执行的是
  /*def averageLineCountDesugaredWontCompile(url1: URL, url2: URL): Either[String, Int] =
    getContent(url1).right.flatMap { source1 =>
      getContent(url2).right.map { source2 =>   //这个map 返回的是Either
        val lines1 = source1.getLines().size
        val lines2 = source2.getLines().size
        (lines1, lines2)
      }.map { case (x, y) => x + y / 2 }  //在 for 语句中追加新的值定义会在前一个 map 调用上自动引入另一个 map 调用， 前一个 map 调用返回的是 Either 类型，不是 RightProjection 类型， 而 Scala 并没有在 Either 上定义 map 函数，因此编译时会出错
    }*/

  //解决办法
  def averageLineCount1(url1: URL, url2: URL): Either[String, Int] =
    for {
      source1 <- getContent(url1).right
      source2 <- getContent(url2).right
      lines1 <- Right(source1.getLines().size).right
      lines2 <- Right(source2.getLines().size).right
    } yield (lines1 + lines2) / 2

  /**
    * 何时使用Either
    */
  //错误处理
  import scala.util.control.Exception.catching
  def handling[Ex <: Throwable, T](exType: Class[Ex])(block: => T): Either[Ex, T] =
    catching(exType).either(block).asInstanceOf[Either[Ex, T]]




}
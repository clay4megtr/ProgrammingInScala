import java.io.{File, FileNotFoundException, FileReader, IOException}
import java.net.{MalformedURLException, URL}

import scala.actors.migration.pattern
import scala.io.Source

/**
  * 尽可能寻找使用val的机会，他们能让你的代码既容易阅读，又容易重构
  *
  * >>>>>>>>>>>>>>>>>>>>>尽量让函数没有副作用，就是说不产生打印，写入文件等任何操作，只返回这个函数应该得到的计算结果，<<<<<<<<<<<<<<<<<<
  * >>>>>>>>>>>>>>>多用mkString<<<<<<<<<<<<
  *
  */
object program_7 {

  def main(args: Array[String]): Unit = {

    var filename = "default.txt"
    if (!args.isEmpty)
      filename = args(0)

    /**
      * 几乎所有的scala的控制结构都会产生某个值，这是函数式语言所采用的方式
      */
    val filename1 = if (!args.isEmpty) args(0) else "default.txt"

    //while
    def gcdLoop(x: Long, y: Long): Long = {
      var a = x
      var b = y

      while (a != 0) {
        val temp = a
        a = b % a
        b = temp
      }
      b
    }

    //do while
    var line = ""
    do {
      line = Console.readLine()
      println("Read: " + line)
    } while (line != "")


    /**
      * unit 值 的特殊性
      */
    def greet() {
      println("hi")
    }

    val flag = greet() == () //返回true
    println(flag)

    /**
      * 下面这个是不起作用的，
      * 对var再赋值等式本身也是unit值，也就是说 (line1 = Console.readLine()) 的返回值永远是unit类型的
      * 而 != 比较类型为unit 和String 的值将永远返回true
      * 造成死循环
      */
    var line1 = ""
    /*while((line1 = Console.readLine()) != "")   //不起作用
      println("Read: " + line1)*/

    val fileHere = (new File(".")).listFiles

    for (file <- fileHere) { //发生器的语法，file是val类型的变量
      println(file)
    }

    /**
      * for 表达式语法对任何种类的集合类都有效
      */
    for (i <- 1 to 4) {
      println(i)
    }

    for (i <- 1 until 4) {
      println(i)
    }


    /**
      * 可以很容易的过滤出某个子集,多个判断条件之间用;相隔    过滤器
      */
    for (file <- fileHere if file.isFile; if file.getName.endsWith(".scala")) {

      println(file)
    }


    /**
      * 嵌套枚举
      */
    def fileLines(file: File) = Source.fromFile(file).getLines().toList

    def grep(pattern: String) =
      for (file <- fileHere if file.getName.endsWith(".scala")) {
        for (line <- fileLines(file) if line.matches(pattern))
          println(file + ": " + line.trim)
      }

    grep(".*gcd.*")


    /**
      * 制造新集合
      * for循环的过程中产生新值
      * yield
      *
      * for {子句} yield {循环体}
      */
    def scalaFiles =
      for (
        file <- fileHere
        if file.getName.endsWith(".scala")
      ) yield file

    for (file <- fileHere if file.getName.endsWith(".scala")) {
      //yield file  语法错误
    }


    val forLineLengths =
      for (file <- fileHere if file.getName.endsWith(".scala"))
        for (line <- fileLines(file) if line.trim.matches(".*for*.")) yield line.length


    /**
      * try catch  异常处理
      *
      * 异常将在half被初始化之前被抛出，
      * 异常类型是  Nothing 类型
      *
      * 整个if表达式的类型就是   那个实际计算值得分支   的类型
      */
    val n = 4
    val half =
      if (n % 2 == 0)
        n / 2
      else
        throw new RuntimeException("n must be even")

    println(half)


    /**
      * 和 =====模式匹配=====  能保持一致
      */
    try {
      val f = new FileReader("input.txt")
      //使用并关闭
    } catch {
      case ex: FileNotFoundException => //处理
      case ex: IOException => //
    }

    //finally
    /*val file = new FileReader("input.txt")
    try{
      //使用
    }finally {
      file.close()
    }*/

    //try catch finally 也产生值
    def urlFor(path: String) =
      try {
        new URL(path)
      } catch {
        case e: MalformedURLException => new URL("www.baidu.com")
      }


    //======================================================================

    // 匹配表达式

    //======================================================================

    // 类似switch
    val firstArg = if (args.length > 0) args(0) else ""

    firstArg match {
      case "Salt" => println("papper")
      case "chips" => println("salsa")
      case "eggs" => println("bacon")
      case _ => print("huh?")
    }

    //任何类型的变量 都能当成 scala 里做比较用得case

    //默认含有break

    //match 表达式可以产生值
    val frend =
      firstArg match {
        case "Salt" => "papper"
        case "chips" => "salsa"
        case "eggs" => "bacon"
        case _ => "huh?"
      }

    println(frend)


    //======================================================================

    // 不再使用 break 和 continue

    //======================================================================

    //scala 去掉了break 和 continue

    // 充分利用函数字面量加以替代，可以写出更简单的代码

    var i = 0
    var foundIt = false

    while (i < args.length && !foundIt) {
      if (!args(i).startsWith("-")) { // 不以 - 做前缀
        if (args(i).endsWith(".scala")) // 以 .scala 结尾
          foundIt = true
      }
      i = i + 1
    }

    //使用递归的方式去掉var
    //每个 continue 都被带有 i+1 做参数的递归调用替换掉
    def searchFrom(i: Int): Int =
      if (i >= args.length) -1
      else if (args(i).startsWith("-")) searchFrom(i + 1)
      else if (args(i).endsWith(".scala")) i
      else searchFrom(i + 1)

    val j = searchFrom(0)




    //======================================================================

    // 变量范围

    //======================================================================

    //scala 允许 在嵌套范围内定义同名变量

    // java不允许在内部范围内创建与外部范围变量同名的变量

    // scala 程序中，内部变量被认为遮蔽了同名的外部变量，因为在内部范围中外部变量变得不可见

    val a = 1
    /*{
      val a = 2   // 这样定义是允许的
      println(a)
    }*/
    println(a)




    //======================================================================

    // 重构指令式风格的代码

    //======================================================================

    // 打印99 乘法表     指令式
    def printMultiTable(): Unit = {

      var i = 1
      while (i <= 10) {

        var j = 1
        while (j <= 10) {

          val prod = (i * j).toString

          var k = prod.length

          while (k < 4) {
            print(" ")
            k += 1
          }

          print(prod)
          j += 1
        }

        println()
        i += 1
      }
    }

    printMultiTable()


    //函数式
    def makeRowSeq(row: Int) =
      for (col <- 1 to 10) yield {
        val prod = (row * col).toString
        val padding = " " * (4 - prod.length)
        padding + prod
      }

    //以字符串形式返回一行乘法表
    def makeRow(row: Int) = makeRowSeq(row).mkString("")

    //以字符串形式返回乘法表，每行记录占一行字符串
    def multiTable() = {

      val tableSeq =    //行记录字符串的序列
        for (row <- 1 to 10)
          yield makeRow(row)

      tableSeq.mkString("\n")
    }

    println()
    println(multiTable())

  }
}

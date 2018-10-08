import java.io.{File, PrintWriter}

/**
  * 控制抽象
  */
object program_9 {


  /**
    * ===========================减少代码重复===========================
    */

  private def filesHere = (new File(".")).listFiles

  //找到以 query 结尾的文件
  def filesEnding(query:String) =
    for(file <- filesHere if file.getName.endsWith(query)) yield file

  //找到包含 query 的文件
  def filesContaining(query:String) =
    for(file <- filesHere if file.getName.contains(query)) yield file

  //regex
  def fileRegex(query:String) =
    for(file <- filesHere if file.getName.matches(query)) yield file


  //高级函数的写法：减少代码量
  def filesMatching(query:String,
                    matcher: (String,String) => Boolean) = {
    for(file <- filesHere if matcher(file.getName,query)) yield file
  }

  //新的写法
  //等价写法  def filesEnding1(query:String) = filesMatching(query,((fileName,query) => fileName.endsWith(query)))
  //由于第一个参数在方法体中被第一个使用，第二个参数在方法体中被第二个使用，所以可以写成：  _.endsWith(_)
  def filesEnding1(query:String) = filesMatching(query,_.endsWith(_))
  def filesContaining1(query:String) = filesMatching(query,_.contains(_))
  def fileRegex1(query:String) = filesMatching(query,_.matches(_))

  //query  也可以省略掉，因为filesMatching 中并没有使用query，只是把它传给matcher 函数，这个过程不是必须的
  //因为调用者在前面就已经知道了query的内容
  //所以：最终的写法如下：
  def filesMatching1(matcher: String => Boolean) = {
    for(file <- filesHere if matcher(file.getName)) yield file
  }

  def filesEnding2(query:String) = filesMatching1(_.endsWith(query))
  def filesContaining2(query:String) = filesMatching1(_.contains(query))
  def fileRegex2(query:String) = filesMatching1(_.matches(query))



  /**
    * ===========================减少客户代码===========================
    * 提供了很多的高级函数，不用自己一直用while for等循环实现
    */

  def containNeg(nums:List[Int])= nums.exists(_<0)

  println(containNeg(List(0,-1,2)))


  /**
    * ==========================柯里化===========================
    * 柯里化被应用于多个参数列表，而不是一个
    */

  def plainOldSum(x:Int,y:Int) = x+y

  println(1,2)  //3

  //柯里化后
  def curriedSum(x:Int)(y:Int) = x + y

  //调用时，实际是接连调用了两个传统函数，第一个函数调用带单个的名为x的Int参数，并返回第二个函数的函数值，第二个函数带Int类型参数y
  println(curriedSum(1)(2))


  //first 函数实质上执行了curriedSum的第一个传统函数会做的事情，返回了一个函数值，
  def first(x:Int) = (y:Int) => x + y

  val second = first(1) //产生第二个函数

  second(2)  //执行第二个函数，产生结果3


  //以上两个函数只是模拟，实际可以通过偏函数的方式获得第产生的二个函数,这里的_  代表第二个参数
  val onePlus = curriedSum(1)_

  onePlus(2) //返回3



  /**
    * =======================编写新的控制结构：借贷模式===========================
    */

  def twice(op:Double => Double,x:Double) = op(op(x))

  twice(_+1,5)  //7


  //把需要操作的文件和要对这个文件做的操作传进去
  //好处是由此函数确认文件被关闭，不可能忘记关闭文件，
  //这个技巧叫做：借贷模式，withPrintWriter打开资源，借给op函数，等op函数操作完后，不管op的执行情况，都会收回并关闭资源
  def withPrintWriter(file:File,op:PrintWriter => Unit): Unit ={
    val writer = new PrintWriter(file)
    try{
      op(writer)
    }finally {
      writer.close()
    }
  }

  //使用
  withPrintWriter(new File("data.txt"),
    writer => writer.println(new java.util.Date())
  )


  //如果函数只有一个参数，可以使用{} 代替（）
  println("hello world")
  println{"hello world"}

  //引入 {} 的目的是让客户程序员写出包围在花括号内的函数字面量，这可以让方法调用感觉更像控制抽象
  //柯里化
  def withPrintWriter1(file:File)(op:PrintWriter => Unit): Unit ={
    val writer = new PrintWriter(file)
    try{
      op(writer)
    }finally {
      writer.close()
    }
  }

  //则调用方法可以写为
  val file = new File("data.txt")
  withPrintWriter1(file){     //withPrintWriter(file) 就返回了一个只带一个函数值做参数的函数，就可以使用{} 代替 ()
    writer => writer.println(new java.util.Date())
  }


  /**
    * =======================传名参数===========================
    */

  //根据标志位实现的断言(不使用传名参数)
  var assertionsEnabled = true

  def myAssert(predicate: () => Boolean) =
    if(assertionsEnabled && !predicate())
      throw new AssertionError()

  myAssert(() => 5 > 3)  //正确，但难看

  //myAssert(5 > 3)  //错误

  // 传名参数 ( 去掉() )
  def byMyAssert(predicate: => Boolean) =
    if(assertionsEnabled && !predicate)
      throw new AssertionError()

  byMyAssert(5 > 3)   //正确


  //为什么不写成如下的形式
  def boolAssert(predicate: Boolean) =
    if(assertionsEnabled && !predicate)
      throw new AssertionError()

  boolAssert(5 > 3)

  //两种形式的差别
  /**
    * 因为boolAssert的参数类型是Boolean，所以boolAssert(5 > 3) 括号中的表达式先于boolAssert的调用被评估，表达式5>3产生true，传递给boolAssert，
    *
    * 而byMyAssert的参数类型是predicate: => Boolean，(5 > 3)表达式不是先于byMyAssert的调用别评估的，而是代之以先创建一个函数值，
    * 其apply方法将评估5>3 ，而这个函数值将传递给byMyAssert
    */

  assertionsEnabled = false
  boolAssert(3/0 == 0)    //会产生除数不能为0的异常

  byMyAssert(3/0 == 0)     //正常执行，因为判断assertionsEnabled为false后，函数值根本没有执行

  def main(args: Array[String]): Unit = {

  }
}

import java.net.FileNameMap

import scala.io.Source

/**
  * 函数和闭包
  */

object program_8 {

  /**
    * 函数类型：
    *
    * 对象成员
    * 内嵌在函数中的函数
    * 函数字面量<<<<<<重要
    * 函数值
    */


  /**
    * 对象成员  函数
    * 方法
    */
  def processFile(fileName: String, width: Int): Unit = {

    val source = Source.fromFile(fileName)
    for (line <- source.getLines()) {
      processLine(fileName, width, line)
    }
  }

  private def processLine(fileName: String, width: Int, line: String) {

    if (line.length > width) {
      println(fileName + ": " + line.trim)
    }
  }

  /**
    * 本地函数：
    * 函数私有化
    * 目的是：帮助函数的名称可能污染程序的命名空间(下次定义其他函数可能造成重名)；让方法的调用者对帮助函数不可见
    * java：私有方法；
    * scala：私有方法（如上）
    * scala：本地函数（帮助函数定义在主函数之内）类似于本地变量
    *
    * 本地函数可以无条件的访问包含其函数的参数，所以可以去掉 fileName 和 width 参数
    */
  def processFile1(fileName: String, width: Int): Unit = {

    def processLine(line: String) {

      if (line.length > width) {
        println(fileName + ": " + line.trim)
      }
    }

    val source = Source.fromFile(fileName)
    for (line <- source.getLines()) {
      processLine(line)
    }
  }

  /**
    * 头等函数
    * 不仅可以定义和调用函数，还可以把他们写成匿名的字面量，并把他们作为值传递
    *
    * 字面量：字面量是指由字母，数字等构成的字符串或者数值，它只能作为右值出现,(右值是指等号右边的值，如：int a=123这里的a为左值，123为右值。)
    *
    * 函数字面量：
    * 函数字面量被编译进类，并在运行期间实例化为函数值，因此 函数字面量 和 函数值 的区别在于函数字面量存在于源代码，
    * 而函数值作为对象存在于运行期；
    * 这个区别很像类（源代码）和对象（运行期）之间的关系
    */

  (x: Int) => x + 1 //函数字面量

  var increase = (x: Int) => x + 1  //把函数字面量赋值给变量increase

  increase(1) //函数的调用

  //例如：foreach
  val someNumbers = List(-11, -10, -5, 0, 5, 10)

  someNumbers.foreach((x: Int) => println(x))

  someNumbers.filter((x: Int) => x > 0)


  /**
    * 函数字面量的短格式（也就是更简单的写法）
    */
  someNumbers.filter((x) => x>0)

  someNumbers.filter(x => x> 0)


  /**
    * 占位符语法
    * 要求：每个参数在函数字面量仅出现一次
    */
  someNumbers.filter(_>0)

  //如果编译器无法推断缺失的参数类型
  val f = (_:Int) + (_:Int)

  f(5,10)


  /**
    * 部分应用函数：偏函数
    * 可以使用单个下划线替换整个参数列表 的函数
    *
    * 部分应用函数是一种表达式，你不需要提供函数所需要的所有参数，代之以提供部分，或不提供所需参数
    *
    */
  someNumbers.foreach(println _)  //_ 是整个参数列表的占位符，但是要在函数名和下划线之间留一个空格


  /**
    * scala中：当你调用函数，传入任何需要的参数，实际是在把函数应用到参数上；
    */
  def sum(a: Int, b:Int, c:Int) = a+b+c

  //把函数应用到1，2，3上
  sum(1,2,3)

  /**
    * sum的部分应用表达式， 只需要在 sum 之后放一个_即可
    */
  val a = sum _

  a(1,2,3)

  /**
    * 发生的事情：名为a的变量指向一个函数值对象，这个函数值是由scala编译器依照部分应用表达式sum_，自动产生的类的一个实例，
    * 编译器产生的类有一个apply方法带3个参数，之所以带3个参数是因为sum_表达式缺少的参数数量为3，scala编译器把表达式
    * a(1,2,3) 翻译成对函数值（也就是scala的自动产生的带apply()方法的类）的apply 方法的调用，传入3个参数1，2，3，因此a(1,2,3)是下列代码的短格式
    *
    * scala编译器根据表达式sum_ 自动产生的类的apply方法，简单的把这3个缺失的参数前转到sum，并返回结果
    */
  a.apply(1,2,3)

  /**
    * 这种一个下划线代表全部参数列表的表达式的另一种用途，就是把它当做转换def为函数值的方式。
    * 例如，一个本地函数 sum(a:Int,b:Int): Int，你可以把它 "包装" 成与 apply 方法具有同样的参数列表和结果类型的函数值，
    * 当你把这个函数值应用到某些参数上时，它依次把sum应用到同样的参数，并返回结果，
    * 尽管不能把方法或嵌套函数赋值给变量，或当做参数传递给其他方法，但如果你通过在名称后面加下划线的方式把方法或嵌套函数包装在函数值中，就可以做到了；
    */

  //val g = processFile     这种写法是错误的，因为不能把方法或嵌套函数赋值给变量，这是不允许的
  val g = processFile _    // 这种写法是正确的，相当于把processFile 转化成了函数值，就可以赋值给变量了


  /**
    * 还可以通过提供某些但不是全部需要的参数表达一个偏函数
    * 这个偏函数产生的类的apply()函数就只带一个参数，相当于另外两个参数变成了默认函数
    */
  val b = sum(1,_:Int,3)

  println(b(2))  //实际执行1+2+3=6,b.apply(1,2,3)

  println(b(5))  //实际执行1+5+3=9,b.apply(1,5,3)


  /**
    * 如果你正在写一个省略所有参数的偏程序表达，如 println _ 或 sum _ ，而且在代码的某个地方确实需要一个函数
    * 可以去掉下划线从而得到更简单的声明
    */
  someNumbers.foreach(println _)

  someNumbers.foreach(println)


  /**
    * 闭包
    * 依照这个函数字面量在运行时创建的函数值(对象)称为闭包
    * (x:Int) => x+1  不是闭包，因为这个字面量在编写的时候已经封闭了，
    * 而任何带有自由变量的含数字面量，如 (x:Int) => x+more 都是开放项，
    * 因此任意以(x:Int) => x+more 为模板在运行期间创建的函数值都必将捕获对自由变量more 的绑定，因此得到的函数值都必将包含指向捕获的more 变量的索引
    */

  var more = 1
  val addMore = (x:Int) => x+more


  more = 9999
  println(addMore(10))  //10009


  //闭包对捕获变量做出的改变在闭包之外也同样可见
  var sum1 = 0
  someNumbers.foreach(sum1 += _)

  println(sum1) //-15

  /**
    * 如果闭包访问了某些在程序运行时有若干不同备份的变量，比如闭包使用了某个函数的本地变量，
    * 并且函数被调用很多次会怎样，每一次访问使用的是变量的哪个实例
    *
    * ：使用的实例是那个在闭包被创建的时候活跃的
    */

  def makeIncrease(more:Int) = (x:Int) => x+more

  val incr1 = makeIncrease(1)
  val incr2 = makeIncrease(9999)

  //当把这些闭包应用到参数上时，回来的结果依赖于闭包被创建时more是如何创建的
  incr1(10) // 11
  incr2(10)  //10009


  /**
    * 重复参数
    * 函数内部的类型是声明参数类型的数组，因此，这里实际是：Array[String]
    */
  def echo(args:String*) =
    for (arg <- args) println(arg)

  echo("one")
  echo("one","two")

  val arr = Array("one","two","three")

  //错误写法
  //echo(arr)

  //正确写法：告诉编译器把arr的每个元素当做参数，而不是一个array类型的参数
  echo(arr: _*)


  /**
    * 尾递归（）
    * 在函数最后一个动作调用调用自己的函数，被称为尾递归
    * scala编译器检测到尾递归就用新值更新函数参数，然后把它替换成一个回到函数开头的跳转
    */
  def approximate(guess: Double):Double =
    if(isGoodEnough(guess)) guess
    else approximate(improve(guess))

  //辅助函数
  def isGoodEnough(x:Double) = true
  def improve(x:Double):Double = 0.0


  //while 循环的方式
  def approximateLoop(initialGuess:Double):Double = {
    var guess = initialGuess
    while(!isGoodEnough(guess))
      guess = improve(guess)
    guess
  }
}

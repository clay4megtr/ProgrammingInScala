
/**
  * 函数字面量；也就是函数的定义
  *
  * 函数的每个参数都必须指定类型
  * i++ 和++i  是不起作用的
  * while 和 if 的判断条件必须写在 () 中，代码块只有一句可以不写{}
  * 逐条执行指令，循环枚举，都被称为 指令式的编程风格，
  *
  */
object program_2 {

  def main(args: Array[String]): Unit = {

    // 函数是头等结构，
    args.foreach((arg:String) => println(arg))
    args.foreach(arg => println(arg)) //类型推断
    args.foreach(println)   //函数字面量只有一行语句并且只带一个参数

    for (arg <- args)       //注意： arg 是val 是不可变的
      println(arg)
  }
}


/**
  * 第十一章：scala的层级
  *
  * Any是所有其他类的超类，Nothing是其他类的子类
  * Any 有两个子类
  * AnyVal 是scala里的每个内建值类的父类，八个基本类型和Unit，Unit只有一个实例值，写成()
  *       这里还有一个富函数的概念，包含一个Int到RichInt的隐式转换，
  * AnyRef 是所有引用类的父类，对应着java中的Object类，
  */
class program_11 {


  /**
    * 原始类型的实现和Java中其实是一致的，当需要把这些值看成对象的时候，也是类似拆箱装箱的，
    * scala里的==操作被视为对类型表达透明的，"abcd" == "abcd" 返回true
    * scala 中想要比较引用相等，需要使用eq(AnyRef 中定义的)，反义词为ne
    */

  /**
    * 底层类型：scala.Null  和  scala.Nothing
    * Null 是null引用对象的类型，它是每个引用类( AnyRef的子类 ) 的子类，Null 不兼容值类型，不能把Null赋值给整数变量
    *
    * Nothing 类型在scala层级的最低端，是任何其他类型的子类，但是：根本没有这个类型的任何值
    * Nothing 的一个用处是它标明了不正当的终止，
    * 而且因为Nothing是任何其他类型的子类，所以可以非常灵活的使用像error这样的方法
    */
  def devide(x:Int, y:Int):Int =
    if(y != 0) x / y
    else error("can't devide by zero")

}


/**
  * $ 字符也被当作是字母，但被保留作为Scala 编译器产生的标识符使用，应该避免使用
  *
  * 在scala中，constant 这个词并不等同于val，尽管val在被初始化之后的确保持不变，但它仍然是变量，比如：方法参数是val类型，但是每次方法被调用这个val都代表不同的值
  *
  * 字面量标识符是用反引号 `...` 包括的任意字符串，思路是你可以把运行时环境认可的任意字符串放在反引号之前当作标识符，结果总是被当作是标识符
  * 应用场景： 在scala中不能使用Thread.yield() 因为yield是scala的保留字，但是可以使用Thread.`yield`()
  *
  * 函数式对象：不具有任何可改变状态的对象的类
  *
  * 好处：没有随时间变化的复杂的状态空间，不会有线程安全的问题
  */

class Rational(n:Int,d:Int){    //n代表分子，d代表分母    （只有主构造器可以调用超类的构造器）

  //scala编译器将把类内部的任何既不是字段也不是方法定义的代码编译至主构造器中
  println("Created "+n + "/" + d)//每次创建新的Rational实例时都会打印此消息

  require(d != 0)   //先决条件，分母不能为0

  private val g = gcd(n.abs,d.abs)

  //尽管n和d在类范围内有效，但他们只是构造器的一部分，scala不会为他们自动构造字段，只有如下声明，才会产生两个Int类型的字段，并产生getter方法，供外部调用
  val number: Int = n / g  //分子
  val denom: Int = d / g

  //辅助构造器
  //Scala 的每个构造器的第一个动作都是调用同类的别的构造器
  //所以主构造器是类的唯一入口点
  def this(n: Int) = this(n,1)

  /**
    * 此add() 方法会报错，因为add()方法仅能访问调用对象的自身的值，而that并不是调用add的Rational对象，所以无法获取到that的n和d
    */
  //def add(that: Rational): Rational = new Rational(n*that.d + that.n*d,d*that.d)

  //正确的写法
  def +(that: Rational): Rational =
    new Rational(
      number * that.denom + that.number * denom,
      denom * that.denom
    )


  //方法的重载
  def +(i: Int): Rational = new Rational(number + i * denom,denom)


  def -(that: Rational): Rational =
    new Rational(
      number * that.denom - that.number * denom,
      denom * that.denom
    )

  //方法的重载
  def -(i: Int): Rational = new Rational(number - i * denom,denom)


  def *(that: Rational): Rational =
    new Rational(number * that.number,denom * that.denom)

  //方法的重载
  def *(i: Int): Rational = new Rational(number * i, denom)


  def /(that: Rational): Rational =
    new Rational(number * that.denom,denom * that.number)

  //方法的重载
  def /(i: Int): Rational = new Rational(number, denom * i)


  //测试当前有理数是否小于传入的参数
  //this代表当前执行方法被调用的对象实例（这里可以省略）
  def lessThan(that: Rational) = this.number * that.denom < that.number * this.denom

  //返回较大者
  def max(that: Rational) = if(this.lessThan(that)) that else this  //这里的this不能省略，否则什么都返回不了

  private def gcd(a: Int,b:Int): Int = if (b == 0) a else gcd(b,a % b)  //获取最大公约数

  override def toString: String = n + "/" + d


  //定义Int 到 Rational 的隐式转换，这是为了支持 1 + oneHalf 这样的语法
  implicit def intToRational(x: Int) = new Rational(x)
}

object program_6 {

  
}

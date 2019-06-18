import scala.actors.Actor

/**
  * 样本类和模式匹配
  * 对于树型递归函数尤其有用
  *
  * 样本类：scala 避免在对象上使用模式匹配时需要大量的固定写法而采用的方式，
  * 样本类最大的好处就是支持模式匹配
  */

//sealed  封闭类：除了类定义所在的文件之外不能再添加任何新的子类，这样在使用样本类做模式匹配的时候，如果缺失模式组合，编译器会发出警告
sealed abstract class Expr

//以下四个：每个代表一种样本类
case class Var(name: String) extends Expr

case class Number(num: Double) extends Expr

case class Unop(operator: String, arg: Expr) extends Expr

case class BinOp(operator: String, left: Expr, right: Expr) extends Expr


/**
  * 和switch 的三点不同：
  * 1. 它始终以值作为结果
  * 2. scala的备选项表达式永远不会掉到下一个case
  * 3. 如果没有模式匹配，MatchError 会被抛出
  *
  * 匹配模式的种类
  * 1. 通配模式
  * 2. 常量模式
  * 3. 变量模式
  * 4. 序列模式
  * 5. 元祖模式
  * 6. 类型模式
  * 7. 变量绑定模式
  *
  */
object program_15 {

  //例子
  // 略


  //===========================for 表达式里的模式=======================
  val capitals = Map("France" -> "paris","Japan" -> "Tokyo")

  //一定能匹配到
  for((country,city) <- capitals)
    println("country :" + country + " city is " + city)

  //不一定能匹配到
  val results = List(Some("apple"),None,Some("orange"))
  for(Some(fruit) <- results)
    println(fruit)

  //===========================用做偏函数的样本序列=======================

  // 函数: Option[Int]  是参数，{} 内是函数体
  val withDefault: Option[Int] => Int = {
    case Some(x) => x
    case None => 0
  }
  println(withDefault(Some(10)))
  println(withDefault(None))

  //Actor
  /*react{
    case (name:String,actor:Actor) =>{
      actor ! getip(name)
      act()
    }
    case msg =>{
      println("Unhandled message: " + msg)
      act()
    }
  }*/

  val second: List[Int] => Int = {
    case x :: y :: _ => y
  }

  println(second(List(5,6,7)))
  //println(second(List()))   //报错


  //===========================模式在变量定义中===========================
  val exp = new BinOp("*",Number(5),Number(1))
  val BinOp(op,left,right) = exp


  //===========================Option 类===========================

  println(capitals get "France")
  println(capitals get "North Pole")

  // 模式匹配的方式
  def show(x:Option[String]) = x match {
    case Some(s) => x
    case None => "?"
  }

  println(show(capitals get "France"))
  println(show(capitals get "North Pole"))

  //===========================封闭类=======================================
  //sealed 修饰符修饰抽象类Expr

  // 编译器会发生警告，因为缺少Unop和BinOp，解决方式是使用@unchecked
  def describe(e:Expr):String = (e: @unchecked) match {
    case Number(_) => s"a number"
    case Var(_) => "a var iable"
  }


  //===========================模式重叠=======================================
  // 如果出现模式重叠，编译器将直接报错；


  //===========================模式守卫=======================================

  def simplifyAdd(e:Expr) = e match {
    //case BinOp("+",x,x)  => BinOp("*",x,Number(2))   // 错误
    case BinOp("+",x,y) if x == y => BinOp("*",x,Number(2))   // 必须模式守卫，因为不能使用两个相同的参数名x
    case _ =>
  }

  //===========================模式匹配种类=====================================

  val expr: Any = null

  /**
    * 变量绑定模式
    * 也就是说用 e 指代 Unop("abs",_)，
    * 但是匹配的时候，还是匹配使用了两遍绝对值操作符的操作
    */
  expr match {
    case Unop("abs", e@Unop("abs", _)) => e
    case _ =>
  }

  /**
    * 类型擦除的唯一例外就是数组：无论是在java中，还是在scala中，都被特殊处理了
    */
  def isStringArray(x: Any) = x match {

    case a: Array[String] => "yes"
    case _ => "no"
  }

  val ai = Array(1, 2, 3)
  println(isStringArray(ai)) // false


  /**
    * 类型擦除，类型参数信息根本不会保留到运行期
    * 系统所能做的只是判断这个值是否是某种任意类型参数的Map
    */
  def isIntIntMap(x: Any) = x match {

    case m: Map[Int, Int] => true
    case _ => false
  }

  println(isIntIntMap(Map("abc" -> "abc"))) //true

  // 类型模式
  def generalSize(x: Any) = x match {
    case s: String => s.length
    case m: Map[_, _] => m.size
    case _ => 1
  }

  // 元祖模式
  expr match {
    case (a, b, c) => println("matched " + a + b + c)
    case _ =>
  }

  // 序列模式
  expr match {
    case List(0, _, _) => println("found it")
    case _ =>
  }

  expr match {
    case List(0, _*) => println("found it") // 匹配任意长度
    case _ =>
  }

  /**
    * E 不能匹配 PI，那么编译器怎么知道PI 是常量，而不是代表选择器自身值得变量？
    * 区分规则：
    * 用小写字母开头的简单名被当做模式变量，所有其他的引用被认为是常量，
    * 还可以用 `` 把变量括起来，使其变成常量匹配模式
    */

  import Math.{E, PI}

  val pi = PI

  val res = E match {
    case PI => "strange math Pi = " + PI // PI 被当做常量
    case _ => "ok"
  }
  println(res) // ok

  val res1 = E match {
    case pi => "strange math Pi = " + pi // pi 被当做是变量，传进来的 E 匹配了它，所以最后的返回的数字是 2.718281828459045
    case _ => "ok"
  }
  println("====" + res1) //strange math Pi = 2.718281828459045

  // 常量模式
  def describe(x: Any) = x match {

    case 5 => "five"
    case true => "truth"
    case "hello" => "hi"
    case Nil => "empty list" // 任何的val或单例对象也可以被当做常量
    case _ => "something else"
  }

  // 变量模式
  def simplifyTop(expr: Expr): Expr = expr match {

    // 模式  =>  表达式
    // + /  1 都属于常量模式，用 == 匹配
    // e  属于变量模式，匹配所有值，右侧的 e 指代 被匹配的值
    case Unop("-", Unop("-", e)) => e //双重负号
    case BinOp("+", e, Number(0)) => e // 加0
    case BinOp("*", e, Number(1)) => e //乘1
    case _ => expr
  }


  def main(args: Array[String]): Unit = {

    //================================> case 样本类添加的三件事
    val v = Var("x") // 1. 添加了与类名一致的工厂方法，省去了new

    val op = BinOp("+", Number(1), v)

    println(v.name) // 2. 所有参数都隐式的获得了val前缀，因此被当做字段维度

    println(op.right == Var("x")) // 3. 自动添加了toString,hashCode,equals 的实现，所以：返回true

    val res = simplifyTop(Unop("-", Unop("-", Var("x"))))
    println(res) //Var(x)

  }

}

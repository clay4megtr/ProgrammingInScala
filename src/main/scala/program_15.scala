import program_15.FreeUser

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

  //====================================gitbook===================================
  //自定义提取器
  trait User {
    def name: String
    def score: Int
  }
  class FreeUser(
                  val name: String,
                  val score: Int,
                  val upgradeProbability: Double
                ) extends User
  class PremiumUser(
                     val name: String,
                     val score: Int
                   ) extends User
  object FreeUser {
    def unapply(user: FreeUser): Option[(String, Int, Double)] =
      Some((user.name, user.score, user.upgradeProbability))
  }
  object PremiumUser {
    def unapply(user: PremiumUser): Option[(String, Int)] =
      Some((user.name, user.score))
  }

  val user: User = new FreeUser("Daniel", 3000, 0.7d)
  user match {
    case FreeUser(name, _, p) =>
      if (p > 0.75) "$name, what can we do for you today?"
      else "Hello $name"
    case PremiumUser(name, _) =>
      "Welcome back, dear $name"
  }


  //布尔提取器
  //可以把模式匹配放在单独的提取器中；提取器不一定非要在这个类的伴生对象中定义
  object premiumCandidate {
    def unapply(user: FreeUser): Boolean = user.upgradeProbability > 0.75
  }

  val user2: User = new FreeUser("Daniel", 2500, 0.8d)
  user2 match {
      //使用的时候，只需要把一个空的参数列表传递给提取器，因为它并不真的需要提取数据，自然也没必要绑定变量。
    case freeUser @ premiumCandidate() => initiateSpamProgram(freeUser) //这个函数接收 FreeUser 类型的参数，
      //因为如此，Scala 的模式匹配也允许将提取器匹配成功的实例绑定到一个变量上， 这个变量有着与提取器所接受的对象相同的类型。这通过 @ 操作符实现。
      //premiumCandidate 接受 FreeUser 对象，因此，变量 freeUser 的类型也就是 FreeUser 。
    case _ => sendRegularNewsletter(user2)
  }

  def initiateSpamProgram(freeUser: FreeUser) = {

  }
  def sendRegularNewsletter(user: User) = {

  }


  //中缀表达式形式 只有针对流或者序列才有必要使用这种方式
  val xs = 58 #:: 43 #:: 93 #:: Stream.empty

  xs match {
    case first #:: second #:: _ => first - second
    case _ => -1
  }

  //正常形式
  xs match {
    case #::(first, #::(second, _)) => first - second
    case _ => -1
  }


  /**
    * Scala 提供了提取任意多个参数的模式匹配方法。
    * def unapplySeq(object: S): Option[Seq[T]]
    * 这个方法接受类型 S 的对象，返回一个类型参数为 Seq[T] 的 Option 。
    *
    * def unapplySeq(object: S): Option[(T1, .., Tn-1, Seq[T])]
    */
  val xs1 = 3 :: 6 :: 12 :: Nil
  xs1 match {
    case List(a, b) => a * b
    case List(a, b, c) => a + b + c
    case _ => 0
  }

  //也可以使用通配符 _* 匹配长度不确定的列表
  val xs2 = 3 :: 6 :: 12 :: 24 :: Nil
  xs2 match {
    case List(a, b, _*) => a * b
    case _ => 0
  }

  //提取出所有的 name
  object GivenNames {
    def unapplySeq(name: String): Option[Seq[String]] = {
      val names = name.trim.split(" ")
      if (names.forall(_.isEmpty)) None  //全部为空才参会None，
      else Some(names)  //只要有就返回
    }
  }

  def greetWithFirstName(name: String) = name match {
    case GivenNames(firstName, _*) => s"Good morning, $firstName!"
    case _ => "Welcome! Please make sure to fill in your name!"
  }

  //提取姓、名、和包含其他名字的一个序列
  object Names {
    def unapplySeq(name: String): Option[(String, String, Seq[String])] = {
      val names = name.trim.split(" ")
      if (names.size < 2) None
      else Some((names.last, names.head, names.drop(1).dropRight(1)))
    }
  }

  def greet(fullName: String) = fullName match {
    case Names(lastName, firstName, _*) =>
      s"Good morning, $firstName $lastName!"
    case _ =>
      "Welcome! Please make sure to fill in your name!"
  }


  /**
    * 值定义的模式
    * 也就是在等号左边使用模式，类似python的序列解包
    */
  case class Player(name: String, score: Int)
  def currentPlayer(): Player = Player("Daniel", 3500)

  //在定义类的时候直接去解构它,厉害
  val Player(name, _) = currentPlayer()
  doSomethingWithName(name)

  def doSomethingWithName(str: String) = {

  }

  def scores: List[Int] = List()
  val best :: rest = scores
  println("The score of our champion is " + best)  //MatchError，解决建议是：只解构那些在编译期就知道类型的样例类，例如元祖类型

  //什么时候会抛出 MatchError？
  //必须确保对于每一个可能的输入，都会有一个样例能够匹配成功， 不然，运行时会抛出 MatchError 。

  //返回一个元祖
  def gameResult(): (String, Int) = ("Daniel", 3500)

  val result = gameResult()
  println(result._1 + ": " + result._2)  //让人感觉很怪异

  //换成模式匹配的方式 就会好看很多
  val (name1, score) = gameResult()
  println(name1 + ": " + score)


  /**
    * for 语句中的模式
    */
  def gameResults(): Seq[(String, Int)] =
    ("Daniel", 3500) :: ("Melissa", 13000) :: ("John", 7000) :: Nil

  def hallOfFame = for {
    result <- gameResults()
    (name, score) = result
    if (score > 5000)
  } yield name

  //或者直接写成
  def hallOfFame1 = for {
    (name, score) <- gameResults()
    if (score > 5000)
  } yield name

  //生成器左侧的模式也可以用来过滤。
  //左侧的模式不匹配空列表。 这不会抛出 MatchError ，但对应的空列表会被丢掉，因此得到的结果是 List(3, 2) 。
  val lists = List(1, 2, 3) :: List.empty :: List(5, 3) :: Nil
  for {
    list @ head :: _ <- lists
  } yield list.size


  /**
    * 匿名函数中的模式匹配
    * 模式匹配型的匿名函数的类型就是 PartialFunction
    * 依据继承关系，将一个模式匹配型的匿名函数传递给接受一元函数的方法（如：map、filter）是没有问题的， 只要这个匿名函数对于所有可能的输入都有定义。
    */
  val wordFrequencies = ("habitual", 6) :: ("and", 56) :: ("consuetudinary", 2) ::
    ("additionally", 27) :: ("homely", 5) :: ("society", 13) :: Nil

  //去掉词频最高的，和词频最低的
  //问题1：首先，访问元组字段的代码不好看，如果我们可以直接解构出字段，那代码可能更加美观和可读。
  def wordsWithoutOutliers(wordFrequencies: Seq[(String, Int)]): Seq[String] =
    wordFrequencies.filter(wf => wf._2 > 3 && wf._2 < 25).map(_._1)

  wordsWithoutOutliers(wordFrequencies) // List("habitual", "homely", "society")


  //重构1：模式匹配形式的匿名函数，它是由一系列模式匹配样例组成的，正如模式匹配表达式那样，不过没有 match
  //要解构的数据类型在编译期就确定了，没有会出错的可能
  def wordsWithoutOutliers1(wordFrequencies: Seq[(String, Int)]): Seq[String] =
    wordFrequencies.filter { case (_, f) => f > 3 && f < 25 } map { case (w, _) => w }


  /**
    * 偏函数
    * 只处理特定输入的函数，可以解决上面需要遍历两次的问题
    */
  //重构2：偏函数重构上面的代码
  val pf: PartialFunction[(String, Int), String] = {
    case (word, freq) if freq > 3 && freq < 25 => word
  }

  //方法二：可以显示的扩展 PartialFunction 特质：
  val pf1 = new PartialFunction[(String, Int), String] {
    def apply(wordFrequency: (String, Int)) = wordFrequency match {
      case (word, freq) if freq > 3 && freq < 25 => word
    }
    def isDefinedAt(wordFrequency: (String, Int)) = wordFrequency match {
      case (word, freq) if freq > 3 && freq < 25 => true
      case _ => false
    }
  }

  wordFrequencies.map(pf) // will throw a MatchError , Nil not match

  wordFrequencies.collect(pf) // List("habitual", "homely", "society")  运行正常，因为collect 会直接丢弃不匹配的；

  //最终的形式：collect + 偏函数
  def wordsWithoutOutliers2(wordFrequencies: Seq[(String, Int)]): Seq[String] =
    wordFrequencies.collect { case (word, freq) if freq > 3 && freq < 25 => word }


  //====================================gitbook===================================



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
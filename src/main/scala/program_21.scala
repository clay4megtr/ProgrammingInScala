
/**
  * 隐式转换
  * 优势：让我们在现存类中添加新方法，而且在RandomAccessSeq添加了新的方法后，string会自动包含新增加的方法；
  */

object Dollar{

  //这样就不用在我们的程序中单独的进行引用转换了
  //implicit def dollarToEuro(x: Dollar):Euro = ....
}

class Dollar{

}

object program_21 {

  def main(args: Array[String]): Unit = {

    /*implicit def stringWapper(s:String) =
   //这里类似匿名内部类，只是()省略了
   //仅需要在stringWapper转换中定义length和apply方法，就可以免费获得其他所有RandomAccessSeq中定义的方法，
    new RandomAccessSeq[Char] {
      def length = s.length
      def apply(i:Int) = s.charAt(i)
    }*/

    //隐式定义是指编译器为了修正类型错误而允许插入到程序中的定义

    //=====规则
    //1.只有标记为implicit 的定义才是可用的；
    implicit def intToString(x: Int) = x.toString

    //2.插入的隐式转换必须以 单一标识符 的形式处于作用域中，或与转换的源或目标类型关联在一起；
    //单一标识符有一个例外，编译器还将在源类型或目标类型的伴生对象中寻找隐式定义,如上


    //3.无歧义规则：引用转换唯有不存在其他可插入转换的前提下才能插入
    //如果有两个可选方法修正x+y,编译器将会拒绝作出选择


    //4.单一调用原则：只会尝试一个隐式操作，编译器不会把x+y重写成convert1(convert2(x))+y

    //5.显示操作先行原则：若编写的代码类型检查无误，则不会尝试任何隐式操作；


    //===隐式转换在哪里进行尝试
    //1. 转换为期望类型，
    //2. 指定(方法)调用者的转换，
    //3. 隐式参数


    //1. 转换为期望类型，
    val i = 3.5

    implicit def doubleToInt(x: Double) = x.toInt

    //从Int到Double的隐式转换定义在scala.Predef中；这里有一个被所有scala程序隐式引用的；


    //2. 指定(方法)调用者的转换，
    //program_6 中的intToRational，

    //模拟新的语法
    // -> 这个符号是如何支持的？ 它是 scala.Predef 中 ArrowAssoc 类的方法，此外还有一个 any2ArrowAssoc 隐式转换
    // 1先被转换成ArrowAssoc，然后调用 -> 方法， 返回一个Tuple
    val map = Map(1 -> "one",2 -> "two",3 -> "three")


    //3. 隐式参数

    //显示提供
    val bobsPrompt = new PreferredPrompt("relax> ")
    val bobsdrink = new PreferredDrink("milk")
    Greeter.greet("Bob")(bobsPrompt,bobsdrink)

    //隐式提供
    implicit val promot = new PreferredPrompt("yes, master> ")
    implicit val drink = new PreferredDrink("coffee")
    Greeter.greet("Joe")


    //这里不用显示创建隐式参数： implicit orderer: T => Ordered[T] 的原因是scala本身已经提供了
    val res = maxListImpParm(List(1,2,3))
    println(res)


  }

  //以 List[T] 为入参，并通过上界指定T必须是 Ordered[T] 的子类型
  //使用这种方式的弱点是你不能把这个函数用于元素类型不是 Ordered 子类型的列表
  def maxListUpBound[T <: Ordered[T]](elements: List[T]):T =
    elements match {
      case List() =>
        throw new IllegalArgumentException("empty list")
      case List(x) => x
      case x :: rest =>
        val maxRest = maxListUpBound(rest)
        if(x > maxRest) x else maxRest
    }

  //优化后的写法
  //不能写成如下的写法：def maxListImpParm[T](elements: List[T])(implicit orderer:(T,T) => Boolean):T =
  //因为这样的隐式转换过于普通，很容易影响其他代码，
  //如果把 implicit 用在参数上，编译器不仅会尝试使用隐式值补足这个参数，还会把这个参数当做可用的隐式操作用于方法体中；
  def maxListImpParm[T](elements: List[T])(implicit orderer: T => Ordered[T]):T =
    elements match {
      case List() =>
        throw new IllegalArgumentException("empty list!!!")
      case List(x) => x
      case x :: rest =>
        //val maxRest = maxListImpParm(rest)(orderer)
        val maxRest = maxListImpParm(rest)    //隐式转换出orderer
        //if(orderer(x) > maxRest) x else maxRest
        if(x > maxRest) x else maxRest        //隐式转换出orderer
    }


  //隐式函数的名称是可以被忽略的，因为函数体对他的使用也都是隐式的，所以有下面的写法：视界
  // T <% Ordered[T]  任何的T都好，只要T能被当做Ordered[T]即可，
  def maxList[T <% Ordered[T] ](elements: List[T]): T =
    elements match {
      case List() =>
        throw new IllegalArgumentException("empty list!!!")
      case List(x) => x
      case x :: rest =>
        //val maxRest = maxListImpParm(rest)(orderer)
        val maxRest = maxListImpParm(rest)    //隐式转换出orderer
        //if(orderer(x) > maxRest) x else maxRest
        if(x > maxRest) x else maxRest        //隐式转换出orderer
    }

}

//封装用户喜欢的shell提示字符
class PreferredPrompt(val prefernce: String)
class PreferredDrink(val prefernce: String)

object Greeter{

  //欢迎
  //注意：implicit被用于全体参数列表，而不是单独的参数
  //还有：既然PreferredPrompt和PreferredDrink都是只有一个String字段，为什么不直接把他们设置成String类型？
  //因为编译器选择隐式参数的方式是通过匹配参数类型与作用域内的值类型；隐式参数往往是很稀少或者很特殊的类，以至于不会被碰巧匹配到；
  def greet(name: String)(implicit prompt:PreferredPrompt,drink: PreferredDrink): Unit ={
    println("welcome, " + name + ". The system is ready.")
    println("but while you look")
    println("why not enjoy a cup of " + drink.prefernce + "?")
    println(prompt.prefernce)
  }
}

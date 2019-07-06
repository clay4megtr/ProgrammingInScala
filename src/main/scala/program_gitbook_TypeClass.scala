

//类型类：程序既拥抱扩展性，又不放弃具体的类型信息
object program_gitbook_TypeClass {

  /**
    * 问题：
    * 假设想提供一系列可以操作数字集合的函数，主要是计算它们的聚合值。进一步假设只能通过索引来访问集合的元素，
    * 只能使用定义在 Scala 集合上的 reduce 方法。
    * （施加这些限制，是因为要实现的东西，Scala 标准库已经提供了）最后，假定得到的值已排序。
    */

  object Statistics {
    def median(xs: Vector[Double]): Double = xs(xs.size / 2)  //中位数

    def quartiles(xs: Vector[Double]): (Double, Double, Double) =
      (xs(xs.size / 4), median(xs), xs(xs.size / 4 * 3))      //上四分位数和下四分位数

    def iqr(xs: Vector[Double]): Double = quartiles(xs) match {
      case (lowerQuartile, _, upperQuartile) => upperQuartile - lowerQuartile
    }   //四分差

    def mean(xs: Vector[Double]): Double = {
      xs.reduce(_ + _) / xs.size
    }   //平均数
  }

  //问题：如何支持Int ？
  //方法一：适配器
  //问题：把数字封装在适配器里，这样的代码会令人厌倦，无论读写，而且和统计库交互时，必须创建一大堆适配器实例。
  object Statistics1 {
    trait NumberLike[A] {
      def get: A
      def plus(y: NumberLike[A]): NumberLike[A]
      def minus(y: NumberLike[A]): NumberLike[A]
      def divide(y: Int): NumberLike[A]
    }
    case class NumberLikeDouble(x: Double) extends NumberLike[Double] {  //每次都需要新建这样的适配器类型；
      def get: Double = x
      def minus(y: NumberLike[Double]) = NumberLikeDouble(x - y.get)
      def plus(y: NumberLike[Double]) = NumberLikeDouble(x + y.get)
      def divide(y: Int) = NumberLikeDouble(x / y)
    }
    type Quartile[A] = (NumberLike[A], NumberLike[A], NumberLike[A])
    def median[A](xs: Vector[NumberLike[A]]): NumberLike[A] = xs(xs.size / 2)
    def quartiles[A](xs: Vector[NumberLike[A]]): Quartile[A] =
      (xs(xs.size / 4), median(xs), xs(xs.size / 4 * 3))
    def iqr[A](xs: Vector[NumberLike[A]]): NumberLike[A] = quartiles(xs) match {
      case (lowerQuartile, _, upperQuartile) => upperQuartile.minus(lowerQuartile)
    }
    def mean[A](xs: Vector[NumberLike[A]]): NumberLike[A] =
      xs.reduce(_.plus(_)).divide(xs.size)
  }


  /**
    * 方法二：类型类
    * 一个类型类 C 定义了一些行为，要想成为 C 的一员，类型 T 必须支持这些行为。一个类型 T 到底是不是类型类 C 的成员，这一点并不是与生俱来的。
    * 开发者可以实现类必须支持的行为，使得这个类变成类型类的成员。 一旦 T 变成 类型类 C 的一员，参数类型为类型类 C 成员的函数就可以接受类型 T 的实例。
    * 这样，类型类支持临时的、追溯性的多态，依赖类型类的代码支持扩展性，且无需创建任何适配器对象。
    */

  //创建类型类
  object Math {

    //类型类总会带着一个或多个类型参数，通常是无状态的，比如：里面定义的方法只对传入的参数进行操作。
    import annotation.implicitNotFound
    @implicitNotFound("No member of type class NumberLike in scope for ${T}")  //自定义错误消息(没有找到对应的隐式参数类型)
    trait NumberLike[T] {  //类型类特质 : NumberLike  它来定义怎么计算
      def plus(x: T, y: T): T
      def divide(x: T, y: Int): T
      def minus(x: T, y: T): T
    }

    //第二步通常是在伴生对象里提供一些默认的类型类特质实现，为什么要在伴生对象中提供？原因看：Statistics2的mean方法；
    object NumberLike {

      //实现 Double 和 Int 的类型类特质：
      implicit object NumberLikeDouble extends NumberLike[Double] {  //这里的implicit很重要，对于Statistics2的mean方法来说，在作用域中没有找到隐式参数，会在
        def plus(x: Double, y: Double): Double = x + y                //隐式参数类型的伴生对象中寻找
        def divide(x: Double, y: Int): Double = x / y
        def minus(x: Double, y: Double): Double = x - y
      }
      implicit object NumberLikeInt extends NumberLike[Int] {
        def plus(x: Int, y: Int): Int = x + y
        def divide(x: Int, y: Int): Int = x / y
        def minus(x: Int, y: Int): Int = x - y
      }
    }
  }

  //使用类型类
  object Statistics2 {
    import Math.NumberLike

    //将参数限制在特定类型类的成员上，是通过第二个implicit参数列表实现的。这是什么意思？这是说，当前作用域中必须存在一个隐式可用的 NumberLike[T] 对象，
    //比如说，当前作用域声明了一个隐式值(implicit value)。 这种声明很多时候都是通过导入一个有隐式值定义的包或者对象来实现的。

    //当且仅当没有发现其他隐式值时，编译器会在隐式参数类型的伴生对象中寻找。作为库的设计者，将默认的类型类实现放在伴生对象里意味着库的使用者可以轻易的重写默认实现，
    //这正是库设计者喜闻乐见的。用户还可以为隐式参数传递一个显示值，来重写作用域内的隐式值。
    def mean[T](xs: Vector[T])(implicit ev: NumberLike[T]): T =
      ev.divide(xs.reduce(ev.plus(_, _)), xs.size)
  }

  val numbers = Vector[Double](13, 23.0, 42, 45, 61, 73, 96, 100, 199, 420, 900, 3839)
  println(Statistics2.mean(numbers))  //正常输出


  /**
    * 上下文绑定
    * 总是带着这个隐式参数列表显得有些冗长。对于只有一个类型参数的隐式参数，Scala提供了一种叫做上下文绑定(context bound) 的简写
    * 如果类型类需要多个类型参数，就不能使用上下文绑定语法了
    */
  object Statistics3 {
    import Math.NumberLike
    def mean[T](xs: Vector[T])(implicit ev: NumberLike[T]): T =
      ev.divide(xs.reduce(ev.plus(_, _)), xs.size)

    //使用[T : NumberLike] 替代 (implicit ev: NumberLike[T])
    def median[T : NumberLike](xs: Vector[T]): T = xs(xs.size / 2)
    def quartiles[T: NumberLike](xs: Vector[T]): (T, T, T) =
      (xs(xs.size / 4), median(xs), xs(xs.size / 4 * 3))

    //[T: NumberLike]的意思是：必须有一个类型为NumberLike[T]的隐式值在当前上下文中可用，这和隐式参数列表是等价的。如果想要访问这个隐式值，需要调用 implicitly 方法
    def iqr[T: NumberLike](xs: Vector[T]): T = quartiles(xs) match {
      case (lowerQuartile, _, upperQuartile) =>
        implicitly[NumberLike[T]].minus(upperQuartile, lowerQuartile)
    }
  }

  /**
    * 自定义的类型类成员
    */
  object JodaImplicits {
    import Math.NumberLike
    import org.joda.time.Duration

    implicit object NumberLikeDuration extends NumberLike[Duration] {
      def plus(x: Duration, y: Duration): Duration = x.plus(y)
      def divide(x: Duration, y: Int): Duration = Duration.millis(x.getMillis / y)
      def minus(x: Duration, y: Duration): Duration = x.minus(y)
    }
  }

  //导入包含这个实现的包或者对象，就可以计算一堆 durations 的平均值了：
  import Statistics3._
  import JodaImplicits._
  import org.joda.time.Duration._

  val durations = Vector(standardSeconds(20), standardSeconds(57), standardMinutes(2),
    standardMinutes(17), standardMinutes(30), standardMinutes(58), standardHours(2),
    standardHours(5), standardHours(8), standardHours(17), standardDays(1),
    standardDays(4))
  println(mean(durations).getStandardHours)


}


/**
  * 包和引用
  *
  * 1.可以出现在任何地方；
  * 2.可以指的是对象和包；
  * 3.可以重命名(使用=>)或隐藏一些被引用的成员；
  */

/**
  * 私有成员
  */
class Outer{
  class Inner{
    private def f()={println("f")}
    class InnerMost{
      f()   //ok
    }
  }
  // (new Inner).f() //错误：无法访问，java中可以，外部类无条件访问内部类
}


/**
  * 保护成员
  * scala 中，保护成员只在定义了成员的类的子类中可以访问
  * java 中，还允许在同一个包中的其他类进行访问，
  */
package p{
  class Super{
    protected def f() = {println("f")}
  }
  class Sub extends Super{
    f()
  }
  class other{
    //(new Super).f()  //错误：无法访问，java中可以，可以访问一个包下
  }
}


/**
  * 保护的作用域
  */
package bobsrockets{

  package navigation{

    private[bobsrockets] class Navigator{  //这个类对包含在bobsrockets包下的所有类和对象可见

      protected[navigation] def useStarChart(){}  //useStarChart 能被Navigator所有子类以及包含在navigation包里的所有代码访问

      class LegOfJourney{
        private[Navigator] val distance = 100   //在 Navigator类的任何地方都可见，private[C] 里的C如果是最外层类，那么private和java中的一致
      }
      private[this] var speed = 200   //仅能在包含了定义的同一个对象中被访问，被称为对象私有，即同一个实例内部才能访问，

    }
  }

  package launch{
    import navigation._

    object Vehicle{
      private[launch] val guide = new Navigator  //guide 只能在launch 包中访问，等价于java的包私有访问；

      val other = new Navigator
      // other.speed  //无法访问，speed 是对象私有，只能在Navigator类内部通过 speed 和 this.speed 访问
    }

  }

}


/**
  * 可见性和伴生对象
  *
  * scala 的访问规则给与了伴生对象和类一些特权，类的所有访问权限都对伴生对象开放，反过来也是如此
  */


/**
  *
  */
object program_13 {

  def main(args: Array[String]): Unit = {

  }
}

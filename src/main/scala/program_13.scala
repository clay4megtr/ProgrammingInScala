
/**
  * 包和引用
  *
  * 1.可以出现在任何地方；
  * 2.可以指的是对象和包；
  * 3.可以重命名(使用=>)或隐藏一些被引用的成员；
  */

/**
  * 包
  */

//使用方式1. 类似java
//package com.bobsrockets.navigation
//class Navigator

//使用方式2. 类似c#的命名空间
/*package bobsrockets{
  package navigation{
    //在 bobsrockets.navigation 包中
    class Navigator

    package tests{
      //在 bobsrockets.navigation.tests 包中
      class NavigatorSuite
    }
  }
}*/

//如果一个包只是用来嵌入另一个包的话，可以使用如下方式省去一个缩进
/*package bobsrockets.navigation{
  //在 bobsrockets.navigation 包中
  class Navigator
  package tests{
    //在 bobsrockets.navigation.tests 包中
    class NavigatorSuite
  }
}*/

//java的包是分级的，但是不是嵌套的，java中命名一个包的时候必须从包层级的根开始，
//scala包是嵌套的，所以可以简单的表示为bobsrockets.navigation，
/*package bobsrockets{

  package navigation{
    class Navigator
  }

  package launch{

    class Booter{
      //不用写 bobsrockets.navigation、
      //可以这样写是因为Booter包含在bobsrockets包中，而bobsrockets中又包含navigation，因此可以直接写navigation
      val nav = new navigation.Navigator
    }
  }
}*/

/**
  * 访问修饰符
  * private
  * protected
  * public 默认的
  */

//私有成员：和java相同，只能在类的内部访问，但是scala外部类是无法访问内部类被标记为 private 的方法的；
/*class Outer{
  class Inner{
    private def f(){println("f")}
    class InnerMost{
      f()   //ok
    }
  }
  // (new Inner).f() //错误：无法访问，java中可以，外部类无条件访问内部类
}*/


//保护成员：scala 中，保护成员只在定义了成员的类的子类中可以访问，java 中，还允许在同一个包中的其他类进行访问，
/*package p{
  class Super{
    protected def f() = {println("f")}
  }
  class Sub extends Super{
    f()
  }
  class other{
    //(new Super).f()  //错误：无法访问，java中可以，可以访问一个包下
  }
}*/


/**
  * 保护的作用域
  */

//scala里的访问修饰符可以通过使用限定词强调，格式为private[x]或protected[x]的修饰符表示 "直到" x的私有或保护，
//这里的x指代某个所属的包、类或单例对象
//带限定的访问修饰符提供给你非常细粒度的可见度控制，
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
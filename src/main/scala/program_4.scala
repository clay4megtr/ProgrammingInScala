
/**
  * scala中把成员公开的方法是不显示的指定任何访问修饰符
  * 也就是说public 是scala 的默认访问级别
  *
  * scala 方法参数的一个重要特征是他们都是val，不是var
  */
object program_4 {

  class ChecksumAccumulator {
    private var sum = 0
    def add(b:Byte):Unit = {
//      b=1 // 编译不过，scala 方法参数的一个重要特征是他们都是val，不是var
      sum += b
    }

    def checkSum():Int = {
      return ~(sum &0xFF) + 1
    }

    def f() = {" this string gets returned! "}  //一定要加 = 等于号， 否则默认会返回Unit，丢失string

  }

  /**
    * 单例对象：
    * scala 不能定义静态成员，所以用单例对象来替代
    *
    * 伴生对象：和伴生类之间可以互相访问其私有成员，
    *
    * 类和单例对象的差别是：单例对象不带参数，而类可以
    */
  object ChecksumAccumulator{
    private var cache = Map[String,Int]()

    def calculate(s: String):Int =
      if(cache.contains(s))
        cache(s)
      else {
        val acc = new ChecksumAccumulator
        for(c <- s)
          acc.add(c.toByte)
        val cs = acc.checkSum()
        cache += (s -> cs)
        cs
      }
  }


}

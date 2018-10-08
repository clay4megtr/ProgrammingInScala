
/**
  * scala程序员的平衡感：
  * 崇尚val，不可变对象和没有副作用的方法
  *
  * 方法没有副作用是函数式风格编程的重要理念
  * 计算并返回值应该是方法的唯一目的
  *
  * Array 是可变的，因为元素值是可变的
  */
object program_3 {

  def main(args: Array[String]): Unit = {
    val greetStrings = new Array[String](3)   //不能被重新赋值
    greetStrings(0) = "Hello"                 //但变量指向的对象内部仍然可以改变
    greetStrings(1) = ","
    greetStrings(2) = "world\n"

    for (i <- 0 to 2)   //to 实际上是仅带一个Int 参数的方法   (0).to(2)  //方法只有一个参数，调用的时候可以省略点及括号
      println(greetStrings(i))   //注意：这个语法只有在明确指定方法调用的接收者时才有效 println 10 错误 Console println 10正确

    //数组为什么用() 不用greetStrings[i]？
    //任何对象的值参数应用都将被转换为对apply方法的调用，greetStrings(i) 实际是调用的greetStrings.apply(i)

    //与之相似的是
    //当对带有括号并包括一到若干参数的变量值时，编译器将使用对象的update方法对括号里的参数(索引值)和等号右边的对象执行调用
    greetStrings(0) = "Hello"
    //实际调用的是
    greetStrings.update(0, "Hello")

    val numNames = Array("zero", "one", "two") //类型推断
    //实际是调用了数组的apply工厂方法，
    val numNames1 = Array.apply("zero", "one", "two")   //定义在Array伴生对象中，

    /**
      * List: 一旦创建，不可改变，是为实现函数式风格的编程而设计的
      */
    val oneTwo = List(1, 2)
    val threeFour = List(3, 4)
    val oneTwoThreeFour = oneTwo ::: threeFour

    val twoThree = List(2, 3)
    val oneTwoThree = 1 :: twoThree     //方法以冒号结尾，方法的调用者就是后面的变量  twoThree.::(1)
    println(s"=====>$oneTwoThree")

    val oneTwoThree1 = 1 :: 2 :: 3 :: Nil   //Nil 是空列表的简写

    /**
      * List 常用操作
      */
    val thrill = "Will" :: "fill" :: "until" :: Nil
    List("a", "b") ::: List("c", "d")       //叠加两个列表
    thrill(2)
    thrill.count(s => s.length == 4)
    thrill.drop(2)             //返回drop掉前两个元素后的新list
    thrill.dropRight(2)        //返回drop掉后两个元素后的新list
    thrill.exists(s => s == "until")
    thrill.filter(s => s.length == 4)
    thrill.forall(s => s.endsWith("1")) //判断是否thrill里的所有元素都以1 结尾
    thrill.foreach(println)
    thrill.head       //返回第一个
    thrill.init       //除最后一个元素之后组成的List
    thrill.isEmpty
    thrill.last     //最后一个
    thrill.length
    thrill.map(s => s + "y")
    thrill.mkString(",")
//    thrill.remove(s => s.length == 4)
    thrill.reverse
    thrill.tail   //返回除第一个元素之外剩余的元素

    /**
      * List 排序
      */
    //sorted: 对一个集合进行自然排序，通过传递隐式的Ordering   //适合单集合的升降序

    //sortBy: 对一个属性或多个属性进行排序，通过它的类型。  //适合对单个或多个属性的排序，代码量比较少，推荐使用这种

    //sortWith: 基于函数的排序，通过一个comparator函数，实现自定义排序的逻辑。
    //适合定制化场景比较高的排序规则，比较灵活，也能支持单个或多个属性的排序，但代码量稍多，内部实际是通过java里面的Comparator接口来完成排序的。

    //基于单集合单字段的排序
    val xs = Seq(1, 5, 3, 4, 6, 2)
    println("==============sorted排序=================")
    println(xs.sorted) //升序
    println(xs.sorted.reverse) //降序
    println("==============sortBy排序=================")
    println(xs.sortBy(d => d)) //升序
    println(xs.sortBy(d => d).reverse) //降序
    println("==============sortWith排序=================")
    println(xs.sortWith(_ < _))//升序
    println(xs.sortWith(_ > _))//降序

    //基于元组多字段的排序
    val pairs = Array(
      ("a", 5, 1),
      ("c", 3, 1),
      ("b", 1, 3)
    )
    //使用sortby
    val bx = pairs.sortBy(r => (r._3, r._1))(Ordering.Tuple2(Ordering.Int, Ordering.String.reverse))
    //打印结果
    bx.map(println)

    //使用sortWith
    val b = pairs.sortWith {
      case (a, b) => {
        if (a._3 == b._3) {   //如果第三个字段相等，就按第一个字段降序
          a._1 > b._1
        } else {
          a._3 < b._3   //否则第三个字段降序
        }
      }
    }

    //基于类的排序
    //先看sortBy的实现方法 排序规则：先按年龄排序，如果一样，就按照名称降序排

    case class Person(val name: String, val age: Int)

    val p1 = Person("cat", 23)
    val p2 = Person("dog", 23)
    val p3 = Person("andy", 25)

    val pairs2 = Array(p1, p2, p3)

    //先按年龄排序，如果一样，就按照名称降序排
    val bx2 = pairs2.sortBy(person => (person.age, person.name))(Ordering.Tuple2(Ordering.Int, Ordering.String.reverse))

    bx2.map(
      println
    )

    //再看sortWith的实现方法：
    val b2 = pairs2.sortWith {
      case (person1, person2) => {
        person1.age == person2.age match {
          case true => person1.name > person2.name //年龄一样，按名字降序排
          case false => person1.age < person2.age //否则按年龄升序排
        }
      }
    }

    /**
      * 使用Tuple 不可变，但是可以包含不同类型的元素
      * 函数返回多个值时，可以使用元组
      */
    var pair = (99, "lufg")
    println(pair._1)
    println(pair._2)

    /**
      * 使用Set
      */
    //默认是不可变的，返回false
    var jetSet = Set("Boeing", "Airbus")
    jetSet += "Lear"    //所以这里是返回了新的
    println(jetSet.contains("Cesson"))

    //引入可变的
    val movieSet = scala.collection.mutable.Set("Hitch", "poltergeist")
    movieSet += "Shrek"   //这里没有产生新集合
    println(movieSet)


    /**
      * 使用Map
      */
    //可变
    import scala.collection.mutable.Map

    val treasureMap = Map[Int, String]()
    treasureMap += (1 -> "go to island")
    treasureMap += (2 -> "find big x on ground")
    treasureMap += (3 -> "Dig")
    println(treasureMap(2))

    //不可变
    val romanNumeral = Map(
      1 -> "I", 2 -> "II", 3 -> "III"
    )
    println(romanNumeral(1))


    /**
      * 使用函数式编程的特征：完全使用val 编程
      */
    //指令式
    def printArgs(args: Array[String]): Unit = {
      var i = 0
      while (i < args.length) {
        println(args(i))
        i += 1
      }
    }

    //函数式（有副作用）
    def printArgs1(args: Array[String]): Unit = {
      for (arg <- args)
        print(arg)
    }

    def printArgs2(args: Array[String]): Unit = {
      args.foreach(println)
    }

    //完全函数式（没有副作用）
    def formatArgs(args: Array[String]) = args.mkString(",")

    println(formatArgs(args))


    /**
      * 读取文件(格式化：句尾对齐)
      */
    import scala.io.Source
    def widthOfLength(s: String) = s.length

    if (args.length > 0) {
      val lines = Source.fromFile(args(0)).getLines().toList
      val longestLine = lines.reduceLeft(
        (a, b) => if (a.length > b.length) a else b
      )
      val maxWidth = widthOfLength(longestLine)

      for (line <- lines) {
        val numSpaces = maxWidth - widthOfLength(line)
        val padding = " " * numSpaces
        println(padding + " | " + line)
      }
    } else {
      Console.err.println("please enter fileName")
    }
  }
}

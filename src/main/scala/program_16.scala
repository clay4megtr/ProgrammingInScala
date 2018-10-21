
/**
  * list
  */
object program_16 {

  /**
    * 和数组的区别：1. 列表不可变，不能通过赋值改变列表的元素
    *            2. 列表具有可递归结构（liked list），数组是连续的
    */
  def main(args: Array[String]): Unit = {

    /**
      * 空列表的类型是List[Nothing]，Nothing是所有类型的子类
      * 所以List[Nothing]可以被当做是其他任何形式为List[T] 的列表类型的对象
      */
    val xs: List[String] = List()

    //构造列表
    //List(1,2,3,4) 只是对如下方式的扩展
    val numbers = 1 :: 2 :: 3 :: 4 :: Nil
    val fruit = List("apple", "orange", "pears")

    // 基本操作
    numbers.head        // 第一个元素
    numbers.tail        // 除第一个元素之外的元素组成的列表；
    numbers.isEmpty

    /**
      * 不管是List(a,b,c) 还是 d :: e :: rest 都不符合之前定义的模式匹配种类，
      * 实际上 List(a,b,c) 是：开发库定义的抽取器模式的实例，
      * d :: e :: rest 是中缀操作符模式的特例，
      * 如果被看做是表达式，那么中缀操作与方法调用等价，但是对于模式匹配来说有些不同：
      * 如果被当做模式，那么类似于 p op q 这样的中缀操作符等价于op(p,q)，也就是说，中缀操作符op被当做构造器模式；
      * 在这里，x :: xs 这样的cons模式被看做是 ::(x,xs)，这么说，就存在一个名为 :: 的类，确实存在: scala.::
      * 它是可以创建非空列表的类，
      * 所以scala中存在两个 :: 一个存在于scala包中，另一个是List类的方法，
      * :: 方法的目的是实例化scala.::的对象
      */
    // 列表模式
    val List(a, b, c) = fruit
    println(a)
    println(b)
    println(c)

    //如果事先不知道元素的数量,最后的rest就是一个：未知长度的List
    val d :: e :: rest = fruit
    println(rest.isInstanceOf[List[String]]) //true


    //插入排序:模式匹配的方式
    def isSort(xs: List[Int]): List[Int] = xs match {
      case List() => List()
      case x :: xs1 => insert(x, isSort(xs1))
    }

    def insert(x: Int, xs: List[Int]): List[Int] = xs match {
      case List() => List(x)
      case y :: ys => if (x <= y) x :: xs
                      else y :: insert(x, ys)
    }


    //================================一阶方法==================================================
    //一阶方法：不以函数做入参的方法

    //:::
    List(1,2,3) ::: List(2,3,4)  //1,2,3,4,5

    /**
      * 自己实现 ::: 方法： 能够了解利用列表实现算法的通用方式
      * 分治原则：
      * 列表的许多算法首先是利用模式匹配把输入列表拆分为更简单的样本，这是原则里所说的分；
      * 然后根据每个样本构建结果，如果结果是非空列表，那么一块块部件将通过同样的递归遍历算法构建出来；
      * 这就是治；
      */
    def append[T](xs: List[T],ys: List[T]): List[T] = xs match {

      case List() => ys
      case x :: xs1 => x :: append(xs1,ys)
    }

    // length：需要遍历整个链表，和元素长度成正比
    numbers.length

    //last、init
    numbers.last    //最后一个元素
    numbers.init    //除去最后一个元素之外余下的列表

    /**
      * reverse
      * reverse 创建了新的列表而不是就地改变被操作的列表；
      */

    numbers.reverse.reverse   // 等价于 numbers

    numbers.reverse.init  // 等价于 numbers.tail.reverse
    numbers.reverse.tail  // 等价于 numbers.init.reverse
    numbers.reverse.head  // 等价于 numbers.last
    numbers.reverse.last  // 等价于 numbers.head

    //自己实现reverse 方法
    def rev[T](xs:List[T]):List[T] = xs match {
      case List() => xs
      case x :: xs1 => rev(xs1) ::: List(x)
    }

    /**
      * 前缀 和 后缀
      * splitAt：
      * 在指定位置拆分列表，并返回对偶列表，定义符合如下等式
      * xs splitAt n  ==== (xs take n,xs drop n)
      */
    numbers take 2   //1,2
    numbers drop 2   //3，4
    numbers splitAt 2  //(List(1,2),List(3,4))


    /**
      * apply : xs apply n 实际上的定义为 (xs drop n).head
      * indices
      */
    numbers apply 2  //3
    numbers(2)       //3  用的比较少，因为遍历花费的时间和n成正比

    numbers.indices  // List(0,1,2,3) 返回索引值列表


    /**
      * zip 操作
      * 把两个列表组合成一个对偶列表
      */
    numbers.indices zip numbers  //  ((0,1), (1,2), (2,3), (3,4))
    //等价于
    numbers.zipWithIndex  //((0,1), (1,2), (2,3), (3,4))


    /**
      * toString 和 mkString
      */
    numbers.toString

    numbers.mkString("11",",","22")  // 11,1,2,3,4,22

    val buf = new StringBuilder
    numbers addString (buf,"(",",",")")

    println(buf)  //(1,2,3,4)


    /**
      * 转换链表
      * toArray、toList
      */
    val arr = numbers.toArray

    arr.toString

    arr.toList

    //copyToArray
    val arr2 = new Array[Int](10)
    List(1,2,3) copyToArray (arr2,3)

    arr2.foreach(print _)
    println()

    //elements :枚举器访问列表元素
    val it = numbers.iterator
    println(it.next())   //1


    //================================ List 类的高阶方法======================================

    // map,flatmap,foreach
    List(1,2,3) map (_ + 1)

    val words = List("the","quick","brown","fox")
    words map (_.length)   // List(3,5,5,3)

    val res = words map (_.toList.reverse.mkString)
    println(res)  //List(eht, kciuq, nworb, xof)

    println(words map (_.toList))   //List(List(t, h, e), List(q, u, i, c, k), List(b, r, o, w, n), List(f, o, x))

    println(words flatMap (_.toList))  //List(t, h, e, q, u, i, c, k, b, r, o, w, n, f, o, x)


    //range：不包含右边的数
    println(List.range(1,5))   // 1,2,3,4

    //第三个参数指的是每隔几个数字产生一个新的数字
    println(List.range(1,9,2))   //1,3,5,7

    val res1 = List.range(1,5) flatMap (
      i => List.range(1,i) map (j => (i,j))  //i=3，返回1，2，再map, 返回 (3,1), (3,2)
    )
    println(res1)  //List((2,1), (3,1), (3,2), (4,1), (4,2), (4,3))  1 <= j < i < 5


    //使用for实现
    for(i <- List.range(1,5); j <- List.range(1,i)) yield (i,j)


    /**
      * 列表过滤，
      * filter,partition,find,takeWhile,dropWhile,span
      */
    List(1,2,3,4,5) filter ( _ % 2 == 0)  //2,4

    //partition: 返回列表对，其中一个包含所有论断为真的元素，另外一个包含所有论断为假的元素
    List(1,2,3,4,5) partition ( _ % 2 == 0)

    println(List(1,2,3,4,5) partition ( _ % 2 == 0))  //(List(2, 4),List(1, 3, 5))

    //find 只返回第一个，而非全部
    List(1,2,3,4,5) find ( _ % 2 == 0)  //Some(2)

    List(1,2,3,4,5) find ( _ < 0)        //None

    //takeWhile 返回列表中最长的能够满足论断的前缀
    List(1,2,3,-4,5) takeWhile (_ > 0)   //1,2,3

    //dropWhile 移除列表中最长的能够满足论断的前缀
    words dropWhile (_ startsWith ("t"))   //

    println(words dropWhile (_ startsWith ("t")))  //List(quick, brown, fox)

    // xs span p 等价于 (xs takeWhile p,xs dropWhile p)
    // 类似于 splitAt
    println(List(1,2,3,-4,5) span (_ > 0))   // (List(1, 2, 3),List(-4, 5))


    /**
      * 列表的论断
      * forall(所有值都满足) 和 exist (只需要一个值满足)
      */

    //判断矩阵是否有全是0的行
    def hasZeroRow(m:List[List[Int]]) =
      m exists (row => row forall (_ == 0))


    /**
      * 折叠列表  /:  和  \:
      * 左折叠操作：(z /: xs) (op) 与三个对象有关：开始值z，列表xs，以及三元操作符：op，
      * 折叠的结果是op应用到前缀值z以及每个相邻元素上；
      */

    //左折叠  /:
    def product(xs:List[Int]):Int = ( 1 /: xs) (_ + _)  // 1是初始值，(_ + _) 是操作符，xs 是列表

    println(product(List(1,2,3)))   //1 + 1 + 2 + 3

    //应用：用空格连接所有字符串列表中的单词

    ("" /: words) (_ + " " + _)

    println(("" /: words) (_ + " " + _))   //the quick brown fox

    //去掉开头的空格
    (words.head /: words.tail) (_ + " " + _)

    println((words.head /: words.tail) (_ + " " + _))    //the quick brown fox


    //使用折叠操作完成列表reverse
    def reverseLeft[T] (xs:List[T]) =
      (List[T]() /: xs) {(ys,y) => y :: ys}


    /**
      * 列表排序
      * sort
      */
    //查看第三章


    /**
      * 创建统一的列表
      * make
      */
    println(List.fill(5)("a"))      //List(a, a, a, a, a)

    /**
      * 连接列表
      * flatten concat
      */
    val xss = List(List("a","b"),List("c"),List("d","e"))

    xss.flatten
    println(xss.flatten)   //List(a, b, c, d, e)

    List.concat(List("a","b"),List("c"))

    println(List.concat(List("a","b"),List("c")))    //List(a, b, c)

  }
}

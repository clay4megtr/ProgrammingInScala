import com.sun.tools.corba.se.idl.StringGen

import scala.collection.immutable.{Queue, Stack, TreeSet}
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Iterable
  * Seq       Set      Map
  *
  * Seq: 可变和不可变序列，是有序的集合，例如数组和链表
  * Set: 集，不可重复
  * Map: 映射
  *
  * Iterable 和 Iterator 和区别：
  * Iterator 扩展的是：AnyRef
  * 特质 Iterable 指代的是可以被枚举的类型(如：集合类型)，而Iterator是用来执行枚举操作的机制，
  * 尽管 Iterable 可以被枚举若干次，但Iterator 仅能使用一次，一旦你使用Iterator 枚举了遍历了集合对象，你就不能再使用它了，
  * 如果需要再次使用，必须重新获取迭代器
  */
object program_17 {

  def main(args: Array[String]): Unit = {

    //================================ Seq ================================

    /**
      * List
      */
    val colors = List("red", "blue", "green")

    println(colors.head) //快速访问

    println(colors(1)) //遍历访问，效率低

    /**
      * 数组
      */
    val fiveInts = Array(1, 2, 3, 4, 5)

    println(fiveInts(2)) //快速访问，效率高


    /**
      * 列表缓存，ListBuffer 是可变对象，包含在scala.collection.mutable包中，
      * 可以高效的通过添加元素的方式构建列表，
      */
    val buf = new ListBuffer[Int]()
    buf += 1
    buf += 2
    println(buf)

    buf.toList

    /**
      * 数组缓存  ArrayBuffer 是可变对象
      */
    val aBuf = new ArrayBuffer[Int]()

    aBuf += 11
    aBuf += 12

    println(aBuf.length) //长度
    println(aBuf(1)) //高效访问


    /**
      * 不可变队列 （Queue）
      */
    val empty = Queue[Int]() // 必须使用伴生对象的，下面的stack同理

    //添加元素
    val has1 = empty.enqueue(1)

    //添加多个元素
    val has123 = has1.enqueue(List(2, 3))

    //移除元素
    //返回由队列头部元素和移除该元素之后的剩余队列组成的对偶
    val (element, has23) = has123.dequeue
    println(element)


    /**
      * 可变队列
      */
    val queue = new scala.collection.mutable.Queue[String]()

    queue += "a"
    queue ++= List("b", "c")

    //移除元素
    queue.dequeue() //a

    println(queue) //b,c


    /**
      * 栈
      */
    val stack = Stack[Int]()

    //推入元素
    val stack1 = stack.push(1)
    val stack12 = stack1.push(2)

    //只获取栈顶元素而不移除
    stack12.top // 2

    //移除
    stack12.pop //2

    println(stack12)


    /**
      * RichString: 可以把任何字符串当做Seq[Char]
      * String 中本身没有exists 方法，编译器把s隐式转换成了RichString，exists把字符串看做的Seq[Char]
      */
    def hasUpperCase(s: String) = s.exists(_.isUpper)


    //=============================Set 和Map ==================================


    /**
      * Set
      */
    val text = "See Spot run. Run, Spot. Run!"

    val wordArray = text.split("[ !,.]+")

    val words = scala.collection.mutable.Set.empty[String]

    for (word <- wordArray)
      words += word.toLowerCase()

    println(words.mkString(",")) //see,run,spot

    //常用操作(不可变集合)，每次操作都返回新元素
    //+ 添加元素
    //- 删除元素
    //++ 添加多个
    //-- 删除多个
    //** 获得交集
    //size 返回数量
    //contain

    //常用操作(可变集合)
    //+= 添加元素
    //-= 存在则删除
    //++= 添加多个元素
    //--= 删除多个元素
    //clear 清除所有元素


    /**
      * Map
      */
    val map = scala.collection.mutable.Map.empty[String, Int]

    map("hello") = 1
    map("there") = 2

    println(map("hello"))

    //统计字符串单词出现次数
    def countWords(text: String) = {
      val counts = scala.collection.mutable.Map.empty[String, Int]

      for (rowWord <- text.split("[ !,.]+")) {
        val word = rowWord.toLowerCase
        val oldCount =
          if (counts.contains(word)) counts(word)
          else 0
        counts += (word -> (oldCount + 1)) //添加或更新
      }

    }

    //常用方法 (不可变映射), 每次都返回新的
    // +
    // -
    // ++
    // --
    // size
    // contains
    // keys
    // keySet
    // values
    // isEmpty

    //常用方法 (可变映射)
    // +=
    // -=
    // ++=
    // --=


    /**
      * 有序的集和映射
      * TreeSet
      * TreeMap
      */

    val ts = scala.collection.immutable.SortedSet(9, 3, 1, 8, 0, 2, 7, 4, 6, 5)

    println(ts.mkString(",")) // 0,1,2,3,4,5,6,7,8,9

    var tm = scala.collection.immutable.SortedMap(3 -> "x", 1 -> "x", 4 -> "x")
    tm += (2 -> "x")

    println(tm.mkString(",")) //1 -> x,2 -> x,3 -> x,4 -> x


    /**
      * 同步的集和映射
      */

    //混入SynchronizedMap 特质
    //已经过期，建议使用java.util.concurrent.ConcurrentHashMap
    def MapMaker: mutable.Map[String, String] = {

      new mutable.HashMap[String, String] with
        mutable.SynchronizedMap[String, String] {
        override def default(key: String) =  //默认的返回值
          "why do you want to know"
      }
    }


    //============================ 可变和不可变集合 ==================================

    /**
      * 为了更易于完成从不可变集合到可变集合的转化，或反向转换，scala提供了一些语法糖，纵使不可变集合和映射并不支持真正的+=
      * 方法，scala还是为此提供了+=的语法解释，如果你写了 a += b，而a 不支持名为 += 的语法，scala将尝试把它解释为a = a+b
      *
      * 同样的理念可以应用于所有以=结尾的方法，
      */

    var people = Set("Nancy","Jane")

    people += "Bob"   //首先创建新集合，然后重新赋值
    people -= "Jane"
    people ++= List("Tom","Harry")

    println(people)


    //============================ 初始化集合 ==================================

    /**
      * 把列表中的元素保存在TreeSet中
      */

    //val treeSet = TreeSet(colors)  // 错误的写法

    val treeSet = TreeSet[String]() ++ colors    //正确的写法

    println(treeSet)   //

    /**
      * 集合和映射的可变与不可变互转，
      * 使用上面treeSet创建时的技巧，先创建空不可变对象，然后把可变集合元素用 ++ 操作符添加进去
      */

    val mutaSet = mutable.Set.empty ++ treeSet   // 不可变转为可变，

    val ummutaSet = Set.empty ++ mutaSet    // 可变转为不可变

    //同样的技巧还适用于可变map和不可变map之间的转换；


    //============================ 元祖 ==================================

    //可以保存不同类型的对象
    (1,"hello",Console)

    //找到最长单词并返回它的索引
    def logestWord(words:Array[String]) = {
      var word = words(0)
      var idx = 0

      for(i <- 1 until words.length){
        if(words(i).length > word.length){
          word = words(i)
          idx = i
        }
      }

      (word,idx)
    }

    // 函数调用
    val logest = logestWord("The quick brown fox".split(" "))

    println(logest._1)
    println(logest._2)

    val (word,idx) = logest

    println(word)
    println(idx)
  }
}

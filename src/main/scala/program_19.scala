

/**
  * 类型参数化
  */
class program_19 {

  /**
    * 开发函数式队列，不可变的，
    * 高效：head，tail，append 都应该在常量时间内完成
    * tail 返回除第一个元素之外剩余的元素组成的队列
    */
}

//1.
class SlowAppendQueue[T](elems: List[T]) {

  def head = elems.head

  def tail = new SlowAppendQueue(elems.tail)

  //append 操作花费的时间和队列数量成正比
  def append(x: T) = new SlowAppendQueue(elems ::: List(x))
}

//2.
class SlowHeadQueue[T](smele: List[T]) {

  //head和tail 花费的时间和队列数量成正比，因为smele 是逆序的
  def head = smele.last

  def tail = new SlowAppendQueue(smele.init)

  //append 是常量时间的操作
  def append(x: T) = new SlowHeadQueue(x :: smele) // :: 是放在List的第一个位置；
}


//3.
//两个List，leading 包含了前半段元素，trailing 包含了反向排列的后半段元素，
//假设队列中元素是 1,2,3,4,5,6  则leading是 1,2,3  trailing是 6,5,4
class Queue[T] private( //私有构造器，竟然这么写...， 这里屏蔽这个构造器的原因是通过两个List(还有一个反序的)不符合常规；
                        private val leading: List[T],
                        private val trailing: List[T]) {

  //第一种方法.使用辅助构造器创建队列
  def this(elems: T*) = this(elems.toList, Nil)

  /**
    * 当空的队列通过append构建出来时，trailing将不断增加，而leading始终空白，
    * 于是，在对空的leading第一次执行head或tail操作之前，trailing应该被反转，并复制给leading
    */
  private def mirror =
    if (leading.isEmpty)
      new Queue(trailing.reverse, Nil) //返回一个新的队列对象
    else
      this

  def head = mirror.leading.head

  def tail = {
    val q = mirror
    new Queue(q.leading.tail, q.trailing) //就是返回除第一个元素之后的元素组成的Queue，
  }

  def append(x: T) =
    new Queue(leading, x :: trailing) //返回一个新的 Queue 对象；
}

object Queue {

  //scala没有全局可见的方法，每个方法都必须被包含在对象或类中，然而，使用定义在全局对象中的apply方法，可以让我们看上去在调用全局方法；
  //第二种方法.使用工厂方法创建队列
  def apply[T](xs: T*) = new Queue[T](xs.toList, Nil)
}


//第三种方法：直接把类隐藏掉，只暴露接口，通过Queue1向外提供服务
/**
  * 协变：String是AnyRef的子类型，如果Queue1[String]也是Queue1[AnyRef]的子类型，就说Queue1特质与它的类型参数T是保持协变的；
  * 但是scala泛型类型默认是非协变的，也就是说默认情况下Queue1[String]不是Queue1[AnyRef]的子类型，
  * 需要使用 Queue1[+T] 来要求Queue1协变子类型化；即Queue1[String]是Queue1[AnyRef]的子类型，
  * Queue1[-T] 的意思是逆变的，即Queue1[AnyRef]是Queue1[String]的子类型  【这有什么用？】
  */
trait Queue1[+T] { //Queue1是特质，Queue1[String]是类型，所以Queue1被称为类型构造器，因为有了它，就能通过指定参数类型来构造新的类型；
  def head: T

  def tail: Queue1[T]

  //编译错误，原因：违反了规则：不允许使用+号注解的类型参数用作方法的参数类型；
  //规则的原因可见下面的：即使没有可变数据，协变仍然会带来问题
  def append(x: T): Queue1[T]
}

object Queue1 {

  def apply[T](xs: T*) = new QueueImpl[T](xs.toList, Nil)

  private class QueueImpl[T](
                              private val leading: List[T],
                              private val trailing: List[T]
                            ) extends Queue1[T] {

    def mirror =
      if (leading.isEmpty)
        new QueueImpl(trailing.reverse, Nil)
      else
        this

    def head: T = mirror.leading.head

    def tail: QueueImpl[T] = {
      val q = mirror
      new QueueImpl(q.leading.tail, q.trailing)
    }

    def append(x: T) =
      new QueueImpl(leading, x :: trailing)

  }

}


/**
  * 协变遇到可变数据会带来的问题
  */
class Cell[+T](init: T) {
  private[this] var current = init;

  def get = current

  def set(x: T) {
    current = x
  } //假如定义是Cell[+T]，也就是协变的，这里会报编译错误，原因如下
}

//下面的代码每一行都没错，但是要做的事情却是把整数1赋值给字符串s，很明显是对类型声明的破坏，
//所以 String类型的Cell并不就是Any类型的Cell，因为有些事情可以对Any类型的Cell做，但不能对String类型的Cell做，
//比如，不能以Int类型的入参调用String类型的Cell的set() 方法；这也是上面报错的原因；
val c1 = new Cell[String]("cbc")
val c2: Cell[Any] = c1
c2.set(1)
val s: String = c1.get


/**
  * 变化型和数组
  * java中的数组被认为是协变的，下面的java代码是可以编译通过的，但是 a2[0] = new Integer(17) 会抛出 ArrayStore异常，还存在这种方式主要是因为兼容性；
  * scala 不认为数组是协变的， 所以 val a2 : Array[Any] = a1 直接在编译期报错；
  */
/*String[] a1 = {"abc"}
Object[] a2 = a1;
a2[0] = new Integer(17)
String s = a1[0]*/

val a1 = Array("abc")
val a2: Array[Any] = a1 //error

//兼容java，调用asInstanceOf把Array("abc") 强转为 Array[Object]，所以仍有可能遇到ArrayStore异常
val a3: Array[Object] = a1.asInstanceOf[Array[Object]]


/**
  * 即使没有可变数据，协变仍然会带来问题
  * 假设StrangeIntQueue 是协变的
  * rule: 不允许使用+号注解的类型参数用作方法的参数类型；
  */
class StrangeIntQueue extends Queue[Int] {

  override def append(x: Int) = {
    println(Math.sqrt(x))
    super.append(x)
  }
}

//下面两行代码本身都没错，连在一起 求"abc"的平方根，就错了，
val x: Queue[Any] = new StrangeIntQueue //如果StrangeIntQueue 是协变的，这里会编译通过
x.append("abc")


/**
  * 下界
  * 协变和下界结合在一起，可以提供更灵活的动作行为
  */
class Queue2[+T](   //协变的
                 private val leading: List[T],
                 private val trailing: List[T]) {

  def this(elems: T*) = this(elems.toList, Nil)

  def append[U >: T](x: U) =   //定义T为U的下界，所以U必须是T的超类型，返回值也是返回超类 Queue2[U]
    new Queue2[U](leading, x :: trailing)
}
val x1: Queue2[Any] = new Queue2[Int]()
val x2:Queue2[Any] = x1.append("abc")


/**
  * 逆变
  * 考虑一下为什么；
  * 考虑一下我们能对OutputChannel[String]做什么，唯一支持的操作就是写一个String给他，而同样的操作对于OutputChannel[AnyRef]
  * 来说也支持，因此用OutputChannel[AnyRef]替代OutputChannel[String]是安全的，相反，在需要OutputChannel[AnyRef]的地方替换成
  * OutputChannel[String]却是不安全的，毕竟你可以把任何对象发送给OutputChannel[AnyRef]，而OutputChannel[String]要求写入的值只能是字符串；
  *
  * 所以这里用逆变是合适的；
  */
trait OutputChannel[-T]{
  def write(x: T)
}


/**
  * 协变和逆变结合使用
  * 我们编写函数类型 A => B 的时候，scala会把它扩展为Function1[A,B]
  * 可以看一下源码 trait Function1[-S, +T] ,参数类型S是逆变的，结果类型T是协变的，原因可以看下面的分析
  */
class Publication(val title: String)
class Book(title: String) extends Publication(title)

object Library{
  val books:Set[Book] =
    Set(
      new Book("programming in scala"),
      new Book("walden")
    )

  def printBookList(info: Book => AnyRef) ={
    //调用toString方法并打印，这个行为对于String及所有AnyRef子类的对象都有效，这就是返回结果类型为协变的意义；
    for(book <- books) println(info(book))
  }
}

object Customer{

  def main(args: Array[String]): Unit = {

    //printBookList需要的函数值是参数为Book类型的，而传递的getTitle的参数是Publication，是Book的超类型，
    //printBookList接收一个函数值，这个函数值的参数是Book类型，所以只能操作一个Book对象
    //getTitle这个函数字面量中只能操作Publication对象，
    //又因为任何声明在 Publication 内的方法在他的子类Book中都有效，所以不管在getTitle中调用Publication的什么方法；
    //在printBookList中调用info这个函数值对Book做同样操作的时候，都是同样有效的，这就是参数类型是逆变的意义；
    def getTitle(p: Publication):String = p.title

    /**
      * 这里的参数要求是info: Book => AnyRef，参数是Book，返回值是AnyRef，
      * 而实际传递的函数值的返回值是String，这也印证了结果类型是协变的这一事实，
      */
    Library.printBookList(getTitle)
  }
}




object program_19 {

  def main(args: Array[String]): Unit = {

    //new Queue[Int](List(1,2),List(3))  错误，私有构造器


  }
}
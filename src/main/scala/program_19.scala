

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
trait Queue1[+T] {  //Queue1是特质，Queue1[String]是类型，所以Queue1被称为类型构造器，因为有了它，就能通过指定参数类型来构造新的类型；
  def head: T

  def tail: Queue1[T]

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
      new QueueImpl(q.leading.tail,q.trailing)
    }

    def append(x: T) =
      new QueueImpl(leading,x :: trailing)

  }
}


/**
  * 协变遇到可变数据会带来的问题
  */
class Cell[+T](init: T){
  private[this] var current = init;
  def get = current
  def set(x: T){current = x}  //假如定义是Cell[+T]，也就是协变的，这里会报编译错误，原因如下
}

//下面的代码每一行都没错，但是要做的事情却是把整数1赋值给字符串s，很明显是对类型声明的破坏，
//所以 String类型的Cell并不就是Any类型的Cell，因为有些事情可以对Any类型的Cell做，但不能对String类型的Cell做，
//比如，不能以Int类型的入参调用String类型的Cell的set() 方法；这也是上面报错的原因；
val c1 = new Cell[String]("cbc")
val c2: Cell[Any] = c1
c2.set(1)
val s:String = c1.get


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
val a2 : Array[Any] = a1  //error




object program_19 {

  def main(args: Array[String]): Unit = {

    //new Queue[Int](List(1,2),List(3))  错误，私有构造器


  }
}
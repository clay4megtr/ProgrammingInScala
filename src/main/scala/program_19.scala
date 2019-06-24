

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
  def append(x: T) = new SlowHeadQueue(x :: smele)   // :: 是放在List的第一个位置；
}


//3.
//两个List，leading 包含了前半段元素，trailing 包含了反向排列的后半段元素，
//假设队列中元素是 1,2,3,4,5,6  则leading是 1,2,3  trailing是 6,5,4
class Queue[T] private (  //私有构造器
                          private val leading: List[T],
                          private val trailing: List[T]) {

  //辅助构造器
  //def this(elems: T*) = this(elems.toList,Nil)

  /**
    * 当空的队列通过append构建出来时，trailing将不断增加，而leading始终空白，
    * 于是，在对空的leading第一次执行head或tail操作之前，trailing应该被反转，并复制给leading
    */
  private def mirror =
    if(leading.isEmpty)
      new Queue(trailing.reverse,Nil)  //返回一个新的队列对象
    else
      this

  def head = mirror.leading.head

  def tail = {
    val q = mirror
    new Queue(q.leading.tail,q.trailing)
  }

  def append(x: T) =
    new Queue(leading,x :: trailing)
}

object Queue{

  //工厂方法
  //def apply[T](xs: T*) = new Queue[T](xs.toList,Nil)
}


object program_19{

  def main(args: Array[String]): Unit = {

    //new Queue[Int](List(1,2),List(3))  错误，私有构造器


  }
}
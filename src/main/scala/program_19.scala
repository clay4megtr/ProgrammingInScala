

/**
  * 类型参数化
  */
class program_19 {

  /**
    * 开发函数式队列，不可变的，
    * 高效：head，tail，append 都应该在常量时间内完成
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
  def head = smele.head

  def tail = new SlowAppendQueue(smele.tail)

  //append 是常量时间的操作
  def append(x: T) = new SlowHeadQueue(x :: smele)
}


//3.
//两个List，leading包含了前半段元素，trailing包含了反向排列的后半段元素，
class Queue[T] private (  //私有构造器
                          private val leading: List[T],
                          private val trailing: List[T]) {

  //辅助构造器
  //def this(elems: T*) = this(elems.toList,Nil)

  private def mirror =
    if(leading.isEmpty)
      new Queue(trailing.reverse,Nil)
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
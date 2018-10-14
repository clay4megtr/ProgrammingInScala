
/**
  * 第12章：特质
  * 类可以混入多个特质
  *
  * 最常用的两种方式：
  * 1.扩宽瘦接口为胖接口: 也就是说trait中可以有具体的实现的一个应用，不用都定义为抽象方法
  * 2.定义可堆叠的改变(trait 这种特别的super调用的实现原理是线性化，原理可自行google)
  *
  * 使用场景
  * 1.如果行为不会被重用，就把它做成具体的类
  * 2.如果要在多个不相关的类中重用，就做成特质，只有特质可以混入到不同的类层级中；
  * 3.如果你想在java代码中继承它，就定义抽象类，因为trait和java没有相似的模拟，
  *   一个例外：如果特质中只含有抽象成员，java中将直接翻译成接口，此时也可以使用特质
  */

trait Philosophical{

  def philosophical(): Unit ={
    println("Philosophical's philosophical")
  }
}

class Animal
trait HasLegs

/**
  * 使用extends 混入特质的情况下：隐式的继承了特质的超类
  */
class Frog extends Animal with Philosophical with HasLegs {

  override def toString: String = "green"

  override def philosophical(): Unit = {
    println("Frog's philosophical")
  }
}


/**
  * ordered 特质。混入ordered 特质，实现compare方法，即可使用全部 < > = 等比较方法
  */


/**
  * ======================可堆叠的改变============================================
  */
abstract class IntQueue{

  def get():Int
  def put(x:Int)
}

class BasicQueue extends IntQueue{

  private val buf = new scala.collection.mutable.ArrayBuffer[Int]

  override def get(): Int = buf.remove(0)
  override def put(x: Int): Unit = buf += x
}

trait Doubling extends IntQueue{
  abstract override def put(x: Int): Unit = {super.put(2 * x)}    //这里竟然有一个super调用
}

/**
  * 解释：
  * Doubling 中的super调用对于普通的类来说，是非法的，因为IntQueue 中根本没有put的实现，但是trait的super调用是动态绑定的，
  * 它的super方法将直到被混入另一个特质或类之后，有了具体的方法定义时才工作
  * 为了告诉编译器你的目的，必须对这种话方法打上 abstract override 的标志，这种标识符的组合仅在特质成员的定义中被认可，在类中则不行，
  * 它意味着特质必须被混入某个具有期待方法的具体定义的类中
  *
  * 所以这里调用 MyQueue.put() 方法的时候，调用的是Doubling的方法
  */
class MyQueue extends BasicQueue with Doubling


trait Incrementing extends IntQueue{
  abstract override def put(x:Int) = {super.put(x+1)}
}

trait Filtering extends IntQueue{
  abstract override def put(x:Int) = {
    if (x > 0) super.put(x)
  }
}


/**
  * 既能过滤负数，又对每个进队列的数字+1
  * 混入的次序非常重要，粗略的说：越靠近右侧的特质越先起作用，
  * 所以结果是：先过滤队列的负数，然后再把队列中的数都加1
  */
class MyQueue1 extends BasicQueue with Incrementing with Filtering


//============================================================================
/**
  * 结论：特质就像是带有具体方法的Java接口
  * 实际上可以用特质的定义做任何用类定义能做的事情，除了以下两点
  *   1. 特质不能有任何 "类" 参数：  trait NoPoint(x:Int, y:Int)  是错误的
  *   2. super是动态绑定的，在定义特质的时候，定义super.toString，此时super调用的方法尚未被定义，调用的实现将在每一次特质被混入到具体类的
  *      时候才被决定，这种处理super的有趣的行为是使得特质能以可堆叠的改变方式工作的关键
  *
  */
object program_12 {

  val frog = new Frog()
  frog.philosophical()

  //特质也是类型
  val phil:Philosophical = frog
  phil.philosophical()   //打印 Frog's philosophical ,和多态保持一致

  val queue = new MyQueue

  val queue1 = new MyQueue1
  queue1.put(-1); queue1.put(0);queue1.put(1)
  queue1.get()   //1
  queue1.get()   //2
}

import Element.elem
/**
  * 第10章： 组合与继承
  */
abstract class Element{

  //注意contents 虽然没有使用abstract 修饰，但它也是抽象的
  //scala中一个方法只要没有实现(即没有等号和方法体)，它就是抽象的
  def contents:Array[String]

  /**
    * 竖着摞的，所有数组的长度就代表高度，数组第一个元素的长度就代表宽度
    */
  def height:Int = contents.length
  def width:Int = if(height == 0) 0 else contents(0).length

  /*def demo(): Unit ={
    println(" Element demo")
  }*/

  def above(that:Element):Element =
    elem(this.contents ++ that.contents)   // ++ 只能连接两个相等的数组

  def beside(that:Element):Element ={
    elem(
      for(
      (line1,line2) <- this.contents zip that.contents    //1,2,3   zip   a,b  =>  (1,a),(2,b)
      ) yield line1 + line2
    )
  }

  override def toString: String = contents mkString "\n"
}


/**
  * Element 的伴生对象
  */
object Element{

  //=============设置为private，只能通过工厂方法调用========
  /**
    * 顶层超类是AnyRef，地位和Java中的 Object 类型相同
    * 超类中的私有成员不会被子类继承
    */
  private class ArrayElement(conts:Array[String]) extends Element{

    //scala 中的字段和方法属于相同的命名空间，所以字段可以重写无参数方法
    val contents: Array[String] = conts     //这样写是可以的

    //final 标记，禁止重写
    /*final override def demo(): Unit = {
      println(" ArrayElement demo")
    }*/

  }

  /**
    * 创造由给定的单行字符串构成的布局元素
    * 要调用超类构造器，只需要简单的把要传递的参数或参数列表放在超类名之后的括号内即可
    */
  private class LineElement(s:String) extends Element{

    val contents = Array(s)

    //scala 要求：若子类成员重写了父类的具体成员，则必须带有override修饰符
    override def height = 1

    override def width = s.length

  }


  /**
    * 定义拥有给定长度和高度，并充满指定字符的新的 Element 形式
    */
  private class UniformElement(
                        ch:Char,
                        override val width:Int,
                        override val height:Int
                      )extends Element{
    private val line = ch.toString * width

    def contents = Array.fill(height)(line)
  }


  def elem(contents:Array[String]): Element =
    new ArrayElement(contents)

  def elem(chr:Char, width:Int, height:Int):Element =
    new UniformElement(chr,width,height)

  def elem(line:String):Element =
    new LineElement(line)
}


object program_10 {

  def main(args: Array[String]): Unit = {

    /*val e1: Element = new ArrayElement(Array("hello","world"))
    val ae:ArrayElement = new LineElement("hello")
    val e2:Element = ae
    val e3:Element = new UniformElement('x',2,3)*/

    val x = elem(Array("hello")) above elem(Array("world!"))
    x.contents.foreach(println)

    val y = elem(Array("one","two")) beside elem(Array("one"))
    y.contents.foreach(println)

  }

}

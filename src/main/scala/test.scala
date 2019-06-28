
object test {

  def main(args: Array[String]): Unit = {

    val list1 = List(1,2,3)
    val list2 = List(4,5,6)

    val mm = 7

    val list3 = 7 :: list2

    list3.foreach(println _)

  }
}

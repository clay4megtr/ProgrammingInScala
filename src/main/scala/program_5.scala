
object program_5 {

  def main(args: Array[String]): Unit = {

    println(
      """|Welcome to Ultamix 3000
        |Type "help" for help
      """.stripMargin)

    val s = "Hello World"
    println(s indexOf('o'))


    /**
      * scala 的 == 既可以比较原始类型，又可以比较引用类型，
      * scala 用于判断两个变量是否指向了同一内存的方法是eq，eq的反义词是ne
      */
    println("aaa" == "aaa")


    /**
      * 富包装器，每个基本类型都对应一个，它提供了许多的额外方法，
      * 0 max 5
      * -2.7 abs
      * 4 to 6  返回Range(4,5,6)
      * "bob" capitalize    返回 "Bob"
      * "robert" drop 2   返回"bert"
      */
  }
}

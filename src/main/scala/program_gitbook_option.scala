
object program_gitbook_option {

  /**
    * Option
    * Option[A] 是一个类型为 A 的可选值的容器： 如果值存在， Option[A] 就是一个 Some[A] ，如果不存在， Option[A] 就是对象 None 。
    * Option 是强制的！不要使用 null 来表示一个值是缺失的。
    */

  //创建方式
  val greeting: Option[String] = Some("Hello world")

  //或者为None
  val greeting1: Option[String] = None

  val absentGreeting: Option[String] = Option(null) // absentGreeting will be None
  val presentGreeting: Option[String] = Option("Hello!") // presentGreeting will be Some("Hello!")


  //案例研究
  case class User(
                   id: Int,
                   firstName: String,
                   lastName: String,
                   age: Int,
                   gender: Option[String]
                 )

  object UserRepository {
    private val users = Map(1 -> User(1, "John", "Doe", 32, Some("male")),
      2 -> User(2, "Johanna", "Doe", 30, None))

    def findById(id: Int): Option[User] = users.get(id)

    def findAll = users.values
  }

  //处理Option[User] 方式一 不推荐
  val user1 = UserRepository.findById(1)
  if (user1.isDefined) {  //可能会忘记
    println(user1.get.firstName)
  } // will print "John"


  val user = User(2, "Johanna", "Doe", 30, None)
  println("Gender: " + user.gender.getOrElse("not specified")) // will print "not specified" 指定一个默认值

  //处理Option[User] 方式二：模式匹配 不推荐
  //模式匹配,你可能已经发现用模式匹配处理 Option 实例是非常啰嗦的，这也是它非惯用法的原因。 所以，即使你很喜欢模式匹配，也尽量用其他方法吧。
  //Some 是一个样例类，可以出现在模式匹配表达式或者其他允许模式出现的地方。 上面的例子可以用模式匹配来重写：
  val user2 = User(2, "Johanna", "Doe", 30, None)
  user2.gender match {
    case Some(gender) => println("Gender: " + gender)
    case None => println("Gender: not specified")
  }

  //处理Option[User] 方式三  推荐, 通过集合的方式
  //优雅使用 Option 的方式
  //1. 在 Option 值存在的时候执行某个副作用
  UserRepository.findById(2).foreach(user => println(user.firstName)) // prints "Johanna"  //有值就调用一次，否则不调用

  //2. 像使用集合一样使用map，flatmap等
  val age = UserRepository.findById(1).map(_.age) // age is Some(32)

  val gender = UserRepository.findById(1).map(_.gender) // gender is an Option[Option[String]]

  //既然可以 flatMap 一个 List[List[A]] 到 List[B] ，那么也就可以 flatMap 一个 Option[Option[A]] 到 Option[B] ，这没有任何问题： Option 提供了 flatMap 方法。
  val gender1 = UserRepository.findById(1).flatMap(_.gender) // gender is Some("male")
  val gender2 = UserRepository.findById(2).flatMap(_.gender) // gender is None
  val gender3 = UserRepository.findById(3).flatMap(_.gender) // gender is None

  // but why?
  //flatmap 工作原理
  val names: List[List[String]] =
    List(List("John", "Johanna", "Daniel"), List(), List("Doe", "Westheide"))

  names.map(_.map(_.toUpperCase))  // results in List(List("JOHN", "JOHANNA", "DANIEL"), List(), List("DOE", "WESTHEIDE"))

  //如果我们使用 flatMap，内部列表中的所有元素会被转换成一个扁平的字符串列表。显然，如果内部列表是空的，则不会有任何东西留下。
  names.flatMap(_.map(_.toUpperCase))  //results in List("JOHN", "JOHANNA", "DANIEL", "DOE", "WESTHEIDE")

  //回到Option类型
  //使用 flatMap 时，内部集合的元素就会被放到一个扁平的列表里： 任何一个 Some[String] 里的元素都会被解包，放入结果集中；原列表中的 None 值由于不包含任何元素，就直接被过滤出去了。
  val names1: List[Option[String]] = List(Some("Johanna"), None, Some("Daniel"))
  names1.map(_.map(_.toUpperCase)) // List(Some("JOHANNA"), None, Some("DANIEL"))
  names1.flatMap(xs => xs.map(_.toUpperCase)) // List("JOHANNA", "DANIEL")


  //过滤 Option
  UserRepository.findById(1).filter(_.age > 30) // None, because age is <= 30
  UserRepository.findById(2).filter(_.age > 30) // Some(user), because age is > 30
  UserRepository.findById(3).filter(_.age > 30) // None, because user is already None

  //for 语句

  //等同于上面嵌套的 flatMap 调用
  for {
    user <- UserRepository.findById(1)
    gender <- user.gender
  } yield gender // results in Some("male")

  //返回用户的性别
  for {
    user <- UserRepository.findAll
    gender <- user.gender
  } yield gender
  // result in List("male")


  //在生成器左侧使用 for 语句中生成器的左侧也是一个模式
  for {
    User(_, _, _, _, Some(gender)) <- UserRepository.findAll
  } yield gender


  //链接 Option  orElse
  case class Resource(content: String)
  val resourceFromConfigDir: Option[Resource] = None
  val resourceFromClasspath: Option[Resource] = Some(Resource("I was found on the classpath"))
  val resource = resourceFromConfigDir orElse resourceFromClasspath
}

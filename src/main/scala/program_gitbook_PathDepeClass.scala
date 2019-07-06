
object program_gitbook_PathDepeClass {

  object Franchise {

    //一个系列里面可以创建多个角色
    case class Character(name: String)
  }

  //系列(漫威系列、 DC系列 等)
  class Franchise(name: String) {
    import Franchise.Character

    //创建有关两个角色的约会
    def createFanFiction(
                          lovestruck: Character,
                          objectOfDesire: Character): (Character, Character) = {
      //require(lovestruck.franchise == objectOfDesire.franchise)  快速失败
      (lovestruck, objectOfDesire)
    }
  }

  //两个系列
  val Marvel = new Franchise("Marvel")
  val DC = new Franchise("DC")

  //漫威系列
  val tony = Franchise.Character("Tony Stark")
  val peper = Franchise.Character("peper")

  //DC系列
  val man = Franchise.Character("Super Man")
  val woman = Franchise.Character("Wonder Woman")

  //tony和神奇女侠约会???
  Marvel.createFanFiction(lovestruck = tony, objectOfDesire = woman)


  /**
    * 比require 快速失败更好的解决办法
    * 路径依赖类型,在编译期解决这个问题
    */
  //Scala 嵌套类型 工作的方式允许我们这样做。一个嵌套类型被绑定在一个外层类型的实例上，而不是外层类型本身。
  //这意味着，如果将内部类型的一个实例用在包含它的外部类型实例外面，会出现编译错误：
  class A {
    class B
    var b: Option[B] = None
  }
  val a1 = new A
  val a2 = new A
  val b1 = new a1.B
  val b2 = new a2.B
  a1.b = Some(b1)
  a2.b = Some(b1) // does not compile

  //不能简单的将绑定在a1上的类型B的实例赋值给a2上的字段：前者的类型是a1.B，后者的类型是a2.B。中间的点语法代表类型的路径，这个路径通往其他类型的具体实例。因此命名为路径依赖类型。

  //使用路径依赖类型解决上面的问题
  class Franchise1(name: String) {
    case class Character1(name: String) //类型Character1嵌套在Franchise1里，它依赖于一个特定的Franchise1实例。
    def createFanFictionWith(
                              lovestruck: Character1,
                              objectOfDesire: Character1): (Character1, Character1) = (lovestruck, objectOfDesire)

    //假设下面这个方法不是在Franchise1中定义的，这项技术同样可用。这种情况下，可以使用依赖方法类型，一个参数的类型信息依赖于前面的参数。
    def createFanFiction(f: Franchise1)(lovestruck: f.Character1, objectOfDesire: f.Character1) =
      (lovestruck, objectOfDesire)
  }

  //两个系列
  val Marvel1 = new Franchise1("Marvel")
  val DC1 = new Franchise1("DC")

  //漫威系列
  val tony1 = Marvel1.Character1("Tony Stark")
  val peper1 = Marvel1.Character1("peper")

  //DC系列
  val man1 = DC1.Character1("Super Man")
  val woman1 = DC1.Character1("Wonder Woman")

  //tony和peper约会
  Marvel1.createFanFictionWith(lovestruck = tony1, objectOfDesire = peper1)

  //tony和神奇女侠约会，type mismatch
  //Marvel1.createFanFictionWith(lovestruck = tony1, objectOfDesire = woman1)


  /**
    * 抽象类型成员
    */
  //类型安全的键值存储
  object AwesomeDB {
    abstract class Key(name: String) {
      type Value
    }
  }
  import AwesomeDB.Key
  class AwesomeDB {
    import collection.mutable.Map
    val data = Map.empty[Key, Any]
    def get(key: Key): Option[key.Value] = data.get(key).asInstanceOf[Option[key.Value]]
    def set(key: Key)(value: key.Value): Unit = data.update(key, value)
  }

  //定义一些想使用的具体的键：
  trait IntValued extends Key {
    type Value = Int
  }
  trait StringValued extends Key {
    type Value = String
  }
  object Keys {
    val foo = new Key("foo") with IntValued
    val bar = new Key("bar") with StringValued
  }

  //可以存放键值对
  val dataStore = new AwesomeDB
  dataStore.set(Keys.foo)(23)
  val i: Option[Int] = dataStore.get(Keys.foo)
  //dataStore.set(Keys.foo)("23") // does not compile

}

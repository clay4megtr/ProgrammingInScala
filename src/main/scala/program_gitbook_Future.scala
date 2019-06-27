
object program_gitbook_Future {

  /**
    * Future的语义：
    * Future 只能写一次：当一个future完成后，它就不能再被改变了。同时，Future只提供了读取计算值的接口，写入计算值的任务交给了 Promise，这样，API 层面上会有一个清晰的界限。
    */

  // Some type aliases, just for getting more meaningful method signatures:
  type CoffeeBeans = String
  type GroundCoffee = String

  case class Water(temperature: Int)

  type Milk = String
  type FrothedMilk = String
  type Espresso = String
  type Cappuccino = String

  //研磨异常
  case class GrindingException(msg: String) extends Exception(msg)

  import scala.concurrent.Future

  //Future 的隐式参数，可以看做是一个线程池
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.util.Random

  //研磨咖啡豆
  def grind(beans: CoffeeBeans): Future[GroundCoffee] = Future {
    println("start grinding...")
    Thread.sleep(Random.nextInt(2000))
    if (beans == "baked beans") throw GrindingException("are you joking?")
    println("finished grinding...")
    s"ground coffee of $beans"
  }

  //烧水
  //Future 后面使用{}的原因是 Future 的第一个参数只有一个参数
  def heatWater(water: Water): Future[Water] = Future {
    println("heating the water now")
    Thread.sleep(Random.nextInt(2000))
    println("hot, it's hot!")
    water.copy(temperature = 85)
  }

  //打奶泡
  def frothMilk(milk: Milk): Future[FrothedMilk] = Future {
    println("milk frothing system engaged!")
    Thread.sleep(Random.nextInt(2000))
    println("shutting down milk frothing system")
    s"frothed $milk"
  }

  //咖啡粉和热水做成浓咖啡
  def brew(coffee: GroundCoffee, heatedWater: Water): Future[Espresso] = Future {
    println("happy brewing :)")
    Thread.sleep(Random.nextInt(2000))
    println("it's brewed!")
    "espresso"
  }

  //浓咖啡和奶泡做成卡布奇诺
  def combine(espresso: Espresso, frothedMilk: FrothedMilk): Cappuccino = "cappuccino"


  /**
    * 回调,偏函数的方式
    */
  import scala.util.{Success, Failure}

  grind("baked beans").onComplete {
    case Success(ground) => println(s"got my $ground")
    case Failure(ex) => println("This grinder needs a replacement, seriously!")
  }


  /**
    * Future的组合
    */

  //Map 操作；实现在Future[T]执行过程中，转化为另外一个Future[V]
  //烧水的结果传递给map的时候，时间在未来，或者说可能的未来，如果在烧水过程中给出现异常，那么此时调用map，将是一个失败的 Future[Boolean]
  val tempreatureOkay: Future[Boolean] = heatWater(Water(25)) map { water =>
    println("we're in the future!")
    (80 to 85) contains (water.temperature)
  }

  def temperatureOkay(water: Water): Future[Boolean] = Future {
    (80 to 85) contains (water.temperature)
  }

  //FlatMap 操作,适用于一个Future的计算依赖于另一个Future的结果的情况，可以避免 Future 的嵌套
  val nestedFuture: Future[Future[Boolean]] = heatWater(Water(25)) map {
    water => temperatureOkay(water)
  }

  val flatFuture: Future[Boolean] = heatWater(Water(25)) flatMap {
    water => temperatureOkay(water)
  }

  //for操作，重写上面的例子
  val acceptable: Future[Boolean] = for {
    heatedWater <- heatWater(Water(25))
    okay <- temperatureOkay(heatedWater)
  } yield okay

  //如果有多个可以并行执行的计算，则需要特别注意，要先在 for 语句外面创建好对应的 Futures。
  //下面的写法看起来很漂亮，但是会导致过程变为串行执行的过程，因为for语句只不过是flatMap嵌套调用的语法糖。
  //这意味着，只有当 Future[GroundCoffee] 成功完成后， heatWater 才会创建 Future[Water]。 可以查看函数运行时打印出来的东西来验证这个说法。
  def prepareCappuccinoSequentially(): Future[Cappuccino] =
    for {
      ground <- grind("arabica beans")
      water <- heatWater(Water(25))
      foam <- frothMilk("milk")
      espresso <- brew(ground, water)
    } yield combine(espresso, foam)

  //正确的写法是：确保在 for 语句之前实例化所有相互独立的 Futures
  def prepareCappuccino(): Future[Cappuccino] = {
    val groundCoffee = grind("arabica beans")
    val heatedWater = heatWater(Water(20))
    val frothedMilk = frothMilk("milk")
    for {
      ground <- groundCoffee
      water <- heatedWater
      foam <- frothedMilk
      espresso <- brew(ground, water) //这个肯定在上面三个操作的后面，因为它的Future没有定义在外面
    } yield combine(espresso, foam)
  }


  /**
    * 失败偏向的 Future
    * 默认Future[T]是成功偏向的，允许你使用 map、flatMap、filter 等。
    * 但是，有时候可能处理事情出错的情况。调用 Future[T]上的 failed 方法，会得到一个失败偏向的 Future，类型是 Future[Throwable]。
    * 之后就可以映射这个 Future[Throwable]，在失败的情况下执行 mapping 函数。
    */

}
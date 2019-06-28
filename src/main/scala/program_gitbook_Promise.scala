import scala.concurrent.{Future}

object program_gitbook_Promise {

  /**
    * 类型 Promise
    * 是除了Future apply的工厂方法之外，另外一种构建Future的方式，
    * 而且只能放一次
    * 一个 Future 实例总是和一个（也只能是一个）Promise 实例关联在一起。 如果你在 REPL 里调用 future 方法，你会发现返回的也是一个 Promise：
    */


  import concurrent.ExecutionContext.Implicits.global
  val f: Future[String] = Future { "Hello World!" }  //f: scala.concurrent.Future[String] = scala.concurrent.impl.Promise$DefaultPromise@2b509249


  /**
    * 给出承诺
    */

  import concurrent.Promise
  case class TaxCut(reduction: Int)  //减税
  // either give the type as a type parameter to the factory method:
  val taxcut = Promise[TaxCut]()
  // or give the compiler a hint by specifying the type of your val:
  val taxcut2: Promise[TaxCut] = Promise()
  // taxcut: scala.concurrent.Promise[TaxCut] = scala.concurrent.impl.Promise$DefaultPromise@66ae2a84
  // taxcut2: scala.concurrent.Promise[TaxCut] = scala.concurrent.impl.Promise$DefaultPromise@346974c6

  // 一旦创建了这个 Promise，就可以在它上面调用 future 方法来 **获取承诺的未来**;
  // 在同一个 Promise 上调用 future 方法总是返回同一个对象;以确保 Promise 和 Future 之间一对一的关系
  val taxCutF: Future[TaxCut] = taxcut.future
  // `> scala.concurrent.Future[TaxCut] `  scala.concurrent.impl.Promise$DefaultPromise@66ae2a84


  /**
    * 兑现承诺(成功结束一个 Promise)
    */

  //这样做之后，Promise 就无法再写入其他值了，如果再写，会产生异常。
  //此时，和 Promise 关联的 Future 也成功完成，注册的回调会开始执行，
  taxcut.success(TaxCut(20))

  //一般来说，Promise 的完成和对返回的Future的处理发生在不同的线程。 很可能你创建了Promise，并立即返回和它关联的Future给调用者，而实际上，另外一个线程还在计算它。
  object Government {
    def redeemCampaignPledge(): Future[TaxCut] = {
      val p = Promise[TaxCut]() //Promise 并不是在调用者的线程里完成的。
      Future {
        println("Starting the new legislative period.")
        Thread.sleep(2000)
        p.success(TaxCut(20))
        println("We reduced the taxes! You must reelect us!!!!1111")
      }
      p.future //这个方法调用完之后，Future[TaxCut]就立即返回了；Promise的执行是在另外一个线程；
    }
  }

  //添加 onComplete 回调
  import scala.util.{Success, Failure}
  val taxCutF1: Future[TaxCut] = Government.redeemCampaignPledge()
  println("Now that they're elected, let's see if they remember their promises...")
  taxCutF1.onComplete {
    case Success(TaxCut(reduction)) =>
      println(s"A miracle! They really cut our taxes by $reduction percentage points!")
    case Failure(ex) =>
      println(s"They broke their promises! Again! Because of a ${ex.getMessage}")
  }


  /**
    * 违背诺言
    */

  //调用 failure 方法
  case class LameExcuse(msg: String) extends Exception(msg)
  object Government1 {
    def redeemCampaignPledge(): Future[TaxCut] = {
      val p = Promise[TaxCut]()
      Future {
        println("Starting the new legislative period.")
        Thread.sleep(2000)
        p.failure(LameExcuse("global economy crisis"))
        println("We didn't fulfill our promises, but surely they'll understand.")
      }
      p.future
    }
  }

  /**
    * 基于 Future 的编程实践
    */
  //如果想使用基于 Future 的编程范式以增加应用的扩展性，那应用从下到上都必须被设计成非阻塞模式。 这意味着，基本上应用层所有的函数都应该是异步的，并且返回 Future。

  //到现在为止，我们都是使用隐式可用的全局 ExecutionContext 来执行这些代码块。 通常，更好的方式是创建一个专用的 ExecutionContext 放在数据库层里。
  // 可以从 Java的 ExecutorService 来它，这也意味着，可以异步的调整线程池来执行数据库调用，应用的其他部分不受影响。
  import java.util.concurrent.Executors
  import concurrent.ExecutionContext
  val executorService = Executors.newFixedThreadPool(4)
  val executionContext = ExecutionContext.fromExecutorService(executorService)

  //也就是说，每个会阻塞程序的操作都应该使用Future的模式；而且不同性质的阻塞应该使用不同的 ExecutorService 来执行；

}

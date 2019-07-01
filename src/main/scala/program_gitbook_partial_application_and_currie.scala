
object program_gitbook_partial_application_and_currie {

  /**
    * 部分应用的函数
    */

  //DRY的例子
  case class Email(
                    subject: String,
                    text: String,
                    sender: String,
                    recipient: String)
  type EmailFilter = Email => Boolean


  type IntPairPred = (Int, Int) => Boolean     //该函数接受一对整数（邮件内容长度和值n）
  def sizeConstraint(pred: IntPairPred, n: Int, email: Email) =
    pred(email.text.size, n)

  //遵循 DRY 原则，先定义常用的 IntPairPred 实例
  val gt: IntPairPred = _ > _
  val ge: IntPairPred = _ >= _
  val lt: IntPairPred = _ < _
  val le: IntPairPred = _ <= _
  val eq: IntPairPred = _ == _

  //调用sizeConstraint，生成部分应用函数
  //对所有没有传入值的参数，必须使用占位符 _ ，还需要指定这些参数的类型，
  val minimumSize: (Int, Email) => Boolean = sizeConstraint(ge, _: Int, _: Email)
  val maximumSize: (Int, Email) => Boolean = sizeConstraint(le, _: Int, _: Email)

  //可以绑定或漏掉任意个、任意位置的参数
  val constr20: (IntPairPred, Email) => Boolean =
    sizeConstraint(_: IntPairPred, 20, _: Email)

  val constr30: (IntPairPred, Email) => Boolean =
    sizeConstraint(_: IntPairPred, 30, _: Email)

  //总结：虽然函数部分应用看起来比较冗长，但它要比 Clojure 的灵活，在 Clojure 里，必须从左到右的传递参数，不能略掉中间的任何参数

  /**
    * 从方法到函数对象 (函数值)
    */
  //在一个方法上做部分应用时，可以不绑定任何的参数，这样做的效果是产生一个函数对象，并且其参数列表和原方法一模一样。通过这种方式可以将方法变成一个可赋值、可传递的函数！
  val sizeConstraintFn: (IntPairPred, Int, Email) => Boolean = sizeConstraint _


  /**
    * 上面的函数看起来不够优雅
    * 另一种方式：函数柯里化
    */
  def sizeConstraint(pred: IntPairPred)(n: Int)(email: Email): Boolean =
    pred(email.text.size, n)

  //如果把它变成一个可赋值、可传递的函数对象，它的签名看起来会像是这样：
  //sizeConstraintFn 接受一个 IntPairPred ，返回一个函数，这个函数又接受 Int 类型的参数，返回另一个函数，最终的这个函数接受一个 Email ，返回布尔值。
  val sizeConstraintFn1: IntPairPred => Int => Email => Boolean = sizeConstraint _

  //构建柯里化函数,被留空的参数没必要使用占位符，因为这不是部分函数应用。
  val minSize: Int => Email => Boolean = sizeConstraint(ge)
  val maxSize: Int => Email => Boolean = sizeConstraint(le)

  //通过这两个柯里化函数来创建 EmailFilter 谓词：
  val min20: Email => Boolean = minSize(20)
  val max20: Email => Boolean = maxSize(20)

  //也可以在柯里化的函数上一次性绑定多个参数，直接得到上面的结果。传入第一个参数得到的函数会立即应用到第二个参数上：
  val min201: Email => Boolean = sizeConstraint(ge)(20)
  val max201: Email => Boolean = sizeConstraint(le)(20)

  //把一个第三方函数柯里化
  val sum: (Int, Int) => Int = _ + _
  val sumCurried: Int => Int => Int = sum.curried

  //柯里化函数转换成非柯里化
  //使用 Funtion.uncurried 进行反向操作


  /**
    * 函数化的依赖注入
    * ioc？
    */
  case class User(name: String)

  //下面两个 Repository 接口为依赖；
  trait EmailRepository {
    def getMails(user: User, unread: Boolean): Seq[Email]
  }
  trait FilterRepository {
    def getEmailFilter(user: User): EmailFilter
  }

  //对外暴露的服务
  trait MailboxService {
    //依赖两个不同存储库的服务，这些依赖被声明为 getNewMails 方法的参数，并且每个依赖都在一个单独的参数列表里。
    def getNewMails(emailRepo: EmailRepository)(filterRepo: FilterRepository)(user: User) =
      emailRepo.getMails(user, true).filter(filterRepo.getEmailFilter(user))

    //留空了字段 newMails，这个字段的类型是一个函数： User => Seq[Email]，依赖于 MailboxService 的组件会调用这个函数。
    val newMails: User => Seq[Email]
  }

  object MockEmailRepository extends EmailRepository {
    def getMails(user: User, unread: Boolean): Seq[Email] = Nil
  }
  object MockFilterRepository extends FilterRepository {
    def getEmailFilter(user: User): EmailFilter = _ => true
  }

  //扩展MailboxService时，实现 newMails 的方法就是应用 getNewMails 这个方法，把依赖 EmailRepository 、 FilterRepository 的具体实现传递给它：
  object MailboxServiceWithMockDeps extends MailboxService {
    val newMails: (User) => Seq[Email] =
      getNewMails(MockEmailRepository)(MockFilterRepository) _
  }

  //调用 MailboxServiceWithMockDeps.newMails(User("daniel") 无需指定要使用的存储库。在实际的应用程序中，这个服务也可能是以依赖的方式被使用，而不是直接引用。
}

object program_gitbook_DRY {

  //可组合性特征

  /**
    * 高阶函数：三种形式
    * 1.一个或多个参数是函数，并返回一个值。   比如map 、 filter 、 flatMap
    * 2.返回一个函数，但没有参数是函数。
    * 3.上述两者叠加：一个或多个参数是函数，并返回一个函数。
    */

  /**
    * 函数生成
    */
  case class Email(
                    subject: String,
                    text: String,
                    sender: String,
                    recipient: String
                  )

  //注意，类型别名使得代码看起来更有意义。
  type EmailFilter = Email => Boolean
  def newMailsForUser(mails: Seq[Email], f: EmailFilter) = mails.filter(f)  //过滤器

  //可以产生 EmailFilter 的工厂方法，这四个 vals 都是可以返回 EmailFilter 的函数；
  //前两个接受代表发送者的 Set[String] 作为输入，后两个接受代表邮件内容长度的 Int 作为输入。
  val sentByOneOf: Set[String] => EmailFilter =  //Set[String] => EmailFilter 代表sentByOneOf的类型是一个函数，函数的参数是Set[String]，返回值又是一个函数: EmailFilter
    senders =>    //senders就代表要返回的函数的参数也就是上面的Set[String]
      email => senders.contains(email.sender)  //代表要返回的函数的结果类型，它又是一个函数，参数是Email，返回是Boolean
  val notSentByAnyOf: Set[String] => EmailFilter =
    senders =>
      email => !senders.contains(email.sender)
  val minimumSize: Int => EmailFilter =
    n =>
      email => email.text.size >= n
  val maximumSize: Int => EmailFilter =
    n =>
      email => email.text.size <= n

  //可以使用这些函数来创建 EmailFilter ：
  val emailFilter: EmailFilter = notSentByAnyOf(Set("johndoe@example.com"))
  val mails = Email(
    subject = "It's me again, your stalker friend!",
    text = "Hello my friend! How are you?",
    sender = "johndoe@example.com",
    recipient = "me@example.com") :: Nil
  newMailsForUser(mails, emailFilter) // returns an empty list


  /**
    * 重用已有函数
    * 上面的代码有两个问题：
    * 第一个是工厂方法中有重复代码,比如 minimumSize 和 maximumSize
    */
  type SizeChecker = Int => Boolean
  //接受一个谓词函数，该谓词函数检查函数内容长度是否OK，邮件长度会通过参数传递给它
  val sizeConstraint: SizeChecker => EmailFilter =
    f =>
      email => f(email.text.size)

  //这样，我们就可以用 sizeConstraint 来表示 minimumSize 和 maximumSize 了：
  val minimumSize1: Int => EmailFilter =
    n =>
      sizeConstraint(_ >= n)
  val maximumSize1: Int => EmailFilter =
    n =>
      sizeConstraint(_ <= n)

}


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


  /**
    * 函数组合
    */

  //谓词函数，返回一个新函数，这个新函数总是得出和谓词相对立的结果：
  def complement[A](predicate: A => Boolean) = (a: A) => !predicate(a)

  //现在，对于一个已有的谓词 p ，调用 complement(p) 可以得到它的补。
  //scala的可组合能力：厉害！！！
  //给定两个函数f、g，f.compose(g)返回一个新函数，调用这个新函数时，会首先调用g，然后应用f到g的返回结果上。
  //类似的，f.andThen(g) 返回的新函数会应用g到f的返回结果上。

  //重写 notSentByAnyOf
  //当调用notSentByAnyOf1的时候，先调用sentByOneOf函数，这个函数返回一个EmailFilter，是一个Email => Boolean的函数，然后调用 andThen 后面的函数，
  //后面的函数将应用到这个Email => Boolean函数上，g也就代表这个Email => Boolean函数，complement(g)也会返回一个函数，
  // 这个函数的参数也是Email类型，但是返回的值总是和g代表的这个函数的返回布尔值相反；
  val notSentByAnyOf1 = sentByOneOf andThen (g => complement(g))

  //省略complement的参数
  val notSentByAnyOf2 = sentByOneOf andThen (complement(_))


  /**
    * 谓词组合
    * 邮件过滤器的第二个问题是，当前只能传递一个EmailFilter给newMailsForUser函数，而用户必然想设置多个标准。所以需要可以一种可以创建组合谓词的方法，
    * 这个组合谓词可以在任意一个标准满足的情况下返回 true ，或者在都不满足时返回 false 。
    */

  //满足任意一个过滤条件，即返回True
  def any[A](predicates: (A => Boolean)*): A => Boolean =
    a => predicates.exists(pred => pred(a))

  //any的取反，any只有在所有过滤条件都不符合的时候，才返回false，此时none返回True，也就是说所有的过滤条件都不符合时，none返回True；
  def none[A](predicates: (A => Boolean)*) = complement(any(predicates: _*))

  //满足过滤条件的设置为false，不满足的设置为true，也就是只有在所有过滤条件都符合的时候，返回false，
  def every[A](predicates: (A => Boolean)*) = none(predicates.view.map(complement(_)): _*)
}


/**
  * 有状态的对象: 对于不同的时间，相同的操作会产生不同的结果
  */

class BankAccount{

  private var bal:Int = 0

  /**
    * 在scala中，每个非私有的var类型成员变量都隐含了getter和setter方法，然而这些方法的命名方式却没有沿袭java的规定，
    * var变量x的getter方法命名为 x，它的setter方法命名是 x=
    */
  var hour = 12

  def balance:Int = bal

  //存钱
  def deposit(account:Int): Unit ={
    require(account > 0)
    bal += account
  }

  //取钱
  def withdraw(amount:Int):Boolean = {
    if (amount > bal) false
    else{
      bal -= amount
      true
    }
  }
}


class Time{

  private[this] var h = 12
  private[this] var m = 12

  // _ 代表默认值，
  // 直接写成 var celsius:Float 是定义抽象变量；
  var celsius:Float = _
  /**
    * 自定义getter setter 方法
    * 可以依照自己的意愿解释对变量的访问及赋值操作
    */
  def hour:Int = h
  def hour_= (x:Int){
    require(0 <= x && x < 24)
    h = x
  }

  def minute = m
  def minute_= (x:Int): Unit ={
    require(0 <= x && x < 60)
    m = x
  }
}


object program_18 {

  def main(args: Array[String]): Unit = {

    val account = new BankAccount()

    account.deposit(100)
    account.withdraw(80)  //true
    account.withdraw(80) //false

    account.hour_=(14)
    println(account.hour)

    var clock = new Time()
    clock hour= 12
  }
}

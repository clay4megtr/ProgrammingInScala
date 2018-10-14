import org.scalatest.Suite
import org.scalatest.FunSuite;
import junit.framework.TestCase;

/**
  * 断言和单元测试
  */


/**
  * scalaTest
  */
class WidthSuite extends Suite{

  def testEquals(): Unit ={

    def test1 = 1
    assert(test1 == 1)
  }

}

/**
  * 函数值的方式定义测试
  */
class FunctionSuite extends FunSuite{

  //test 被FunctionSuite 的主函数调用
  test("that's should be eaual"){
    // 测试代码作为传名参数传递给test的函数；并由test函数注册之后运行
    val test1 = 2
    assert(test1 === 2)  // === 可以看到详细的失败信息，但是不能区分实际结果和希望结果

    // 检查是否抛出了期待的异常
    intercept[ArithmeticException]{
      3 / 0
    }


  }
}


/**
  *
  */
class JunitTestCase extends TestCase{


}



object program_14 {

  def main(args: Array[String]): Unit = {

    //1. assert(condition)
    //2. assert(condition,explantion)  explantion的类型是Any，

  }
}

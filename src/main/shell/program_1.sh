#! /bin/bash

a="aa"
echo 'hello ${a} world!'       # 单引号不会对变量求值

echo -n "a b c d e"         # -n 取消自动换行

# printf 不会自动换行
# - 表示左对齐，默认右对齐，5是宽度，.2 指定保留两个小数位
printf "%-5s %-10s %-4s\n" No Name Mark
printf "%-5s %-10s %-4.2f\n" 1 Sarath 80.3456
printf "%-5s %-10s %-4.2f\n" 2 James 90.9899
printf "%-5s %-10s %-4.2f\n" 3 Jeff 77.564



###################################################################################

                                #变量和环境变量：

###################################################################################

# 每一个变量的值都是字符串，无论赋值的时候有没有使用引号，值都以字符串的形式存储，

pgrep gedit  # pgrep  获取运行程序的进程pid

# cat /proc/$PID/environ   # 查看程序运行时的环境变量

# cat /proc/$PID/environ | tr '\0' '\n'   # 默认以null字符（\0）分割，可以使用tr命令将\0替换成\n，以进行格式化

var=value  # 如果value不包含任何的空白字符或者$变量引用，那么不需要使用引号进行引用，反之，则必须使用单引号或者双引号，如果有$变量引用，必须使用双引号

#==========注意：var = value 和 var=value 是完全不同的操作，前者是相等操作，后者是赋值操作=================

# 环境变量是未在当前进程中定义，而从父进程中继承的变量，
HTTP_PROXY=http://192.168.0.2:3128
export HTTP_PROXY

var=1234567890
echo ${#var}      # ${#var} 用户获取var变量的长度

echo $0        # 查看当前使用的是哪种shell


# 查看是都是超级用户：  使用 UID 环境变量    root用户的UID是0
if [ $UID -ne 0 ]; then
echo Non root user, please run as root
else
echo "Root user"
fi

# 修改Bash 提示字符串=======》 默认的提示文本是在 ~/.bashrc 文件的PS1环境变量
# cat ~/.bashrc | grep PS1
PS1="########"   # 直接修改PS1，设置终端显示的字符串



###################################################################################

                                #数学计算： let  expr   bc

###################################################################################

# 定义的变量存储的形式都是字符串，所以需要使用一些方法进行数字计算   let   []
no1=4
no2=5

# 1. 使用let操作
let result=no1+no2    # 使用let时，变量名之前不需要添加$
echo ${result}

let no1++      # 自增
echo ${no1}

let no1--       # 自减

# 简写形式
let no1+=6
let no1-=6


# 2. 使用 [] 操作
result=$[ no1 + no2 ]
echo ${result}

result=$[ $no1 + 5 ]
echo ${result}


# 3. 使用(()) 操作，变量名之前必须加上 $
result=$(( no1 + 50 ))
echo ${result}


# 4. expr 计算操作  ===========使用expr 计算必须使用   ` ` 或者  $()  ==============
result=`expr 3 + 4`
echo ${result}

result=$(expr ${no1} + 5)

# 上面的都不支持浮点数计算，bc可以；
# 5. bc 计算操作（高级操作）
echo "4 * 0.56" | bc

no=54
result=`echo "${no} * 1.5" | bc `
echo ${result}

# 设置小数精度(scale=2)
echo "scale=2;3/8" | bc

# 进制转换  (obase 目标进制，ibase 原始进制)
no=100
echo "obase=2;$no" | bc

no=1100100
echo "obase=10;ibase=2;$no" | bc

# 平方 / 平方根
echo "sqrt(100)" | bc
echo "10^10" | bc



###################################################################################

                                #文件描述符和重定向

###################################################################################


# 文件描述符：是与文件输入输出相关联的**整数**，用来跟踪已打开的文件，最常见的文件描述符是stdin,stdout,stderr,
# 我们可以将某个文件描述符的内容重定向到另一个文件描述符中；

# 通过文件描述符，可以区分出哪些是正常的输出文本，那些是错误文本；

# 0  stdin   标准输入
# 1  stdout  标准输出
# 2  stderr  标准错误

echo "this is sample text 1" > temp.txt

echo "this is sample text 2" >> temp.txt

# 重定向操作符默认使用标准输出，也就是说 > 等同于 1>   >> 等同于  1>>

ls + 2> out.txt  # 正常运行

ls + 2> stderr.txt 1> stdout.txt

# 将 stderr 转换成 stdout，使得stderr和stdout 同时定义到一个文件
#ls + 2>& output.txt

ls + &> output.txt

# 排除stderr 信息
ls + 2> /dev/null

# 重定文本到文件时，如果还想把文本传递给后续命令，可以使用：tee
# command | tee file1 file2
# 注意： tee 只能从stdin中读取   cat -n 显示行号
cat std* | tee out.txt | cat -n

# tee -a 表示文件追加
cat std* | tee -a out.txt | cat -n


# 将文件内容重定向到命令，作为参数
ls < out.txt

# 向log日志中写入头部数据
#cat <<EOF>>log.txt
#Log File Header
#This is a test log file
#Function: System statistics
#EOF



###################################################################################

                                #数组和关联数组

###################################################################################


# 普通数组： 使用整数作为索引
# 关联数组： 使用字符串作为索引

array_var=(1 2 3 4 5 6)

array_var[0]="test1"
array_var[1]="test2"
array_var[2]="test3"
array_var[3]="test4"
array_var[4]="test5"
array_var[5]="test6"

# 元素打印
echo ${array_var[0]}

# 打印所有
echo ${array_var[*]}
echo ${array_var[@]}
# test1 test2 test3 test4 test5 test6

# 打印长度   # 号
echo ${#array_var[*]}

# 声明关联数组
#declare -A ass_array

# 添加元素到关联数组中
# 1. 使用一个索引 - 值 列表
ass_array=([index1]=val1 [index2]=val2)

# 2. 使用独立的索引 - 值进行赋值
ass_array[index1]=val1
ass_array[index2]=val2


# 例子： 水果价格制定  linux 下可以，mac下不行
#declare -A fruits_value

fruits_value=([apple]='100' [orange]=150)

echo "apple cost ${fruits_value[apple]}"


# 列出数组索引
echo ${!array_var[*]}  # 0 1 2 3 4 5



###################################################################################

                                #别名

###################################################################################

alias install='sudo apt-get install'

# 永久生效
# 因为每当一个shell进程生成时，都会执行 ~/.bashrc 的命令
# echo 'alias cmd="command seq"' >> ~/.bashrc

# $@ 用来获取参数   自动备份的例子
alias rm='cp $@ ~/backup; rm $@'

# 强制执行linux原本的命令（将命令进行转义）,避免安全问题
# \command



###################################################################################

                                # 获取、设置日期和延迟

###################################################################################

# 类unix系统中，日期被存储为一个整数；
date

# 打印纪元时(从1970年0分0秒至当前时刻的总秒数)
date +%s

# date -d "2018-09-10 12:12:00" +%s     linux下可以
# +%A    代表星期几
# +%B    代表月份
# +%d    日              31
# +%D    固定格式日期      09/10/18
# +%Y    年份
# +%H    小时

# so -> 日期格式化的方法(注意 必须加上 ``)
curr_date=`date +"%Y-%m-%d %H:%M:%S"`

# 获取一段时间的时间间隔的方法
latest_exe_date="2018-09-10 10:10:10"

echo "当前日期: "$curr_date
echo "最新执行日期: "$latest_exe_date

# =======1. 先转化成毫秒数
aa=$(date -d "$curr_date" +%s)
bb=$(date -d "$latest_exe_date" +%s)

# =======2. 计算间隔
time_distance=`expr $aa - $bb`      # 总的毫秒数
day_distance=$(expr ${time_distance} / 86400) ;    # 天数
day_remainder=$(expr ${time_distance} % 86400) ;   # 剩余的秒数
hour_distance=$(expr ${day_remainder} / 3600) ;    # 小时数
hour_remainder=$(expr ${day_remainder} % 3600) ;   # 剩余的秒数
min_distance=$(expr ${hour_remainder} / 60) ;      # 分钟
min_remainder=$(expr ${hour_remainder} % 60) ;     # 剩余的秒数

# =======3. 打印
echo "已经过去了${day_distance}天，${hour_distance}小时，${hour_distance}分钟"

# ===========shell中的乘号是:    \*=========
sum_min=$(expr $day_distance \* 1440 + $hour_distance \* 60 + $min_distance)


# sleep 设置睡眠时间

count=0
while [ $count -lt 3 ];
do
if [ $count -lt 3 ]; then
  let count++;
  sleep 1
  tput rc    # 恢复之前存储的光标位置，在终端打印出新的count值，
  tput ed    # 清楚从当前光标位置到行尾之间的所有内容
  echo -n $count
else
  echo "echo number done"
fi
done


###################################################################################

                                # 调试脚本

###################################################################################

for i in {1..6}
do
  set -x
  echo $i
  set +x     # 只有set -x  和   set +x   区域内的调试信息会被打印出来
done
echo "script executed"



###################################################################################

                                # 函数和参数、读取命令序列输出

###################################################################################

fname()
{
  echo $1,$2;
  echo "$@";  # 以列表的形式一次性打印所有参数
  echo "$*";  # 类似于"$@"，但是参数被作为单个实体
  return 0;   # 返回值
}

fname 123124 234234 2343

# 命令序列的输出
ls | cat -n > out.txt


###################################################################################

       # 内部字段分割符：IFS

###################################################################################

# 内部字段分割符：是用于特定用途的定界符，IFS是存储定界符的环境变量，它是当前shell环境使用的默认定界字符串

# 分割csv文件
data="name,sex,rollno,lacation"
# 可以使用INF读取变量中的每个条目
oldIFS=$IFS
IFS=,
for item in $data;
do
  echo item: $item
done

IFS=$oldIFS  # 恢复

# 默认分隔符是空白符：（换行符，制表符，空格）


#获取用户对应的shell
line="root:x:0:0:root:/root:/bin/bash"
oldIFS=$IFS
IFS=:
count=0
for item in $line
do

[ $count -eq 0 ] && user=$item
[ $count -eq 6 ] && shell=$item
let count++
done;

IFS=$oldIFS  # 恢复
echo $user\'s shell is $shell



###################################################################################

                    # 比较与测试

###################################################################################

#数字的比较
num=3

if [ $num -lt 0 ]; then
  echo "num < 0"
elif [ $num -gt 0 ] && [ $num -lt 10 ]; then    # 多个判断条件的写法
  echo "0 < num < 10"
else
  echo "num > 10"
fi


if [ $num -lt 0 ]; then
  echo "num < 0"
elif [ $num -gt 0 -a $num -lt 10 ]; then    # -a 代表逻辑与，-o 代表逻辑或
  echo "0 < num < 10"
else
  echo "num > 10"
fi


# 简单写法
# [ condation ] && action   # condation为真，执行action
# [ condation ] || action   # condation为假，执行action


## 文件系统相关测试
var=/bin
[ -f $var ]        #包含正常的文件路径或文件名
[ -x $var ]        #文件可执行
[ -d $var ]        #是目录
[ -e $var ]        #文件存在
[ -c $var ]        #是一个字符设备文件的路径
[ -b $var ]        #块设备文件的路径
[ -r $var ]        #可读
[ -w $var ]        #可写
[ -L $var ]        #符号链接

fpath="/etc/passwd"
if [ -e $fpath ]; then
  echo "file exist"
else
  echo "file not exists"
fi


# 字符串比较
# 最好用 [[]] ，否则容易出现问题
str1="abc"
str2=""

[[ $str1 = $str2 ]]   # 字符串相等
[[ $str1 == $str2 ]]   # 同上
[[ $str1 != $str2 ]]
[[ $str1 > $str2 ]]     # 字典序

[[ -z $str1 ]]         # 空
[[ -n $str1 ]]          # 非空


if [[ -n $str1 ]] && [[ -z $str2 ]]; then    # 多个判断条件的情况；
  echo "yes"
fi
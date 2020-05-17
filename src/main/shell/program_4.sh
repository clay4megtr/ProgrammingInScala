#! /bin/bash


###################################################################################

                            #grep

###################################################################################

# 一般用法, 可以一次过滤多个文件
grep is log.txt

# 使用正则表达式, -o表示只输出文件中匹配到的文本部分(而且还会换行)
echo "this is a line." | egrep -o "[a-z]+\."

# 打印除包含match行之外的所有行 -v
grep -v is log.txt

# 统计match字符串的行数(不是次数)
grep -c is log.txt

# 统计match字符串的次数
echo -e "1 2 3 4\nhello\n5 6" | egrep -o "[0-9]" | wc -l

# 打印出match行所在的行数 （第几行）
grep -n is log.txt

# 递归搜索 -R   .表示当前目录
grep is . -R -n

# 忽略大小写
grep -i is log.txt

# 匹配多个样式
echo "this is a line of text" | grep -e "this" -e "line" -o

# 只包括或排除文件
grep "main()" . -r --include *.{c,cpp}
grep "main()" . -r --exclude "README"

# 打印匹配文本之前或者之后的行
seq 10 | grep 5 -A 3  #打印之后的3行
seq 10 | grep 5 -B 3  #打印之前的3行
seq 10 | grep 5 -C 3  #打印之前和之后的3行


###################################################################################

                            #cut, 按列切分

###################################################################################

# 一般用法
cut -f 2,3 student.txt

# 指定分隔符
cut -f 2 -d ";" student.txt


###################################################################################

                            #sed: 流编辑器

###################################################################################

# 最常用的功能：文本替换
# sed 's/pattern/replace_string/' file

# 替换结果应用于原文件 -i
# sed -i 's/pattern/replace_string/' file

# 默认只会替换第一个，替换所有: g
# sed 's/pattern/replace_string/g' file

# 从第N处开始替换
echo this thisthisthis | sed 's/this/THIS/2g'

# 移除空白行 d表示移除
sed '/^$/d' log.txt

# 已匹配字符串标记 &
echo "this is a example" | sed 's/\w\+/[&]/g'

# 组合多个表达式
sed 'experssion' | sed 'expersion'

# example, 所有的3位数字转换为 "NUMBER"
echo "11 abd 111 this 9 file 222" | sed 's/\b[0-9]\{3\}\b/NUMBER/g'


###################################################################################

                            #awk: 对列和行进行操作

###################################################################################

# 格式
#awk 'BEGIN { statements } { statements } END { statements }' file

# 工作原理
# 1. 执行 BEGIN { commands } 语句块中的语句
# 2. 从文件或stdin中读取一行，然后执行pattern { commands }. 重复这个过程，直到文件被读取完毕
# 3. 当读至输入流末尾时，执行 end { commands }
# 3个部分都是可选的，pattern { commands } 也是可选的，不提供时，则默认执行 { print },即打印读取的行

# example
echo -e "line1\nline2" | awk 'BEGIN { print "start" } {print} END { print "end" }'

echo | awk '{ var1="var1"; var2="var2"; var3="var3"; print var1,var2,var3 }'

# 特殊变量
# NR: 当前行号
# NF: 当前行的字段数
# $0: 当前行的文本内容
# $1: 第一个字段的文本内容

# example
echo -e "line1 f2 f3\nline2 f4 f5\nline3 f6 f7" | awk '{ print "Line num="NR"", "$0="$0 }'

# 打印每一行的第2个和第3个字段
awk '{ print $2 "-" $3 }' log.txt

# 统计文件中的行数
awk 'END { print NR }' log.txt

# 外部变量值传递  -v
VAR=10000
echo | awk -v VARIABLE=${VAR} '{ print VARIABLE }'
var1="var1"
var2="var2"
echo | awk '{ print v1,v2 }' v1=${var1} v2=${var2}

# 指定过滤条件
awk 'NR < 5 { print NR }' log.txt
awk '/is/ { print }' log.txt
awk '!/is/ { print }' log.txt

# 设定分隔符 -F
awk -F : '{ print }' log.txt


###################################################################################

                            #遍历文件

###################################################################################

# 遍历行(shell脚本内的写法)
while read line;
do
  echo "${line}"
done < log.txt

# 遍历行 (命令行写法)
cat log.txt | (while read line; do echo ${line}; done )

# 遍历行中的每一个单词
for work in $line;
do
  echo ${word}
done


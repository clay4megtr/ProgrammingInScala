#! /bin/bash

###################################################################################

                                #cat：

###################################################################################

# 拼接 Text Through stdin  和 out.txt 的内容
echo "Text Through stdin" | cat - out.txt

# 压缩空白行
cat -s log.txt

# 移除空白行
# tr 将多个连续的换行符连接成一个
cat log.txt | tr -s '\n'

# 标记制表符为 ^ (mac下不可以)
# cat -T out.txt

# 显示行号: 不会修改文件
cat -n out.txt


###################################################################################

       #文件查找与文件列表：find

###################################################################################

# 列出当前目录和子目录的所有文件 -print 使用 '\n' 作为分隔符
find . -print

# -print0 使用 '\0' 作为分隔符
find . -print0

# 根据文件名/正则表达式进行搜索
find . -name "*.txt"

# 忽略大小写搜索
find . -iname "tmp*"

# 按照多个条件搜索 -o 代表 or
find . \( -name "*.txt" -o -name "*.pdf" \)

# -name只能按照文件名进行匹配，-path既可以按照文件名匹配，还可以按照路径名匹配
find . -path "*tmp*"

# 按照re匹配,需要注意""内的所有字符都需要转义
find . -regex ".*\(\.py\|\.sh\)$"

# 按照re匹配，忽略大小写
find . -iregex ".*\(\.py\|\.sh\)$"

# 否定参数
find . ! -name "*.txt"

# 设置目录深度的搜索
find . -maxdepth 1 -name "*.txt"

# 根据文件类型搜索,d表示目录，f表示普通文件，l表示符号链接
find . -type d
find . -type f
find . -type l

# 根据文件时间搜索(-atime:用户最近一次访问文件的时间，-mtime: 文件内容最后一次被修改的时间，-ctime文件元数据最后一次被修改的时间，单位都是天) -表示小于，+表示大于
find . -maxdepth 1 -atime -7

# 单位为分钟：-amin -mmin -cmin
find . -maxdepth 1 -amin +7

# 查找比当前文件更新的文件
find . -maxdepth 1 -newer file.txt

# 按照文件大小搜索(k,M,G)
find . -maxdepth 1 -size +2k

# 删除匹配的文件
find . -maxdepth 1 -name "*.swp" -delete

# 基于文件权限和所有权匹配
find . -maxdepth 1 -type f -perm 644
find . -maxdepth 1 -name "*.sock" ! -perm 644
find . -maxdepth 1 -user loushang

# 结合find执行命令或动作 (-exec)
find . -maxdepth 1 -type f -user _mysql -exec chown loushang {} \;

# exec执行多条命令
# find . -name "*.txt" -exec ./command.sh {} \;

# 跳过特定目录
find . \( -name ".git" -prune \) -o \( -type f -print \)


###################################################################################

       #xargs: 标准输入转化为命令行参数
       #重点1: 只要我们把find的输出作为xargs的输入，就必须将 -print0 和 find 结合使用，以字符null作为分隔输出

###################################################################################

# 多行输入转换为单行输出, 空格替换掉换行符
cat log.txt | xargs

# 单行输入转换为多行输出, 每行3个参数
cat log.txt | xargs -n 3

# 使用定制的定界符分隔 -d
echo "asdasdXerermiXw3gf4rXrtgqw" | xargs -d X

# 结合 -d 和 -n
echo "asdasdXerermiXw3gf4rXrtgqw" | xargs -d X -n 2

# 传递参数，每次传递一个参数，不加 -n 是一次性传递所有内容为一个参数
# cat args.txt | xargs -n 1 ./command.sh

# find 和 xargs 结合使用
# 错误用法，如果存在 hello test.txt 这种带空格的文件名，会解析成hello和test.txt两个文件
# find . -type f -name "*.txt" -print | xargs rm -f

# 正确用法，-0指定 '\0' 作为分隔符, yes!
find . -type f -name "*.txt" -print0 | xargs -0 rm -f


###################################################################################

       #tr: 对来自标准输入的字符进行替换、删除、以及压缩

###################################################################################

# 将输入字符由大写转换为小写
echo "HELLO WHO IS THIS" | tr "A-Z" "a-z"

# 使用集合对数字加密
echo 12345 | tr '0-9' '9876543210'

# 制表符转换为空格
cat log.txt | tr '\t' ' '

# 删除(数字)字符 -d
echo "Hello 123" | tr -d '0-9'

# 压缩字符 -s
echo "GNU    is   not UNIX" | tr -s ' '

# 技巧: 对一个文件中的数字(每行一个)相加求结果
# cat number.txt | echo $[ $(tr '\n' '+') 0 ]


###################################################################################

       #sort: 对文本文件和stdin进行排序   uniq: 从文本或者stdin中提取不重复的行(输入必须是已经排序的，所以一般必须和sort结合使用)

###################################################################################

# 排序文件
# sort file1.txt file2.txt > sorted.txt

# 找出文件中不重复的行
#cat sorted.txt | uniq > uniq_line.txt

# 按数字排序
sort -n number.txt

# 按逆序排序
sort -r number.txt

# 按照其他列排序, 默认是按照第一列排序
#sort -k 2 number.txt

# 重复的行只打印一次
uniq number.txt
sort number.txt | uniq
sort -u number.txt

# 重复的行直接过滤掉,只显示没有重复的行
uniq -u number.txt

# 只显示重复的行
sort number.txt | uniq -d

# 统计每行出现的次数
sort number.txt | uniq -c





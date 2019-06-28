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
cat -s log.txt | tr -s '\n'

# 标记制表符为 ^
cat -T out.txt

# 行号: 不会修改文件
cat -n out.txt



###################################################################################

       #文件查找与文件列表：find

###################################################################################

#列出当前目录和子目录的所有文件
find .

# 打印当前目录文件列表
find . -print





























#! /bin/bash

# 用途：列出10条最常用的命令
printf "COMMAND\tCOUNT\n"

cat ~/.bash_history | awk '{ list[$1]++; } END { for(i in list){ printf("%s\t%d\n",i,list[i]); } }' | sort -nrk 2 | head



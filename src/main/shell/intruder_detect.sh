#! /bin/bash

# 用途：入侵报告工具，以auth.log作为日志文件

AUTHLOG=/var/log/secure

if [[ -n $1 ]];
then
 AUTHLOG=$1
 echo Using log file : ${AUTHLOG}
fi
LOG=/tmp/valid.$$.log
grep -v -e "invalid" -e "Invalid" ${AUTHLOG} > ${LOG}  #过滤掉所有不存在的用户登陆日志
users=$(grep "Failed password" ${LOG} | awk '{ print $(NF-5) }' | sort | uniq )


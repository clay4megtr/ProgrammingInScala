#! /bin/bash

###################################################################################

                            # 收集进程信息   ps  top  pgrep

###################################################################################

# ps默认只列出当前终端启动的进程，-e (every) 列出所有  -f 列出更多列  -ax和-e相同
ps -ef | head

# -o 指定想要显示的列
# comm: command
# pcpu: cpu利用率
# pid: 进程id
# ppid: 父进程id
# user: 所属用户
# cmd: 简单命令
ps -eo comm,pcpu | head

# 列出占用cpu最多的10个进程 (+表示升序 -表示降序)
ps -eo comm,pcpu --sort -pcpu | head

# 列出线程相关信息 -L, NLWP是线程数量，NLP是ps输出中每个条目的线程ID
# 列出线程数最多的10个线程
ps -eLf --sort -nlwp | head

# 显示进程的环境变量 (更简单的办法是查询 /proc/PID/ 下的进程信息)
ps -eo pid,cmd e | tail -n 3


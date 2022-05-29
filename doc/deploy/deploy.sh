#!/bin/bash
echo "publish----------"

# 如果存在这个进程就杀掉
process_id=`ps -ef | grep wiki.jar | grep -v grep |awk '{print $2}'`
if [ $process_id ] ; then
sudo kill -9 $process_id
fi


source /etc/profile
# 让后台一直执行这个命令
nohup java -jar -Dspring.profiles.active=prod ~/wiki/wiki.jar > /dev/null 2>&1 &

echo "end publish"

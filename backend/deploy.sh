#!/bin/bash
ssh kdc 'cp /data/server/kadingche/admin/karting-admin-1.0-SNAPSHOT.jar /data/server/kadingche/admin/karting-admin-1.0-SNAPSHOT.jar.$(date "+%Y%m%d%H%M%S").bak'
echo "备份完成"
scp /Users/wuyongcong/projects/java/dayu/kdc_admin/karting-admin/target/karting-admin-1.0-SNAPSHOT.jar kdc:/data/server/kadingche/admin
echo "上传完成"
num=$(ssh kdc 'ps -ef | grep "karting-admin-1.0-SNAPSHOT.jar" | grep -v grep | awk "{print \$2}"')
ssh kdc 'kill -9 '$num
echo "结束进程"
ssh kdc 'nohup java -jar /data/server/kadingche/admin/karting-admin-1.0-SNAPSHOT.jar --spring.profiles.active=sit >/dev/null 2>&1 &'
echo "启动进程"
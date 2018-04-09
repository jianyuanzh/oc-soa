#!bin/bash
cur_dir=`pwd`
docker stop databus-mysql
docker rm databus-mysql
docker run --name databus-mysql -v ${cur_dir}/conf/:/etc/mysql/conf.d -v ${cur_dir}/data:/var/lib/mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=Log1cm0n -d mysql:latest

#!/bin/bash

cur_dir=`pwd`
docker stop microservice-mysql
docker rm microservice-mysql
docker run --name microservice-mysql -v ${cur_dir}/conf:/etc/mysql/conf.d -v ${cur_dir}/data:/var/lib/mysql -p 3386:3306 -e MYSQL_ROOT_PASSWORD=L0g1cm0n -d mysql:5.7
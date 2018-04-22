#!/usr/bin/env bash

docker stop microservice-zookeeper
docker rm microservice-zookeeper

docker run --name microservice-zookeeper -p 2181:2181 --restart always -d zookeeper:3.5
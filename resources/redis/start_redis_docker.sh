#!/usr/bin/env bash

docker stop microservice-redis
docker rm microservice-redis
docker run -idt -p 6379:6379 -v `pwd`/data:/data --name microservice-redis -v `pwd`/redis.conf:/etc/redis/redis_default.conf redis:3.2
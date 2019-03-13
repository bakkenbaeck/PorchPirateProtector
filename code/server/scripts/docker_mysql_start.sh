#!/usr/bin/env bash

docker run -d mysql/mysql-server:5.7.24 -n mysql1
docker start mysql1
docker container inspect mysql1

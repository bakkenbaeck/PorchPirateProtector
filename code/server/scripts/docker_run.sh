#!/usr/bin/env bash

docker run --name=ppp -m512M --cpus 2 -it -p 8080:8080 --rm ppp-server

#!/usr/bin/env bash

# Get rid of previous image
docker image remove ppp-server

# Go to where we can run a gradle build of the server, then run it
cd ../../
./gradlew :server:build

# Go to where we can build something with docker, then build it.
cd server
docker build -t ppp-server .
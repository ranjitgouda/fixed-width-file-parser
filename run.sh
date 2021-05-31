#!/bin/sh
no_of_lines=$1
echo "Building docker image"
docker build -t json_parser .

echo "Running docker container"
docker run --rm -it -v  /tmp:/tmp  json_parser $no_of_lines
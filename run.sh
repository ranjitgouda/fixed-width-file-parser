#!/bin/sh
if [ $# -eq 0 ];then
  echo "File line count not provided, adding default value of 1000 lines"
  no_of_lines=1000
else
  no_of_lines=$1
fi

echo "Building docker image $no_of_lines}"
docker build -t json_parser .

echo "Running docker container"
docker run --rm -it -v  /tmp:/tmp  json_parser $no_of_lines
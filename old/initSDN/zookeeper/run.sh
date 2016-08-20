#!/usr/bin/env bash

echo "running server s1"
export ZOOCFGDIR=/vagrant/initSDN/zookeeper/s1; ~/Downloads/zookeeper-3.4.6/bin/zkServer.sh  start
echo "running server s2"
export ZOOCFGDIR=/vagrant/initSDN/zookeeper/s2; ~/Downloads/zookeeper-3.4.6/bin/zkServer.sh  start
echo "running server s3"
export ZOOCFGDIR=/vagrant/initSDN/zookeeper/s3; ~/Downloads/zookeeper-3.4.6/bin/zkServer.sh  start
echo "running client c1"
~/Downloads/zookeeper-3.4.6/bin/zkCli.sh -server 127.0.0.1:2191

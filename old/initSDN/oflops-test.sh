#!/usr/bin/env bash

sudo rm /var/run/dp-test test1.log test2.log
sudo pkill -9 ofdatapat
sudo pkill -9 ofprotoco
for p in 0 1; do
sudo ip link delete vHost$p;
sudo ip link delete vGuest$p;
done

for p in 0 1; do
sudo ip link add vHost$p type veth peer name vGuest$p
sudo ifconfig vHost$p up;
sudo ifconfig vGuest$p up;
done
sudo ofdatapath --detach punix:/var/run/dp-test -d 004E46324357 -i vHost0,vHost1
sleep 2
sudo ofprotocol unix:/var/run/dp-test tcp:192.168.33.3:6633 &> test1.log &
sleep 2
sudo oflops -c eth1 -d vGuest0 -d vGuest1 /home/vagrant/mininet-dev/oflops/example_modules/openflow_flow_dump_test/.libs/libopenflow_flow_dump_test.so.0  &> test2.log &





#!/usr/bin/env bash


sudo pkill -f -9 pox
sudo pkill -f -9 flowvisor
sudo mn -c
sudo pkill  -f -9 mininet

#debug="" 
debug="log.level --DEBUG"
sudo /home/vagrant/pox/pox.py $debug  openflow.of_01 --port=10003 forwarding.l2_learning &> pox10001.log &
sudo /home/vagrant/pox/pox.py  $debug openflow.of_01 --port=10004 forwarding.l2_learning &> pox10002.log &


sudo rm /etc/flowvisor/config.json
sleep 5
sudo -u vagrant fvconfig generate /etc/flowvisor/config.json
sleep 5
sudo /etc/init.d/flowvisor start
sleep 5
fvctl -f /dev/null set-config --enable-topo-ctrl
sleep 5
sudo /etc/init.d/flowvisor restart
sleep 5

fvctl -f /dev/null add-slice upper tcp:localhost:10003 admin1@upperslice;
fvctl -f /dev/null add-slice lower tcp:localhost:10004 admin2@upperslice;


fvctl -f /dev/null add-flowspace dpid1-port1 1 1 in_port=1 upper=7;
fvctl -f /dev/null add-flowspace dpid1-port3 1 1 in_port=3 upper=7;
fvctl -f /dev/null add-flowspace dpid2-any 2 1 any upper=7;
fvctl -f /dev/null add-flowspace dpid4-port1 4 1 in_port=1 upper=7;
fvctl -f /dev/null add-flowspace dpid4-port3 4 1 in_port=3 upper=7;


fvctl -f /dev/null add-flowspace dpid1-port2 1 1 in_port=2 lower=7
fvctl -f /dev/null add-flowspace dpid1-port4 1 1 in_port=4 lower=7
fvctl -f /dev/null add-flowspace dpid3-any 3 1 any lower=7
fvctl -f /dev/null add-flowspace dpid4-port2 4 1 in_port=2 lower=7
fvctl -f /dev/null add-flowspace dpid4-port4 4 1 in_port=4 lower=7



sudo mn --custom ./topo.py --topo fvtopo --link tc --controller remote --mac --arp
# sudo mn -c

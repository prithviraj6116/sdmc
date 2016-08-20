#!/usr/bin/env bash

printf "installing openvirtex\n"
cd /home/vagrant
git clone https://github.com/opennetworkinglab/OpenVirteX.git
#requires oracle java and 0.90 floodlight

cd /home/vagrant
sudo chown vagrant:vagrant -R .

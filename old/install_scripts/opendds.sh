#!/usr/bin/env bash

printf "installing opendds\n"
cd /home/vagrant
wget http://download.ociweb.com/OpenDDS/OpenDDS-3.5.1.tar.gz
tar -xzvf OpenDDS-3.5.1.tar.gz
cd /home/vagrant/DDS
./configure
make
cd /home/vagrant




cd /home/vagrant
sudo chown vagrant:vagrant -R .

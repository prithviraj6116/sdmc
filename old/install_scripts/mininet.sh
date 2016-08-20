#!/usr/bin/env bash

printf "installing mininet\n"
cd /home/vagrant
git clone git://github.com/mininet/mininet mininet; 
cd /home/vagrant/mininet/util
./install.sh
./install.sh -v -3 -V 2.3.1
./install.sh -y
cd /home/vagrant
sudo chown vagrant:vagrant -R .

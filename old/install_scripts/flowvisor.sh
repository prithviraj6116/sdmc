#!/usr/bin/env bash

printf "installing flowvisor\n"
cd /home/vagrant
git clone git://github.com/OPENNETWORKINGLAB/flowvisor.git flowvisor
cd flowvisor
sudo make fvuser=vagrant fvgroup=vagrant install
cd /home/vagrant


cd /home/vagrant
sudo chown vagrant:vagrant -R .

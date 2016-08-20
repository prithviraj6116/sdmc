#!/usr/bin/env bash

printf "installing floodlight"
cd /home/vagrant
git clone git://github.com/floodlight/floodlight.git;
cd floodlight;
git checkout stable;
git checkout remotes/origin/v0.90 #required for the OpenVirtex
ant;
#java -jar target/floodlight.jar &> floodlight.log & #for starting
cd /home/vagrant


cd /home/vagrant
sudo chown vagrant:vagrant -R .

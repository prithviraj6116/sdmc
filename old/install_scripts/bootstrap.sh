#!/usr/bin/env bash
cp /vagrant/.emacs /home/vagrant
cp /vagrant/.screenrc /home/vagrant
sudo /vagrant/install_scripts/aptgetinstalls.sh
sudo /vagrant/install_scripts/mininet.sh
sudo /vagrant/install_scripts/pyretic.sh
sudo /vagrant/install_scripts/openvirtex.sh
sudo /vagrant/install_scripts/floodlight.sh


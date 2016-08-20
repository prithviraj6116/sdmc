#!/usr/bin/env bash


print "installing frenetic\n"

opam init --yes
opam config env
git clone https://github.com/frenetic-lang/frenetic.git
cd /home/vagrant/frenetic
opam update
opam upgrade
#opam pin add frenetic . 
opam install frenetic -y
opam install async -y
opam install ox
cd ~
mkdir frenetic-tutorial; git clone https://github.com/frenetic-lang/tutorials.git frenetic-tutorial




cd /home/vagrant
sudo chown vagrant:vagrant -R .

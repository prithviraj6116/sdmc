#!/usr/bin/env bash

add-apt-repository ppa:n-muench/programs-ppa
add-apt-repository ppa:webupd8team/java

apt-get -y update
apt-get -y upgrade
apt-get -y install emacs git build-essential make gdb bridge-utils vlan
apt-get -y install quagga curl 
apt-get -y install mongodb mongodb-dev maven libsnmp-dev libpcap-dev doxygen
apt-get -y install help2man python-setuptools
apt-get -y install python python-software-properties
#apt-get -y install texlive-full    wireshark-dev
apt-get -y install docker.io scons 
#apt-get -y install openjdk-7-jdk ant 
#opervirtex requires oracle java
apt-get -y install oracle-java7-installer
apt-get -y install oracle-java7-set-default
apt-get -y install wireshark wireshark-dev hexedit
apt-get -y install python-software-properties  build-essential m4 python-networkx wireshark git
apt-get -y install python-dev python-pip screen hping3
apt-get -y install opam 
apt-get -y install python-wxgtk2.8
easy_install --upgrade pip
pip install networkx bitarray netaddr ipaddr pytest ipdb yappi pbr



cd /home/vagrant
sudo chown vagrant:vagrant -R .


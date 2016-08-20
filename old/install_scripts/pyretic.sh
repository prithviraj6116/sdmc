#!/usr/bin/env bash

print "installing pyretic\n"
cd /home/vagrant
wget https://raw.github.com/frenetic-lang/pyretic/master/pyretic/backend/patch/asynchat.py
sudo mv asynchat.py /usr/lib/python2.7/
sudo chown root:root /usr/lib/python2.7/asynchat.py 
git clone git://github.com/frenetic-lang/pyretic.git
echo "export PATH=$PATH:$HOME/pyretic:$HOME/pox\
 export PYTHONPATH=$HOME/pyretic:$HOME/mininet:$HOME/pox" >> ~/.profile
echo "export PATH=$PATH:$HOME/pyretic:$HOME/pox\
 export PYTHONPATH=$HOME/pyretic:$HOME/mininet:$HOME/pox" >> ~/.bashrc
##Note on running
##source .bashrc and Do NOT run pyretic with sudo
##use ./pyretic.py -m p0 pyretic.modules.mac_learner



cd /home/vagrant
sudo chown vagrant:vagrant -R .

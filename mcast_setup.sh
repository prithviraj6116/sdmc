sudo rm -r mininet loxigen pox openflow oftest oflops netifaces-0.10.4*
sudo apt-get -y update;
sudo apt-get   install -y emacs build-essential screen git wget python-setuptools
sudo apt-get install -y   graphviz python-matplotlib python-dev freeglut3 libgraphviz-dev libgtkglext1-dev python-pip python-scipy 
sudo pip install networkx pygraphviz;
cd /vagrant
if [ ! -d "netifaces-0.10.4" ]; then
    sudo rm -rf netifaces-0.10.4.tar.gz;
    wget -nc https://pypi.python.org/packages/source/n/netifaces/netifaces-0.10.4.tar.gz#md5=36da76e2cfadd24cc7510c2c0012eb1e ;
    sudo rm -rf netifaces-0.10.4;
    tar -zxvf netifaces-0.10.4.tar.gz;
    cd netifaces-0.10.4;
    sudo python setup.py install;
    cd /vagrant;
fi
if [ ! -d "mininet" ]; then
    git clone git://github.com/mininet/mininet mininet; 
    cd mininet/util
    sudo bash install.sh
    # ./install.sh -v -3 -V 2.3.1
    # ./install.sh -y
    # cd /home/vagrant
    # sudo chown vagrant:vagrant -R .
    cd /vagrant
fi

/bin/cp -rf /vagrant/mymcast/pox/lib/packet/* /vagrant/pox/pox/lib/packet
/bin/cp -rf /vagrant/mymcast/pox/misc/* /vagrant/pox/pox/misc/
/bin/cp -rf /vagrant/mymcast/pox/openflow/* /vagrant/pox/pox/openflow/
/bin/cp  /vagrant/.screenrc /home/vagrant

cd /vagrant/mymcast/
#sudo python mcast_controller_1.py

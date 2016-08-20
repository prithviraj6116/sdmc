#!/usr/bin/env python

from mininet.node import Host, RemoteController
from mininet.topo import Topo
from mininet.util import quietRun
from mininet.log import error
c1 = RemoteController( 'c1', ip='127.0.0.1' )

def topo1():
    # net = Mininet( topo=VLANStarTopo() )
    net = Mininet()

    h11 = net.addHost('h11', ip='10.0.0.11')
    h12 = net.addHost('h12', ip='10.0.0.12')
    h21 = net.addHost('h21', ip='10.0.0.21')
    h22 = net.addHost('h22', ip='10.0.0.22')
    h31 = net.addHost('h31', ip='10.0.0.31')
    h32 = net.addHost('h32', ip='10.0.0.32')

    s1 = net.addSwitch('s1')
    s2 = net.addSwitch('s2')
    s3 = net.addSwitch('s3')
    net.addLink(h11, s1)
    net.addLink(h12, s1)
    net.addLink(h21, s2)
    net.addLink(h22, s2)
    net.addLink(h31, s3)
    net.addLink(h32, s3)
    net.addLink(s1, s2)
    net.addLink(s2, s3)

    net.addController(c1)
    net.start()
    CLI( net )
    net.stop()

if __name__ == '__main__':
    import sys
    from functools import partial

    from mininet.net import Mininet
    from mininet.cli import CLI
    from mininet.topo import SingleSwitchTopo
    from mininet.log import setLogLevel

    setLogLevel( 'info' )
    topo1()

    

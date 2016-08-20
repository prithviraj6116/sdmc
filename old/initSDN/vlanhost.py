#!/usr/bin/env python
"""
vlanhost.py: Host subclass that uses a VLAN tag for the default interface.

Dependencies:
    This class depends on the "vlan" package
    $ sudo apt-get install vlan

Usage (example uses VLAN ID=1000):
    From the command line:
        sudo mn --custom vlanhost.py --host vlan,vlan=1000

    From a script (see exampleUsage function below):
        from functools import partial
        from vlanhost import VLANHost

        ....

        host = partial( VLANHost, vlan=1000 )
        net = Mininet( host=host, ... )

    Directly running this script:
        sudo python vlanhost.py 1000

"""

from mininet.node import Host, RemoteController
from mininet.topo import Topo
from mininet.util import quietRun
from mininet.log import error
c = RemoteController( 'c', ip='127.0.0.1' )
class VLANHost( Host ):
    "Host connected to VLAN interface"

    def config( self, vlan=100, **params ):
        """Configure VLANHost according to (optional) parameters:
           vlan: VLAN ID for default interface"""

        r = super( VLANHost, self ).config( **params )

        intf = self.defaultIntf()
        # remove IP from default, "physical" interface
        self.cmd( 'ifconfig %s inet 0' % intf )
        # create VLAN interface
        self.cmd( 'vconfig add %s %d' % ( intf, vlan ) )
        # assign the host's IP to the VLAN interface
        self.cmd( 'ifconfig %s.%d inet %s' % ( intf, vlan, params['ip'] ) )
        # update the intf name and host's intf map
        newName = '%s.%d' % ( intf, vlan )
        # update the (Mininet) interface to refer to VLAN interface name
        intf.name = newName
        # add VLAN interface to host's name to intf map
        self.nameToIntf[ newName ] = intf

        return r

hosts = { 'vlan': VLANHost }


def exampleAllHosts( vlan ):
    """Simple example of how VLANHost can be used in a script"""
    # This is where the magic happens...
    host = partial( VLANHost, vlan=vlan )
    # vlan (type: int): VLAN ID to be used by all hosts

    # Start a basic network using our VLANHost
    topo = SingleSwitchTopo( k=2 )
    net = Mininet(controller=c, host=host, topo=topo )
    net.start()
    CLI( net )
    net.stop()

# pylint: disable=arguments-differ


class TreeTopoVlan( Topo ):
    "Topology for a tree network with a given depth and fanout."

    def build( self, depth=1, fanout=2, vlanno=1):
        # Numbering:  h1..N, s1..M
        self.hostNum = 1
        self.switchNum = 1
        # Build topology
        self.addTree( depth, fanout, vlanno )

    def addTree( self, depth, fanout, vlanno ):
        """Add a subtree starting with node n.
           returns: last node added"""
        isSwitch = depth > 0
        if isSwitch:
            node = self.addSwitch( 's%s' % self.switchNum )
            self.switchNum += 1
            for _ in range( fanout ):
                child = self.addTree( depth - 1, fanout, vlanno )
                self.addLink( node, child )
        else:
            vlanid = 100 + self.hostNum % vlanno
            print vlanid 
            node = self.addHost( 'h%s' % self.hostNum, cls=VLANHost, vlan=vlanid)
            self.hostNum += 1
        return node


class VLANStarTopo( Topo ):
    """Example topology that uses host in multiple VLANs

       The topology has a single switch. There are k VLANs with
       n hosts in each, all connected to the single switch. There
       are also n hosts that are not in any VLAN, also connected to
       the switch."""

    def build( self, k=2, n=2, vlanBase=100 ):
        s1 = self.addSwitch( 's1' )
        for i in range( k ):
            vlan = vlanBase + i
            for j in range(n):
                name = 'h%d-%d' % ( j+1, vlan )
                h = self.addHost( name, cls=VLANHost, vlan=vlan )
                self.addLink( h, s1 )
        for j in range( n ):
            h = self.addHost( 'h%d' % (j+1) )
            self.addLink( h, s1 )


def exampleCustomTags(i=2,j=2,k=1):
    """Simple example that exercises VLANStarTopo"""

    # net = Mininet( topo=VLANStarTopo() )
    net = Mininet(controller=c,topo=TreeTopoVlan(depth=i, fanout=j, vlanno=k) )
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

    if not quietRun( 'which vconfig' ):
        error( "Cannot find command 'vconfig'\nThe package",
               "'vlan' is required in Ubuntu or Debian,",
               "or 'vconfig' in Fedora\n" )
        exit()

    if len( sys.argv ) == 1:
        exampleCustomTags()
    else:
        exampleCustomTags(i=int( sys.argv[ 1 ] ) , j=int( sys.argv[ 2 ] ) , k=int( sys.argv[ 3 ] )  )
    

#!/usr/bin/python

from mininet.topo import Topo
from mininet.net import Mininet
import re, sys, time, select, os, subprocess, threading, errno
from mininet.net import Mininet
from mininet.node import Controller, Host, CPULimitedHost
from mininet.cli import CLI
from subprocess import call,check_output, Popen, PIPE, STDOUT
from mininet.log import setLogLevel, info, debug
from optparse import OptionParser
from mininet.topo import Topo
from mininet.link import TCLink
from mininet.util import isShellBuiltin, dumpNodeConnections




output = open("output.log", "w+")



class DockerHost( Host ):
    def __init__( self, name, image='sdn2', dargs=None, startString=None, **kwargs ):
        self.image = image
        self.dargs = dargs
        if startString is None:
            self.startString = "/bin/bash"
            self.dargs = "-ti"
        else:
            self.dargs = "-ti"
            self.startString = startString
        Host.__init__( self, name, **kwargs )
    def sendCmd( self, *args, **kwargs ):
        assert not self.waiting
        printPid = kwargs.get( 'printPid', True )
        if len( args ) == 1 and type( args[ 0 ] ) is list:
            cmd = args[ 0 ]
        elif len( args ) > 0:
            cmd = args
        if not isinstance( cmd, str ):
            cmd = ' '.join( [ str( c ) for c in cmd ] )
        if not re.search( r'\w', cmd ):
            cmd = 'echo -n'
        self.lastCmd = cmd
        printPid = printPid and not isShellBuiltin( cmd )
        if len( cmd ) > 0 and cmd[ -1 ] == '&':
            cmd += ' printf "\\001%d\n\\177" $! \n'
        else:
            cmd += '; printf "\\177"'
        self.write( cmd + '\n' )
        self.lastPid = None
        self.waiting = True
        #print "command to %s = %s" %(self.name, cmd)
    def popen( self, *args, **kwargs ):
        mncmd = [ 'docker', 'attach', ""+self.name ]
        return Host.popen( self, *args, mncmd=mncmd, **kwargs )
    def terminate( self ):
        #if self.shell:
            #subprocess.call(["docker rm -f "+self.name], shell=True, stdout=output)
        self.cleanup()
    def startShell( self ):
        global startString1
        if self.shell:
            error( "%s: shell is already running" )
            return
        subprocess.call(["docker stop "+self.name], shell=True, stdout=output)
        subprocess.call(["docker rm -f "+self.name], shell=True, stdout=output)

        cmd = ["docker","run","--privileged","-h",self.name ,"--name="+self.name,"-v", "/vagrant:/home/ubuntu"]
        if self.dargs is not None:
            cmd.extend([self.dargs])
        # cmd.extend(["--net='none'",self.image, "/bin/bash", "-c", "'./pox/pox.py", "forwarding.l2_learning" ,"--port=20001","&", "/bin/bash'", startString1 ])
        cmd.extend(["--net='none'",self.image, "/home/ubuntu/run.sh" ])

        self.shell = Popen( cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT, close_fds=True )
        self.stdin = self.shell.stdin
        self.stdout = self.shell.stdout
        self.pid = self.shell.pid
        self.pollOut = select.poll()
        self.pollOut.register( self.stdout )
        self.outToNode[ self.stdout.fileno() ] = self
        self.inToNode[ self.stdin.fileno() ] = self
        self.execed = False
        self.lastCmd = None
        self.lastPid = None
        self.readbuf = ''
        self.waiting = False
        call("sleep 1", shell=True)
        pid_cmd = ["docker","inspect","--format='{{ .State.Pid }}'",""+self.name]
        pidp = Popen( pid_cmd, stdin=PIPE, stdout=PIPE, stderr=STDOUT, close_fds=False )
        ps_out = pidp.stdout.readlines()
        self.pid = int(ps_out[0])



class MyTreeTopo( Topo ):
    "Topology for a tree network with a given depth and fanout."

    def build( self, depth=1, fanout=2 ):
        # Numbering:  h1..N, s1..M
        self.hostNum = 1
        self.switchNum = 1
        # Build topology
        self.addTree( depth, fanout )

    def addTree( self, depth, fanout ):
        global startString1
        """Add a subtree starting with node n.
           returns: last node added"""
        isSwitch = depth > 0
        if isSwitch:
            node = self.addSwitch( 's%s' % self.switchNum )
            self.switchNum += 1
            for _ in range( fanout ):
                child = self.addTree( depth - 1, fanout )
                self.addLink( node, child )
        else:
            startString1 = ""
            node = self.addHost( 'h%s' % self.hostNum, ip='10.0.2.%s' %self.hostNum ,cls=DockerHost)
            self.hostNum += 1
        return node
topos = { 'mytree':  ( lambda: MyTreeTopo(2,2) ) }
#(startString="/bin/bash -c 'pox.py log.level --DEBUG openflow.of_01 --address=%s --port=20002 forwarding.l2_learning' % self.hostNu "  m, dargs="ti") )

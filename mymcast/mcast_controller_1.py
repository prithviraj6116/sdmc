#!/usr/bin/python

"""
Test for fattree and jellyfish topology. No controller
"""


from mininet.topo import *
from scipy.stats import truncnorm, tstd, poisson, expon
from numpy.random import randint, uniform
from datetime import datetime
import os, sys, signal, random
import networkx as nx
from mininet.net import Mininet
from mininet.node import OVSSwitch, Controller, RemoteController
from mininet.topolib import TreeTopo
from mininet.log import setLogLevel
from mininet.cli import CLI
from mininet.node import OVSSwitch, UserSwitch
from mininet.link import TCLink
from mininet.node import Node, RemoteController
from scipy.stats import truncnorm
from numpy.random import randint, uniform
from subprocess import *
from time import sleep, time
from multiprocessing import Process, Pipe
import numpy as np
from networkx import graphviz_layout
try:
    import matplotlib.pyplot as plt
except:
    raise

ENABLE_FIXED_GROUP_SIZE = True
FIXED_GROUP_SIZE = 4


setLogLevel( 'info' )






# adapted from https://reproducingnetworkresearch.wordpress.com/2012/06/04/jellyfish-vs-fat-tree/
class JellyFishTreeTopo( Topo ):
    "Simple JellyFishTreeTopo example comparable to fattree. n degree, (5n^2/4) total switches[20% core, 40% aggregated, 40% edge], , (n^3/4) servers"
    fatdegree = 4;
    switchno = 5/4 * fatdegree * fatdegree
    hostnodeno = fatdegree * fatdegree * fatdegree / 4
    switchlist = []
    hostnodelist = []
    switchiflist = []
    basebw = 10;
    G=nx.Graph()    


    def __init__ (self, fatdegree, basebw):

        if fatdegree % 2 != 0:
            print "coreswitch should be multiple of 2";
            return;
        Topo.__init__(self);
        self.fatdegree = fatdegree;
        self.switchno = 5/4 * fatdegree * fatdegree
        self.hostnodeno = fatdegree * fatdegree * fatdegree / 4
        self.basebw = basebw;

        for count in range(self.switchno):
            self.switchlist.append(self.addSwitch("s" + str(count)))
            self.switchiflist.append(self.fatdegree);
            self.G.add_node("s"+str(count), weight=2)
        for count in range(self.hostnodeno):
            self.hostnodelist.append(self.addHost("h" + str(count)))
            self.G.add_node("h"+str(count), weight=1)

        count = 0;
        while count < self.hostnodeno:
            random_switch = int(random.random() * (self.switchno-1));
            if self.switchiflist[random_switch] == 1:
                continue;
            self.addLink(self.hostnodelist[count], self.switchlist[random_switch], bw = self.basebw, use_htb = True);
            self.switchiflist[random_switch] -= 1;
            self.G.add_edge("h"+str(count), "s"+str(random_switch), weight=1)
            count += 1 ;

        totaledges = self.hostnodeno + self.fatdegree * self.switchno;
        countedges = 2 * self.hostnodeno;
        while totaledges > (2 + countedges):
            for count1 in range (self.switchno):
                for count2 in range (1, self.switchno, int(random.random()*(self.switchno-2)+1)):
                    count3 = (count2 + count1) % self.switchno;
                    if self.switchiflist[count3] > 0  and self.switchiflist[count1] > 0:
                        self.addLink(self.switchlist[count1], self.switchlist[count3], bw=self.basebw, use_htb=True);
                        self.G.add_edge("s"+str(count1), "s"+str(count3), weight=2)
                        self.switchiflist[count1] -= 1;
                        self.switchiflist[count3] -= 1;
                        countedges += 2;

    def  drawtree(self):
        pos=nx.spring_layout(self.G)#, k=0.15,iterations=20) # positions for all nodes
        #nx.draw(self.G, nodelist=d.keys(), node_size=[v * 100 for v in d.values()], cmap=plt.get_cmap('jet'), node_color=values)
        # pos = nx.graphviz_layout(self.G, prog='dot')#, args="-Grankdir=TB")
        #pos=nx.graphviz_layout(self.G)#, prog='dot', args="-Gnodesep=12")                                 
        #pos=nx.graphviz_layout(self.G, prog='sfdp'); #http://stackoverflow.com/questions/14727820/networkx-style-spring-model-layout-for-directed-graphs-in-graphviz-pygraphviz
        nhost=[(u) for (u,d) in self.G.nodes(data=True) if  d['weight'] == 1]
        nswitch=[(u) for (u,d) in self.G.nodes(data=True) if d['weight'] == 2]
        nx.draw_networkx_nodes(self.G,pos,nodelist=nhost,node_size=500, node_color='b')
        nx.draw_networkx_nodes(self.G,pos,nodelist=nswitch,node_size=1000, node_color='r')
        esmall=[(u,v) for (u,v,d) in self.G.edges(data=True) if d['weight'] == 1]
        elarge=[(u,v) for (u,v,d) in self.G.edges(data=True) if d['weight'] == 2]
        nx.draw_networkx_edges(self.G,pos,edgelist=elarge, width=1,alpha=0.5,edge_color='r')#,style='dashed')
        nx.draw_networkx_edges(self.G,pos,edgelist=esmall, width=1,alpha=0.5,edge_color='b')#,style='dashed')
        nx.draw_networkx_labels(self.G,pos,font_size=10,font_family='sans-serif')
        plt.axis('on')
        plt.savefig("weighted_graph1.png")
        plt.show() # display

    def mcastConfig(self, net):
        for count in range(self.hostnodeno):
            net.get('h'+str(count)).cmd('route add -net 224.0.0.0/4 h'+ str(count) +'-eth0')


    def get_host_list(self):
        hostlist = [];
        for count in range(self.hostnodeno):
            hostlist.append('h'+str(count));
        return hostlist;
        
    def get_switch_list(self):
        switchlist = []
        for count in range(self.switchno):
            switchlist.append('s'+str(count));
        return switchlist


# Adapated from https://gist.github.com/pichuang/9875468 and https://storagemojo.com/2008/08/24/fat-trees-and-skinny-switches/

class FatTreeTopo( Topo ):
    "Simple FatTreeTopo example. n degree, (5n^2/4) total switches[20% core, 40% aggregated, 40% edge], , (n^3/4) servers"
    
    fatdegree = 4;
    coreswitchno = (fatdegree / 2) * (fatdegree / 2);
    aggrswitchno = coreswitchno * 2
    edgeswitchno = aggrswitchno
    hostnodeno = edgeswitchno * fatdegree
    coreswitchlist = []
    aggrswitchlist = []
    edgeswitchlist = []
    hostnodelist = []
    basebw = 10;
    G=nx.Graph()    

    def __init__ (self, fatdegree, basebw):

        if fatdegree % 2 != 0:
            print "coreswitch should be multiple of 2";
            return;
        Topo.__init__(self);
        self.fatdegree = fatdegree;
        self.coreswitchno = (self.fatdegree / 2) * (self.fatdegree / 2);
        self.aggrswitchno = self.coreswitchno * 2
        self.edgeswitchno = self.aggrswitchno
        self.hostnodeno = self.edgeswitchno * (self.fatdegree / 2)
        self.basebw = basebw;
        
        for count in range(self.coreswitchno):
            self.coreswitchlist.append(self.addSwitch("cs" + str(count)))
            self.G.add_node("cs"+str(count), weight=2)
        for count in range(self.aggrswitchno):
            self.aggrswitchlist.append(self.addSwitch("as" + str(count)))
            self.G.add_node("as"+str(count), weight=3)
        for count in range(self.edgeswitchno):
            self.edgeswitchlist.append(self.addSwitch("es" + str(count)))
            self.G.add_node("es"+str(count), weight=4)
        for count in range(self.hostnodeno):
            self.hostnodelist.append(self.addHost("h" + str(count)))
            self.G.add_node("h"+str(count), weight=1)


        for edgeswitchcount in range(0, self.edgeswitchno):
            for hostcount in range(0, self.fatdegree/2):
                self.addLink(self.edgeswitchlist[edgeswitchcount], self.hostnodelist[(self.fatdegree/2) * edgeswitchcount + hostcount], bw = self.basebw, use_htb = True);
                self.G.add_edge("h"+str((self.fatdegree/2) * edgeswitchcount + hostcount), "es"+str(edgeswitchcount), weight=1)

            
        for aggrswitchcount in range(0, self.aggrswitchno):
            for fatdegreecount in range(0, self.fatdegree/2):
                self.addLink(self.aggrswitchlist[aggrswitchcount], self.edgeswitchlist[aggrswitchcount + fatdegreecount - aggrswitchcount % (self.fatdegree/2)], 
                             bw = self.basebw, use_htb = True);
                self.G.add_edge("as"+str(aggrswitchcount), "es"+str(aggrswitchcount + fatdegreecount - aggrswitchcount % (self.fatdegree/2)), weight=2)

        for aggrswitchcount in range(0, self.aggrswitchno):
            for coreswitchcount in range(0, self.fatdegree/2):
                self.addLink(self.aggrswitchlist[aggrswitchcount], self.coreswitchlist[(self.fatdegree/2) * (aggrswitchcount%(self.fatdegree/2)) + coreswitchcount], 
                             bw = self.basebw, use_htb = True);
                self.G.add_edge("as"+str(aggrswitchcount), "cs"+str((self.fatdegree/2) * (aggrswitchcount%(self.fatdegree/2)) + coreswitchcount), weight=2)

    def  drawtree(self):
        # pos=nx.spring_layout(self.G)#, k=0.15,iterations=20) # positions for all nodes
        #nx.draw(self.G, nodelist=d.keys(), node_size=[v * 100 for v in d.values()], cmap=plt.get_cmap('jet'), node_color=values)
        pos = nx.graphviz_layout(self.G)#, prog='dot', args="-Grankdir=LR")
        #pos=nx.graphviz_layout(self.G)#, prog='dot', args="-Gnodesep=12")                                 
        #pos=nx.graphviz_layout(self.G, prog='sfdp'); #http://stackoverflow.com/questions/14727820/networkx-style-spring-model-layout-for-directed-graphs-in-graphviz-pygraphviz
        nhost=[(u) for (u,d) in self.G.nodes(data=True) if  d['weight'] == 1]
        nswitchc=[(u) for (u,d) in self.G.nodes(data=True) if d['weight'] == 2]
        nswitcha=[(u) for (u,d) in self.G.nodes(data=True) if d['weight'] == 3]
        nswitche=[(u) for (u,d) in self.G.nodes(data=True) if d['weight'] == 4]
        nx.draw_networkx_nodes(self.G,pos,nodelist=nhost,node_size=400, node_color='b', node_shape='s', label='host')
        nx.draw_networkx_nodes(self.G,pos,nodelist=nswitchc,node_size=600, node_color='r')
        nx.draw_networkx_nodes(self.G,pos,nodelist=nswitcha,node_size=600, node_color='y')
        nx.draw_networkx_nodes(self.G,pos,nodelist=nswitche,node_size=600, node_color='g')
        esmall=[(u,v) for (u,v,d) in self.G.edges(data=True) if d['weight'] == 1]
        elarge=[(u,v) for (u,v,d) in self.G.edges(data=True) if d['weight'] == 2]
        nx.draw_networkx_edges(self.G,pos,edgelist=elarge, width=1,alpha=0.5,edge_color='r')#,style='dashed')
        nx.draw_networkx_edges(self.G,pos,edgelist=esmall, width=1,alpha=0.5,edge_color='b')#,style='dashed')
        nx.draw_networkx_labels(self.G,pos,font_size=10,font_family='sans-serif')
        plt.axis('on')
        plt.savefig("weighted_graph.png")
        plt.show() # display

    def mcastConfig(self, net):
        for count in range(self.hostnodeno):
            net.get('h'+str(count)).cmd('route add -net 224.0.0.0/4 h'+ str(count) +'-eth0')

    def get_host_list(self):
        hostlist = [];
        for count in range(self.hostnodeno):
            hostlist.append('h'+str(count));
        return hostlist;
        
    def get_switch_list(self):
        switchlist = []
        for count in range(self.coreswitchno):
            switchlist.append('cs'+str(count));
        for count in range(self.aggrswitchno):
            switchlist.append('as'+str(count));
        for count in range(self.edgeswitchno):
            switchlist.append('es'+str(count));
        return switchlist






class SmallTopo( Topo ):
    "Simple multicast testing example."
    
    def __init__( self ):
        "Create custom topo."
        
        # Initialize topology
        Topo.__init__( self )
        
        # Add hosts and switches
        h1 = self.addHost('h1', ip='10.0.0.1')
        h2 = self.addHost('h2', ip='10.0.0.2')
        h3 = self.addHost('h3', ip='10.0.0.3')
        
        s1 = self.addSwitch('s1')
        s2 = self.addSwitch('s2')
        
        # Add links
        self.addLink(s1, h1, bw = 10, use_htb = True)
        self.addLink(s2, h2, bw = 10, use_htb = True)
        self.addLink(s2, h3, bw = 10, use_htb = True)
        self.addLink(s1, s2, bw = 10, use_htb = True)


    def mcastConfig(self, net):
        # Configure hosts for multicast support
        net.get('h1').cmd('route add -net 224.0.0.0/4 h1-eth0')
        net.get('h2').cmd('route add -net 224.0.0.0/4 h2-eth0')
        net.get('h3').cmd('route add -net 224.0.0.0/4 h3-eth0')
    
    def get_host_list(self):
        return ['h1', 'h2', 'h3']
    
    def get_switch_list(self):
        return ['s1', 's2']










def mcast(topo, interactive = False, hosts = [], log_file_name = 'test_log.log', util_link_weight = 10, link_weight_type = 'linear', replacement_mode='none', pipe = None):
    membership_mean = 0.1
    membership_std_dev = 0.25
    membership_avg_bound = float(len(hosts)) / 8.0
    test_groups = []
    test_group_launch_times = []
    test_success = True
    
    # /home/prithviraj-isis/Downloads/repos/bitbucket/Programming/sdn/multicast
    # Launch the external controller
    pox_arguments = []
    if 'periodic' in replacement_mode:
        pox_arguments = ['/vagrant/pox/pox.py', 'log', '--file=pox.log,w', 'openflow.discovery', '--link_timeout=30', 'openflow.keepalive',
                'openflow.flow_tracker', '--query_interval=1', '--link_max_bw=19', '--link_cong_threshold=13', '--avg_smooth_factor=0.5', '--log_peak_usage=True',
                'misc.benchmark_terminator', 'openflow.igmp_manager', 'misc.groupflow_event_tracer',
                'openflow.groupflow', '--util_link_weight=' + str(util_link_weight), '--link_weight_type=' + link_weight_type, '--flow_replacement_mode=' + replacement_mode,
                '--flow_replacement_interval=15',
                'log.level', '--WARNING', '--openflow.flow_tracker=INFO']
    else:
        # pox_arguments = ['/vagrant/pox/pox.py', 'log', '--file=pox.log,w', 'openflow.discovery', '--link_timeout=30', 'openflow.keepalive', 'openflow.flow_tracker', '--query_interval=1', '--link_max_bw=19', '--link_cong_threshold=13', '--avg_smooth_factor=0.5', '--log_peak_usage=True', 'openflow.igmp_manager',  'openflow.groupflow', '--util_link_weight=' + str(util_link_weight), '--link_weight_type=' + link_weight_type, '--flow_replacement_mode=' + replacement_mode, '--flow_replacement_interval=15']
        #pox_arguments = ['/vagrant/pox/pox.py','openflow.discovery','forwarding.l3_learning']

        # pox_arguments = ['/home/prithviraj-isis/Downloads/repos/bitbucket/Programming/sdn/multicast/pox/pox.py', 'log', '--file=pox.log,w', 'openflow.discovery', '--link_timeout=30', 'openflow.keepalive', 'openflow.flow_tracker', '--query_interval=1', '--link_max_bw=19', '--link_cong_threshold=13', '--avg_smooth_factor=0.5', '--log_peak_usage=True','misc.benchmark_terminator', 'openflow.igmp_manager', 'misc.groupflow_event_tracer', 'openflow.groupflow', '--util_link_weight=' + str(util_link_weight), '--link_weight_type=' + link_weight_type, '--flow_replacement_mode=' + replacement_mode, '--flow_replacement_interval=15','log.level', '--WARNING', '--openflow.flow_tracker=INFO']

        # pox_arguments = ['/vagrant/pox/pox.py', 'log', '--file=pox.log,w', 'openflow.discovery', '--link_timeout=30', 'openflow.keepalive', 'openflow.flow_tracker', '--query_interval=1', '--link_max_bw=19', '--link_cong_threshold=13', '--avg_smooth_factor=0.5', '--log_peak_usage=True','misc.benchmark_terminator', 'openflow.igmp_manager', 'misc.groupflow_event_tracer', 'openflow.groupflow', '--util_link_weight=' + str(util_link_weight), '--link_weight_type=' + link_weight_type, '--flow_replacement_mode=' + replacement_mode, '--flow_replacement_interval=15','log.level', '--WARNING', '--openflow.flow_tracker=INFO']

        pox_arguments = ['/vagrant/pox/pox.py', 'log', '--file=pox.log,w', 'openflow.discovery', '--link_timeout=30', 'openflow.keepalive', 'openflow.flow_tracker', '--query_interval=1', '--link_max_bw=19', '--link_cong_threshold=13', '--avg_smooth_factor=0.5', '--log_peak_usage=True', 'misc.benchmark_terminator', 'openflow.igmp_manager', 'misc.groupflow_event_tracer', 'openflow.groupflow', '--util_link_weight=' + str(util_link_weight), '--link_weight_type=' + link_weight_type, '--flow_replacement_mode=' + replacement_mode, '--flow_replacement_interval=15', 'log.level', '--WARNING', '--openflow.flow_tracker=INFO']

    print 'Launching external controller: ' + str(pox_arguments[0])
    print 'Launch arguments:'
    print ' '.join(pox_arguments)
    
    with open(os.devnull, "w") as fnull:
        pox_process = Popen(pox_arguments, stdout=fnull, stderr=fnull, shell=False, close_fds=True)
        # Allow time for the log file to be generated
        sleep(1)
    
    # # Determine the flow tracker log file
    # pox_log_file = open('./pox.log', 'r')
    # flow_log_path = None
    # event_log_path = None
    # got_flow_log_path = False
    # got_event_log_path = False
    # while (not got_flow_log_path) or (not got_event_log_path):
    #     pox_log = pox_log_file.readline()

    #     if 'Writing flow tracker info to file:' in pox_log:
    #         pox_log_split = pox_log.split()
    #         flow_log_path = pox_log_split[-1]
    #         got_flow_log_path = True
        
    #     if 'Writing event trace info to file:' in pox_log:
    #         pox_log_split = pox_log.split()
    #         event_log_path = pox_log_split[-1]
    #         got_event_log_path = True
            
            
    # print 'Got flow tracker log file: ' + str(flow_log_path)
    # print 'Got event trace log file: ' + str(event_log_path)
    # print 'Controller initialized'
    # pox_log_offset = pox_log_file.tell()
    # pox_log_file.close()
    
    # External controller
    net = Mininet(topo, controller=RemoteController, switch=OVSSwitch, link=TCLink, build=False, autoSetMacs=True)
    #pox = RemoteController('pox', '127.0.0.1', 6633)
    net.addController('pox', RemoteController, ip = '127.0.0.1', port = 6633)
    net.start()
    for switch_name in topo.get_switch_list():
        #print switch_name + ' route add -host 127.0.0.1 dev lo'
        net.get(switch_name).controlIntf = net.get(switch_name).intf('lo')
        net.get(switch_name).cmd('route add -host 127.0.0.1 dev lo')
        #print 'pox' + ' route add -host ' + net.get(switch_name).IP() + ' dev lo'
        net.get('pox').cmd('route add -host ' + net.get(switch_name).IP() + ' dev lo')
        print("test: route add -host " + net.get(switch_name).IP() + " dev lo")
        #print net.get(switch_name).cmd('ifconfig')
        
    topo.mcastConfig(net)
    
    #print 'Controller network configuration:'
    #print net.get('pox').cmd('ifconfig')
    #print net.get('pox').cmd('route')
    
    sleep_time = 2 + (float(len(hosts))/8)
    print 'Waiting ' + str(sleep_time) + ' seconds to allow for controller topology discovery'
    sleep(sleep_time)   # Allow time for the controller to detect the topology
    
    CLI(net)

    print 'Waiting for controller termination...'
    pox_process.send_signal(signal.SIGKILL)
    pox_process.wait()
    print 'Controller terminated'
    pox_process = None
    net.stop()




def main():
    setLogLevel( 'info' )
    if len(sys.argv) < 3:
        print "Usage: python test_topo.py [1|2|3] [1|0 for topology plot]"
        return -1
    topo_n=int(sys.argv[1]);
    if topo_n == 1:
        topo = FatTreeTopo(fatdegree=4,basebw=1)
    elif topo_n == 2:
        topo = JellyFishTreeTopo(fatdegree=6,basebw=1)
    elif topo_n == 3:
        topo = SmallTopo()
    else:
        print "Usage: python test_topo.py [1|2] "
        return -1;
    plot=int(sys.argv[2]);
    if plot == 1 and (topo_n ==1 or topo_n == 2):
        print "hi";
        topo.drawtree();
    #mcast(topo, True, topo.get_host_list())



if __name__ == "__main__":
    main()


# net = Mininet( topo, controller=RemoteController, link=TCLink, build=False, autoSetMacs=True)
# net.start()
# topo.mcastConfig(net)
# print topo.get_host_list();
# print topo.get_switch_list();
# CLI( net )







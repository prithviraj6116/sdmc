import socket, struct, sys, threading, time, logging, pickle
from scapy.all import *
import netifaces as ni
from pubsub_ds import *
from random import *

mip = "224.1.1.2"
mport = 5005
mport1 = 5006
PACKET_SIZE = 65000
send_wait = 5
sim_send_wait = 10
update_wait = 100
host = 0
ip = 0
part_i = 0
part_pub = 0;
part_sub = 0
part_conf = 0
sim_pubdata = 0;
temp_topics = []

class participant_sub(threading.Thread):
    def __init__(self):
        super(participant_sub,self).__init__();
    def run(self):
        print "----part_sub-------";
        global host, part_i, part_sr, topic_part;
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind(('', mport))
        mreq = struct.pack("=4sl", socket.inet_aton(mip), socket.INADDR_ANY)
        sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
    
        while True:
            try:
                data, addr = sock.recvfrom(PACKET_SIZE)
                print "abc"
                print str(addr[0])
                if(str(addr[0])):
                    d = pickle.loads(data);
                    if((d.host not in part_sr) or (part_sr[d.host] < d.sr)):
                        part_sr[d.host] = int(d.sr)
                        for i in range(max_topics):
                            top =  str("t"+str(i));
                            if (top in d.topics):
                                topic_part[top][d.host] = 1
                            else:
                                try:
                                    del topic_part[top][d.host];
                                except Exception,e:
                                    pass
                    print "topic_part: " + str(topic_part) + "\n";
                print "def"
            except Exception,e:
                print str(e);

class participant_pub(threading.Thread):
    def __init__(self):
        super(participant_pub,self).__init__();

    def send(self):
        global part_i, send_wait
        part_i.sr += 1;
        data=pickle.dumps( part_i)
        a=IP(dst=mip)/UDP(sport=56789, dport=mport)/data
        try:
            send(a);
        except Exception,e: 
            print str(e)
        threading.Timer(send_wait,self.send).start();

    def send_nonperiodic(self):
        global part_i, send_wait
        part_i.sr += 1;
        data=pickle.dumps( part_i)
        a=IP(dst=mip)/UDP(sport=56789, dport=mport)/data
        try:
            send(a);
        except Exception,e: 
            print str(e)

    def run(self):
        print "----part_pub-------";
        self.send();


class participant_conf(threading.Thread):
    def __init__(self):
        super(participant_conf, self).__init__();

    def set(self):
        global part_i, host,max_topics, set_wait, max_sender_per_node, max_receiver_per_node
        total_t = randint(1, max_topics);
        for i in range(max_topics):
            temp_topics.append("t"+str(i+1));
        for i in range(total_t):
            r1 = randint(0, len(temp_topics)-1);
            part_i.addtopic(temp_topics[r1]);
            part_i.senders[host+"_s"+str(i)] = temp_topics[r1]
            # TODO: send a packet to an particular ip saying I have publisher
            part_i.receivers[host+"_r"+str(i)] = temp_topics[r1]
            # TODO: send a packet to an particular ip saying I have subscriber
            temp_topics.pop(r1);

        
    def update(self):
        if(part_i.sr % 2 == 0):
            if(len(temp_topics) != 0):
                r1 = randint(0, len(temp_topics)-1);
                part_i.addtopic(temp_topics[r1]);
                part_i.senders[host+"_s"+str(len(part_i.senders))] = temp_topics[r1]
                part_i.receivers[host+"_r"+str(len(part_i.receivers))] = temp_topics[r1]
                temp_topics.pop(r1);

        else:
            if(len(part_i.topics) >= 2):
                r = part_i.topics1[len(part_i.topics1) - 1]
                part_i.topics1.remove(r)
                del part_i.topics[r]
                temp_topics.append(r);
                temp_s = part_i.senders.keys()
                for s in temp_s:
                    if ( part_i.senders[s] == r):
                        del part_i.senders[s];
                temp_r = part_i.receivers.keys()
                for s in temp_r:
                    if ( part_i.receivers[s] == r):
                        del part_i.receivers[s];


        print "part_i in update = " + str(part_i)
        part_pub.send_nonperiodic();
        threading.Timer(update_wait,self.update).start();



        
    def run(self):
        print "----part_conf-------";
        self.set()
        #self.update()




class simulate_pubdata(threading.Thread):
    def __init__(self):
        self.count_s_mcast = 0
        super(simulate_pubdata,self).__init__();

    def sim_senders_mcast(self):
        global part_i, sim_send_wait

        try:
            for s in part_i.senders:
                # print "Topic numbers: 225.1.2." + part_i.senders[s][1:]
                s_mip = "225.1.2." + str(part_i.senders[s][1:])
                data=pickle.dumps(topic(self.count_s_mcast, 12, 13 ,14))
                a=IP(dst=s_mip)/UDP(sport=56788, dport=mport1)/data
                send(a);
            self.count_s_mcast += 1
        except Exception,e: 
            print str(e)
        threading.Timer(sim_send_wait,self.sim_senders_mcast).start();
    def run(self):
        for r in part_i.receivers:
            sim_sub = simulate_subdata(r);
            sim_sub.start()
        self.sim_senders_mcast();
        



class simulate_subdata(threading.Thread):
    def __init__(self, rec_a):
        self.rec = rec_a;
        self.topic_sr = -1 #topic to sr for data receivers
        super(simulate_subdata,self).__init__();


        
    def sim_receivers_mcast(self):
        global host, part_i, part_sr, topic_part;
        r_mip = "225.1.2." + str(part_i.receivers[self.rec][1:])
        # print "r_mip: " + str(r_mip)
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind(('', mport1))
        mreq = struct.pack("=4sl", socket.inet_aton(r_mip), socket.INADDR_ANY)
        sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
        
        while True:
            try:
                data, addr = sock.recvfrom(PACKET_SIZE)

                if(str(addr[0])):
                    d = pickle.loads(data);
                    # print "t = "+ str(self.topic_sr)+ " d.sr= "+str(d.sr)
                    if(self.topic_sr < int(d.sr)):
                        self.topic_sr = int(d.sr)
                        print "data receiver for " + str(self.rec) + " on topic " + str(part_i.receivers[self.rec][1:]) + " with sr no " + str(self.topic_sr);
                        
            except Exception,e:
                print str(e);


    def run(self):
        self.sim_receivers_mcast();



        
            
def getip(host):
    intf=host+'-eth0'
    ni.ifaddresses(intf)
    ip = ni.ifaddresses(intf)[2][0]['addr']
    return ip

def main():
    global host, ip, part_i, part_pub, part_sub, part_conf, topic_part;


    if len(sys.argv) < 2:
        print "Usage: python pubsub.py [hostname]"
        return -1

    logging.getLogger("scapy").setLevel(1)
    conf.L3socket=L3RawSocket

    host=sys.argv[1]
    ip = getip(host);
    part_i = part_info(ip, host);


    for i in range(max_topics):
        topic_part["t"+str(i)] = {}

    part_pub = participant_pub();
    part_sub = participant_sub();
    part_conf = participant_conf();
    sim_pubdata = simulate_pubdata();
    part_pub.start();
    part_sub.start();
    part_conf.start();
    time.sleep(2);
    sim_pubdata.start();
    part_pub.join();
    part_sub.join();
    part_conf.join();
    sim_pubdata.join();


if __name__ == "__main__":
    main()
 

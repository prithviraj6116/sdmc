import socket, struct, sys, threading, time, logging, pickle
from scapy.all import *
import netifaces as ni

brdcst_ip = "224.1.1.1"
#brdcst_ip = "224.0.0.2"
brdcst_port = 5007
ip = "0.0.0.0"
ip_port = 12345
# class host_listen(threading.Thread):
#     def __init__(self):
#         super(host_listen,self).__init__();
#     def run(self):
#         print "host listening run";
#         sock = socket.socket(socket.AF_INET, # Internet
#                              socket.SOCK_DGRAM) # UDP
#         try:
#             sock.bind((brdcst_ip, brdcst_port));
#         except Exception,e: 
#             print str(e)
#         while True:
#             try:
#                 data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
#                 print "received message:", pickle.loads(data), addr
#             except Exception,e: 
#                 print str(e)




class host_send(threading.Thread):
    def __init__(self):
        super(host_send,self).__init__();
    def send(self):
        print "host sending";
        favorite_color = { "lion": "yellow", "kitty": "red" }
        data=pickle.dumps( favorite_color)
        a=IP(dst=brdcst_ip)/UDP(sport=56789, dport=brdcst_port)/data
        try:
            send(a);
        except Exception,e: 
            print str(e)
        threading.Timer(1,self.send).start();

    def run(self):
        print "host sending run";
        self.send();
        # wireshark(a)



def getip():
    host=sys.argv[1]
    intf=host+'-eth0'
    ni.ifaddresses(intf)
    ip = ni.ifaddresses(intf)[2][0]['addr']
    print ip  # should print "192.168.100.37"

def main():
    if len(sys.argv) < 3:
        print "Usage: python pubsub.py [hostname] [send_ip]"
        return -1

    logging.getLogger("scapy").setLevel(1)
    conf.L3socket=L3RawSocket

    # hl1 = host_listen();
    # hl1.start();

    hs1 = host_send();
    hs1.start();

    # hl1.join();
    hs1.join();


if __name__ == "__main__":
    main()
 

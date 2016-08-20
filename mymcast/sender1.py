#!/usr/bin/env python
import socket
import struct
import sys
import threading
import time
import socket, struct, sys, threading, time, logging, pickle
from scapy.all import *
import netifaces as ni

# To work in Mininet routes must be configured for hosts similar to the following:
# route add -net 224.0.0.0/4 h1-eth0

quit_flag = False

multicast_group = '224.1.1.1'
multicast_port = 5007
echo_port = 5008
send_socket = None
send_packet_index = 1
send_packet_times = {}
echo_packet_times = {}

PACKET_INTERVAL = 3
PACKET_SIZE = 512
INITIAL_DELAY = 1

def send_multicast_packet():
    global send_packet_index, send_packet_times
    send_string = str(send_packet_index).zfill(PACKET_SIZE)
    print 'Send String: ' + str(send_string)
    send_packet_times[send_packet_index] = time.time()
    try:
        favorite_color = { "lion": "yellow", "kitty": "red" }
        data=pickle.dumps( favorite_color)
        bytes = send_socket.sendto(data, (multicast_group, multicast_port))
        print 'Sent multicast packet ' + str(send_packet_index) + ' at: ' + str(send_packet_times[send_packet_index]) + ' (' + str(bytes) + ' bytes)'
    except:
        print 'Socket error occurred, skipped sending packet: ' + str(send_packet_index)
    send_packet_index += 1



    if not quit_flag:
        threading.Timer(PACKET_INTERVAL, send_multicast_packet).start()
    
def main():
    global multicast_group, multicast_port, send_socket, echo_port
    
    # if len(sys.argv) > 1:
    #     multicast_group = sys.argv[1]
    
    # if len(sys.argv) > 2:
    #     multicast_port = int(sys.argv[2])
    
    # if len(sys.argv) > 3:
    #     echo_port = int(sys.argv[3])
    
    send_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
    send_socket.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, 32)
    send_socket.setblocking(False)
    
    print 'Starting multicast on group: ' + multicast_group + ':' + str(multicast_port)
    threading.Timer(INITIAL_DELAY, send_multicast_packet).start()
    
    # print 'Beginning listening on echo socket'
    # echo_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    # echo_socket.bind(('', echo_port))
    
    # last_echo_index = 1
    # while True:
    #     data, addr = echo_socket.recvfrom(PACKET_SIZE)
    #     echo_time = time.time() - send_packet_times[int(data)]
    #     echo_index = str(int(data))
    #     if echo_index != last_echo_index:
    #         print '=================='
    #     print 'Echo P#: ' + echo_index + '\tHost: ' + str(addr[0]) + '\t Time: ' + "{:0.6f}".format(echo_time * 1000) + ' ms'
    #     last_echo_index = echo_index

if __name__ == '__main__':
    main()

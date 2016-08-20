import socket
import struct
import sys
import threading
import time
import logging
import pickle

from scapy.all import *


UDP_IP = "127.0.0.1"
UDP_PORT = 5005


if __name__ == "__main__":
    logging.getLogger("scapy").setLevel(1)
    conf.L3socket=L3RawSocket
    sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
    sock.bind((UDP_IP, UDP_PORT))
    while True:
        data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
        print "received message:", pickle.loads(data), addr

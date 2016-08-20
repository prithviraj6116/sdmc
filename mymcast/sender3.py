import socket
import time

UDP_IP = "127.0.0.1"
UDP_PORT = 5005

print "UDP target IP:", UDP_IP
print "UDP target port:", UDP_PORT

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP
num = 0
while True:
  sock.sendto(str(num), (UDP_IP, UDP_PORT))
  print "Message sent: " + str(num)
  num += 1
  time.sleep(1)

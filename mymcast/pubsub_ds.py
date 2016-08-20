from collections import namedtuple

max_topics = 20
max_sender_per_node = 20
max_receiver_per_node = 20
part_sr = {}; # participant to sr for participant receiver
topic_part = {} # topic to participant for participant receiver
topic_sr = {} #topic to sr for data receivers

class topic():
    def __init__(self, sr, w, h, l):
        self.sr = sr;
        self.w = 0
        self.h = 0
        self.l = 0

class part_info():
    def __init__(self, ip, host):
        self.sr = 0;
        self.host = host
        self.ip = ip
        self.topics = {}
        self.topics1 = []
        self.senders = {}
        self.receivers = {}
        self.receivers_filters = {}
    def addtopic(self, topic):
        self.topics[topic] =  topic
        self.topics1.append(topic)
    def __str__(self):
        t =  "\n";
        # t += "sr = "+ str(self.sr) + "\n";
        # t += "ip = "+ str(self.ip) + "\n";
        # t += "host = "+ str(self.host) + "\n";
        # t += "topics = "+ str(self.topics) + "\n";
        t += "topics1 = "+ str(self.topics1) + "\n";
        t += "senders = "+ str(self.senders)+ "\n";
        t += "receivers = "+ str(self.receivers)+ "\n";
        # t += "receivers_filters = "+ str(self.receivers_filters)+ "\n";
        return t




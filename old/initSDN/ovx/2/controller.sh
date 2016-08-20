#!/usr/bin/env bash


sudo pkill -9 -f floodlight

vn="$(
sudo python ~/OpenVirteX/utils/ovxctl.py listVirtualNetworks
)"
e="[]"
if [ "$vn" != "$e" ] 
then
    vn=${vn%]*}
    vn=${vn#*[}
IFS=','
for word in $vn; 
do 
    python /home/vagrant/OpenVirteX/utils/ovxctl.py removeNetwork $word
done
fi
echo "starting creating virtual network"

id="$(
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createNetwork tcp:localhost:10000 10.0.0.0 16
)"
id1=${id#*tenantId\': }
tid=${id1%\}*}
echo "$tid"

result="$(
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createSwitch $tid 00:00:00:00:00:00:01:00
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createSwitch $tid 00:00:00:00:00:00:02:00

python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:01:00 1
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:01:00 2
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:01:00 5
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:02:00 1
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:02:00 2
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:02:00 5

python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectLink $tid 00:a4:23:05:00:00:00:01 3 00:a4:23:05:00:00:00:02 3 spf 1

python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid 00:a4:23:05:00:00:00:01 1 00:00:00:00:01:01
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid 00:a4:23:05:00:00:00:01 2 00:00:00:00:01:02
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid 00:a4:23:05:00:00:00:02 1 00:00:00:00:02:01
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid 00:a4:23:05:00:00:00:02 2 00:00:00:00:02:02
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n startNetwork $tid
)"


echo "$result"


java -jar /home/vagrant/floodlight/target/floodlight.jar  -cf /vagrant/initSDN/ovx/2/floodlight1.properties &> log1.floodlight &




id2="$(
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createNetwork tcp:localhost:10001 10.0.0.0 16
)"
id21=${id2#*tenantId\': }
tid2=${id21%\}*}
echo "$tid2"

result="$(
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createSwitch $tid2 00:00:00:00:00:00:01:00
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createSwitch $tid2 00:00:00:00:00:00:02:00

python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid2 00:00:00:00:00:00:01:00 3
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid2 00:00:00:00:00:00:01:00 4
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid2 00:00:00:00:00:00:01:00 5
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid2 00:00:00:00:00:00:02:00 3
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid2 00:00:00:00:00:00:02:00 4
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid2 00:00:00:00:00:00:02:00 5

python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectLink $tid2 00:a4:23:05:00:00:00:01 3 00:a4:23:05:00:00:00:02 3 spf 1

python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid2 00:a4:23:05:00:00:00:01 1 00:00:00:00:01:03
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid2 00:a4:23:05:00:00:00:01 2 00:00:00:00:01:04
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid2 00:a4:23:05:00:00:00:02 1 00:00:00:00:02:03
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid2 00:a4:23:05:00:00:00:02 2 00:00:00:00:02:04
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n startNetwork $tid2
)"

echo "$result"
java -jar /home/vagrant/floodlight/target/floodlight.jar  -cf /vagrant/initSDN/ovx/2/floodlight2.properties &> log2.floodlight &

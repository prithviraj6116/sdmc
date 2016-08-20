#!/usr/bin/env bash


vn="$(
sudo python ~/OpenVirteX/utils/ovxctl.py listVirtualNetworks
)"
e="[]"
if [ "$vn" == "$e" ]
    then  echo "starting creating virtual network"
else
    vn=${vn%]*}
    vn=${vn#*[}
IFS=','
for word in $vn; 
do 
    python /home/vagrant/OpenVirteX/utils/ovxctl.py removeNetwork $word
done
  echo "starting creating virtual network"
fi


id="$(
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createNetwork tcp:localhost:10000 10.0.0.0 16
)"
id1=${id#*tenantId\': }
tid=${id1%\}*}
echo "$tid"

python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createSwitch $tid 00:00:00:00:00:00:01:00
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createSwitch $tid 00:00:00:00:00:00:02:00
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createSwitch $tid 00:00:00:00:00:00:03:00
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:01:00 1
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:01:00 5
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:02:00 5
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:02:00 6
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:03:00 5
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n createPort $tid 00:00:00:00:00:00:03:00 2
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectLink $tid 00:a4:23:05:00:00:00:01 2 00:a4:23:05:00:00:00:02 1 spf 1
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectLink $tid 00:a4:23:05:00:00:00:02 2 00:a4:23:05:00:00:00:03 1 spf 1
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid 00:a4:23:05:00:00:00:01 1 00:00:00:00:01:01
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n connectHost $tid 00:a4:23:05:00:00:00:03 2 00:00:00:00:03:02
python /home/vagrant/OpenVirteX/utils/ovxctl.py -n startNetwork $tid




sudo pkill -9 -f floodlight
java -jar /home/vagrant/floodlight/target/floodlight.jar  -cf /vagrant/initSDN/ovx/1/floodlightdefault.properties


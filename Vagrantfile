# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.hostname = "sdn"
  config.vm.box = "ubuntu/trusty64"
  #config.vm.network "private_network", ip: "192.168.33.2"
  #config.vm.network "public_network"
  config.ssh.forward_agent = "true"
  config.ssh.forward_x11 = "true"
#  config.vm.provision :shell, path: "install_scripts/bootstrap.sh" , run: "always"
  config.vm.provider "virtualbox" do |vb|
    vb.name = "sdn"
     vb.gui = false
     vb.customize ["modifyvm", :id, "--memory", "512"]
     vb.customize ["modifyvm", :id, "--cpus", "2"]
     vb.vm.network : bridged, :bridge => 'eth0'
   end
end

#!/usr/bin/env bash

sudo pkill -9 -f nginx
sudo pkill -9 -f hello
sudo apt-get install -y libfcgi-dev spawn-fcgi nginx curl firefox

#compile (fcgi enabled) hello.cpp and create binary named hello
g++ /vagrant/initSDN/web292/fcgi2/hello.cpp -lfcgi++ -lfcgi -o hello_exec


#start nginx with configuration from nginx.cfg
sudo nginx -c /vagrant/initSDN/web292/fcgi2/nginx.cfg


# spawn the hello server on port 8000 with no fork
sudo spawn-fcgi -p 8001 -a 127.0.0.1 -n hello_exec &
sudo spawn-fcgi -p 8002 -a 127.0.0.1 -n hello_exec &




# Open web browser and access the hello server
firefox localhost:80/hello_lb


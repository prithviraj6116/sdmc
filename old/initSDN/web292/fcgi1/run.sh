#!/usr/bin/env bash

sudo pkill -9 -f nginx
sudo pkill -9 -f hello
sudo apt-get install -y libfcgi-dev spawn-fcgi nginx curl firefox

#compile (fcgi enabled) hello.cpp and create binary named hello_exec
g++ /vagrant/initSDN/web292/fcgi1/hello.cpp -lfcgi++ -lfcgi -o hello_exec


#start nginx with configuration from nginx.cfg
sudo nginx -c /vagrant/initSDN/web292/fcgi1/nginx.cfg

# spawn the hello_exec server on port 8000 with no fork
sudo spawn-fcgi -p 8000 -n hello_exec &

# Open web browser and access the hello_exec server via hello_loc from nginx.cfg
firefox localhost:80/hello_loc


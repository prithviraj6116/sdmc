from fabric.api import *

env.hosts=["192.168.33.4"]
env.user="vagrant"

def hostname_check():    # hostname_check is just a task name, replace with anything
    run("hostname&>log1fab")

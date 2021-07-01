import requests
import json
import time

def post(url, **kwargs):
    print('Sending POST')
    print(url)
    print(kwargs)        
    r = requests.post(url, **kwargs)
    return r

def get(url, **kwargs):
    print('Sending GET')
    print(url)
    print(kwargs)
    r = requests.get(url, **kwargs)
    return r

url_local = 'http://localhost:8080/cc-server'
url_local_docker = 'http://localhost:8081/cc-server'
url_aws = 'http://ec2-52-15-179-168.us-east-2.compute.amazonaws.com:8080/cc-server'
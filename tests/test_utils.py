import requests
import json
import time
import base64
from types import SimpleNamespace

def post(url, **kwargs):
    print('POST '+url)
    print(kwargs)        
    r = requests.post(url, **kwargs)
    return r

def get(url, **kwargs):
    print('GET '+url)
    print(kwargs)
    r = requests.get(url, **kwargs)
    return r

def delete(url, **kwargs):
    print('DELETE '+url)
    print(kwargs)
    r = requests.delete(url, **kwargs)
    return r

def b64_string(s):
    b = s.encode()
    b64b = base64.b64encode(b)
    b64s = b64b.decode()
    return b64s

class PathBuilder(object):
    root = SimpleNamespace(
        local='http://localhost:8080/'
    )
    _partner = 'partner/'
    _couple = 'couple/'
    me = _partner+'me/'
    you = _partner+'you/'
    us = _couple+'us/'
    login = 'login'

    def __init__(self):
        pass
    
    def couple(id):
        return self._couple+str(id)+'/'

def make_auth_header_dict (token):
    token_bytes = str.encode(token)
    enc_bytes = base64.b64encode(token_bytes)
    enc_str = enc_bytes.decode()
    return {'Authorization': 'Basic '+token}


class Api(object):
    def __init__(self, root_url, username, password):
        self._root_url = root_url
        self._username = username
        self._password = password
        login_url = self._root_url+PathBuilder.login
        r = requests.get(login_url, params={'emailAddress':self._username,'password':self._password})
        if r.status_code != 200:
            print('login failed')
            print(r.status_code)
            print(r.text)
        t = r.text
        d = json.loads(t)
        self._auth_token = d['token']
        self._pid = d['partnerId']
        self._cid = d['coupleId']
        self._auth_header = {'Authorization': 'cctoken '+b64_string(self._auth_token)}

    def get_partner_id():
        return self._pid

    def get_couple_id():
        return self._cid
    
    def post(self, path, **kwargs):
        url = self._root_url + '/' + path
        try:
            kwargs['headers'].update(self._auth_header)
        except KeyError:
            kwargs['headers'] = self._auth_header        
        print('POST '+url)
        print(kwargs)
        r = requests.post(url, **kwargs)
        return r

    def get(self, path, **kwargs):
        url = self._root_url  + '/' + path
        try:
            kwargs['headers'].update(self._auth_header)
        except KeyError:
            kwargs['headers'] = self._auth_header  
        print('GET '+url)
        print(kwargs)
        r = requests.get(url, **kwargs)
        return r
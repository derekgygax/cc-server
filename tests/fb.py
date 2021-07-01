import requests

app_id = '132046710832884'
app_secret = 'UaMNK717JrPPi1MY38Kdr3CLUQE'
access_token = app_id+'|'+app_secret
input_token = 'EAAB4GIVKZCvQBAGjHwVhScAKUAZB5URYxV8npdzNAZCHNUHE4KuZBEr1YqMueuY9ZB7WYbWrMkZC4gR91jFZAQJEhvZBoLXPdKI5HaEv2iBqCHlt6gZBLZAZCYRZB6wakf1BFZCJYDQz9fRIblLMdjwEx6sf8ZBeTgXhZA2KyTXs7kZCx4qRM4HpDEQwbqiImPgE85CP1hLkxt4J661BeP9qf7Q3QDWG2K1EPQJdpvuk5n7jaiz6lwZDZD'
host = 'https://graph.facebook.com/'
graph_version = 'v2.11'
host_wver = host+'/'+graph_version+'/'

#debug
debug_path = host+'/debug_token'
debug_params = {'input_token':input_token,
                'access_token':access_token}
r = requests.get(debug_path, params=debug_params)
print('debug')
print(r.status_code)
print(r.text)

#me
me_path = host_wver+'me'
scopes = ['birthday','email']
me_params = {'access_token':input_token,
             'fields':','.join(scopes)}
r = requests.get(me_path, params=me_params)
print(r.status_code)
print(r.text)
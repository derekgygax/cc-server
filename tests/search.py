import test_utils
import json
import random
from requests.auth import HTTPBasicAuth

url_base = test_utils.url
# username = 'LoriSimple123'
# password = 'LoriSimple123pw'

username = 'TeresaMoad123'
password = 'TeresaMoad123pw'

auth_tuple = (username, password)
search_params_url = url_base + '/search/parameters'
search_url = url_base + '/search'

r = test_utils.get(search_params_url)
print(r.status_code)
print(r.text)
 
params = {'age': '45,55','relationshipType':'Married,Engaged'}
r = test_utils.get(search_url, params=params, auth=auth_tuple)
print(r.url)
print(r.status_code)
print(r.text)
 
params = {'age': '30,35','numChildren':'2,5'}
r = test_utils.get(search_url, params=params, auth=auth_tuple)
print(r.url)
print(r.status_code)
print(r.text)
 
params = {'relationshipType': 'Married','race':'Black,White'}
r = test_utils.get(search_url, params=params, auth=auth_tuple)
print(r.url)
print(r.status_code)
print(r.text)

params = {'maxDistance': '4', 'relationshipType': 'Married',}
r = test_utils.get(search_url, params=params, auth=auth_tuple)
print(r.url)
print(r.status_code)
print(r.text)
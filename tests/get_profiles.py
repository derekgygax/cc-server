import test_utils
import json
from requests.auth import HTTPBasicAuth

url_base = test_utils.url
username = 'LoriSimple0'
password = 'LoriSimple123pw'
# username = 'fsamuels'
# password = 'fsamuelspw'
get_partner_url = url_base + '/partner/'
get_couple_url = url_base + '/couple/'

pid1 = '00000055'
pid2 = '00000056'
r = test_utils.get(get_partner_url+"me",auth=(username,password))
print(r.status_code)
print(r.text)
r = test_utils.get(get_partner_url+"you",auth=(username,password))
print(r.status_code)
print(r.text)
r = test_utils.get(get_partner_url+pid1,auth=(username,password))
print(r.status_code)
print(r.text)
r = test_utils.get(get_partner_url+pid2,auth=(username,password))
print(r.status_code)
print(r.text)

cid = '00000055-00000056'
r = test_utils.get(get_couple_url+"us",auth=(username,password))
print(r.status_code)
print(r.text)
r = test_utils.get(get_couple_url+cid,auth=(username,password))
print(r.status_code)
print(r.text)

username = 'TeresaMoad123'
password = 'TeresaMoad123pw'
cid_blocked = '0000002d-0000002e'
pid_blocked = '0000002d'
r = test_utils.get(get_partner_url+pid_blocked,auth=(username,password))
print(r.status_code)
print(r.text)
r = test_utils.get(get_couple_url+cid_blocked,auth=(username,password))
print(r.status_code)
print(r.text)
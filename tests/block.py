import test_utils
import json
from requests.auth import HTTPBasicAuth

url_base = test_utils.url
username = 'LoriSimple123'
password = 'LoriSimple123pw'
get_partner_url = url_base + '/block/'
bad_couple = '0000003b-0000003c'
block_couple_url = url_base + '/block/'

r = test_utils.post(block_couple_url+bad_couple,auth=(username,password))
print(r.status_code)
print(r.text)
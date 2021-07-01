import test_utils
import json
from requests.auth import HTTPBasicAuth

url_base = test_utils.url
username = 'LoriSimple123'
password = 'LoriSimple123pw'
# username = 'fsamuels'
# password = 'fsamuelspw'
partner_url = url_base + '/categorical/partner'
couple_url = url_base + '/categorical/couple'
r = test_utils.get(partner_url)
print(r.status_code)
print(r.text)
r = test_utils.get(couple_url)
print(r.status_code)
print(r.text)
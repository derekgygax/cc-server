import test_utils
import json
from requests.auth import HTTPBasicAuth

url_base = test_utils.url
active_url_base = url_base + '/couple-active/{}'
login_url = url_base + '/login'
username = 'LoriSimple0'
password = 'LoriSimple123pw'
auth_tuple = (username, password)

r = test_utils.get(login_url, auth=auth_tuple)
print(r.status_code)
print(r.text)

couple_id = json.loads(r.text)['coupleId']
active_url = active_url_base.format(couple_id)

r = test_utils.get(active_url)
print(r.status_code)
print(r.text)
active = json.loads(r.text)
new_active = not active

r = test_utils.post(active_url, data=json.dumps(new_active))
print(r.status_code)
print(r.text)

r = test_utils.get(active_url)
print(r.status_code)
print(r.text)
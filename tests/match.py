import test_utils
import json
import random
from requests.auth import HTTPBasicAuth

url_base = test_utils.url
username = 'ZitaWilson0'
password = 'ZitaWilson123pw'
auth_tuple = (username, password)
matches_url = url_base + '/matches'

r = test_utils.get(matches_url, auth=auth_tuple)
print(r.status_code)
print(r.text)

filtered_url = matches_url+'?approve=none&orderpriority=true&limit=3'
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)

filtered_url = matches_url+'?approve=none&orderpriority=true'
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)

print('Block the first couple')
recc_couples = r.json()
c1_id = recc_couples[0]['awayCoupleId']
print('Couple is {:}'.format(c1_id))
block_url_template = url_base + '/block/{:}'
r = test_utils.post(block_url_template.format(c1_id),auth=auth_tuple)
print(r.status_code)
print(r.text)
print('Block the second couple')
c1_id = recc_couples[1]['awayCoupleId']
print('Couple is {:}'.format(c1_id))
block_url_template = url_base + '/block/{:}'
r = test_utils.post(block_url_template.format(c1_id),auth=auth_tuple)
print(r.status_code)
print(r.text)
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)

filtered_url = matches_url+'?ismatch=true'
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)

filtered_url = matches_url+'?ismatch=false&approve=approve&partnerapprove=approve'
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)

filtered_url = matches_url+'?ismatch=false'
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)

filtered_url = matches_url+'?approve=approve&partnerapprove=none&partnerapprove=decline'
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)

filtered_url = matches_url+'?approve=none'
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)
 
filtered_url = matches_url+'?approve=none&approve=decline'
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)
 
filtered_url = matches_url+'?approve=approve&approve=decline'
r = test_utils.get(filtered_url,auth=auth_tuple)
print(r.status_code)
print(r.text)

def approve_couple(away_couple, approve):
    match_url = matches_url + '/' + away_couple
    r = test_utils.get(match_url, auth=auth_tuple)
    print(r.status_code)
    print(r.text)
    match_status = {'approve':approve}
    r = test_utils.post(match_url, data=json.dumps(match_status), auth=auth_tuple)
    print(r.status_code)
    print(r.text)
    
print('Approve resulting in couple approve but no match')
approve_couple('00000017-00000018','approve')
print('Approve resulting in match')
approve_couple('00000027-00000028','approve')
print('Approve creating new row plus')
approve_couple('00000075-00000076','approve')
print('Decline resulting in couple mixed')
approve_couple('0000001f-00000020','decline')
print('Decline resulting in couple decline')
approve_couple('0000000b-0000000c','decline')
print('Decline creating new row minus')
approve_couple('00000051-00000052','decline')
print('Approve to blocked couple')
approve_couple(c1_id,'approve')

import requests
import json
from requests.auth import HTTPBasicAuth
import test_utils

home_id = '3333-3562'
partner_id = '3333'
num_cache = 0

url_base = test_utils.url
p1 = {'username':'kim1','password':'kim1pw'};
p2 = {'username':'AmosSmith','password':'AmosSmithpw'};
away_ids = ['2277-2345', '3782-6262']
p1_approval = [False, True]
p2_approval = [False, False]
data = [{'awayCoupleId':'2277-2345','approve':False},
        {'awayCoupleId':'3782-6262','approve':True},
        {'awayCoupleId':'2277-2345','approve':False},
        {'awayCoupleId':'3782-6262','approve':False}]
approve_profile_url = '%s/approve-profile' %url_base
match_status_url = url_base+'/get-match-status' #?partnerId=%s&awayCoupleId=%s' \
for i, away_id in enumerate(away_ids):
    match_status_params = {'awayCoupleId':away_id}
    # Partner 1
    data = {'awayCoupleId':away_id,'approve':p1_approval[i]}
    r = test_utils.post(approve_profile_url,
                        data=json.dumps(data),
                        auth=(p1['username'],p1['password']))
#     print(r.status_code)
#     r = test_utils.get(match_status_url,
#                        params=match_status_params,
#                        auth=(p1['username'],p1['password']))
    print(r.status_code)
    print(r.text)
    # Partner 2
    data = {'awayCoupleId':away_id,'approve':p2_approval[i]}
    r = test_utils.post(approve_profile_url,
                        data=json.dumps(data),
                        auth=(p2['username'],p2['password']))
#     print(r.status_code)
#     r = test_utils.get(match_status_url,
#                        params=match_status_params,
#                        auth=(p2['username'],p2['password']))
    print(r.status_code)
    print(r.text)

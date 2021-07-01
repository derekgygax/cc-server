import requests
import json
import test_utils
from requests.auth import HTTPBasicAuth

url_base = test_utils.url
account_create_url = '%s/create-account' %url_base
update_partner_url = '%s/partner/me' %url_base
create_couple_url = '%s/create-couple' %url_base
update_couple_url = '%s/couple/us' %url_base
login_url = '%s/login' %url_base
p1 = {
      'basic':{
               'username':'fsamuels',
               'emailAddress':'frank.samuels@gmail.com',
               'password':'fsamuelspw',
               'firstName':'Frank',
               'lastName':'Samuels',
               },
      'full':{
              'firstName':'Frank',
              'middleName':'Jacob',
              'lastName':'Samuels',
              'gender':'M',
              'age':28,
              'address':'1234 main st',
              'city':'anytown',
              'state':'VA',
              'zipcode':'23456',
              'phoneNumber':'703-123-4567',
              'smoke':'n',
              'drink':'n',
              'politics':'liberal',
              'religion':'atheist',
              'gender':'Male',
              'race':'White',
              'timeInArea':'0-1 years',
              'incomeRange':'30,000-70,000'
             }
      }

p2 = {
      'basic':{
               'username':'jsamuels',
               'emailAddress':'jess.samuels@gmail.com',
               'password':'jsamuelspw'
               },
      'full':{
              'firstName':'Jess',
              'middleName':'Karen',
              'lastName':'Samuels',
              'gender':'F',
              'age':28,
              'address':'1234 main st',
              'city':'anytown',
              'state':'VA',
              'zipcode':'23456',
              'phoneNumber':'703-987-6543',
              'smoke':'y',
              'drink':'y',
              'politics':'conservative',
              'religion':'christian',
              'gender':'Female',
              'race':'White',
              'timeInArea':'Greater than 10 years',
              'incomeRange':'$125,000-$200,000'
              }
     }

couple_dict = {
               'relationshipType':'M',
               'timeTogether':4,
                'story':'She dropped something and our hands met.',
               'childrenAtHome':'Always',
               'youngestChild':'Toddler',
               'oldestChild':'School age',
               'numChildren': 2,
               'location':{'lat':38.8827754,'lon':-77.2090277},
               'maxDistance':10,
               }
######################################################################################

# Make p1
print('Create account 1')
r = test_utils.post(account_create_url,
                    data=json.dumps(p1['basic']))
print(r.status_code)
print(r.text)
p1['full']['partnerId'] = json.loads(r.text)['partnerId']
print(p1['full']['partnerId'])
print('Fill account 1')
r = test_utils.post(update_partner_url.replace('me',p1['full']['partnerId']),
                data=json.dumps(p1['full']),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)
# Make p2
print('Create account 2')
r = test_utils.post(account_create_url, data=json.dumps(p2['basic']))
print(r.status_code)
p2['full']['partnerId'] = json.loads(r.text)['partnerId']
print(p2['full']['partnerId'])
print('Fill account 2')
r = test_utils.post(update_partner_url,
                data=json.dumps(p2['full']),
                auth=(p2['basic']['username'], p2['basic']['password']))
print(r.status_code)
print(r.text)
print('Delete account 2 middleName')
del p2['full']['middleName']
r = test_utils.post(update_partner_url,
                data=json.dumps(p2['full']),
                auth=(p2['basic']['username'], p2['basic']['password']))
print(r.status_code)
print(r.text)

# p1 bad cr
print('p1 bad cr')
r = test_utils.post(create_couple_url,
                data=json.dumps({'partnerId':'00000001', 'action':'send'}),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)

# Send couple request from p1 to p2
print('p1 cr to p2')
r = test_utils.post(create_couple_url,
                data=json.dumps({'username':'jsamuels', 'action':'send'}),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)

# p1 gets couple requests
print('Get couple requests as p1')
r = test_utils.get(create_couple_url,
                   auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)

# p1 bad revoke
print('p1 revokes wrong')
r = test_utils.post(create_couple_url,
                data=json.dumps({'partnerId':'00000001', 'action':'revoke'}),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)

# p1 good revoke
print('p1 revokes right')
r = test_utils.post(create_couple_url,
                data=json.dumps({'username':'jsamuels', 'action':'revoke'}),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)

# Send couple request from p1 to p2
print('p1 cr to p2')
r = test_utils.post(create_couple_url,
                data=json.dumps({'username':'jsamuels', 'action':'send'}),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)

# p2 gets couple requests
print('Get couple requests as p2')
r = test_utils.get(create_couple_url,
                   auth=(p2['basic']['username'], p2['basic']['password']))
print(r.status_code)
print(r.text)

# p2 bad decline
print('p2 bad cr decline')
r = test_utils.post(create_couple_url,
                data=json.dumps({'partnerId':'00000001','action':'decline'}),
                auth=(p2['basic']['username'], p2['basic']['password']))
print(r.status_code)
print(r.text)

# p2 good decline
print('p2 good cr decline')
r = test_utils.post(create_couple_url,
                data=json.dumps({'username':'fsamuels','action':'decline'}),
                auth=(p2['basic']['username'], p2['basic']['password']))
print(r.status_code)
print(r.text)

# p2 send cr to p1
print('p2 cr to p1')
r = test_utils.post(create_couple_url,
                data=json.dumps({'username':'fsamuels', 'action':'send'}),
                auth=(p2['basic']['username'], p2['basic']['password']))
print(r.status_code)
print(r.text)

# p1 bad accept
print('p1 accepts wrong')
r = test_utils.post(create_couple_url,
                data=json.dumps({'partnerId':'00000001', 'action':'accept'}),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)

# p1 good accept
print('p1 accepts right')
r = test_utils.post(create_couple_url,
                data=json.dumps({'username':'jsamuels', 'action':'accept'}),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)

# Fill couple
print('Fill couple info')
r = test_utils.post(update_couple_url,
                data=json.dumps(couple_dict),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)
print('Delete relationshipType')
del couple_dict['relationshipType']
print('Fill couple info')
r = test_utils.post(update_couple_url,
                data=json.dumps(couple_dict),
                auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)

# Check
r = test_utils.get(update_couple_url,auth=(p1['basic']['username'], p1['basic']['password']))
print(r.status_code)
print(r.text)
exit()
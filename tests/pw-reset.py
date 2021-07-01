import requests
import json
import test_utils
from requests.auth import HTTPBasicAuth

url_base = test_utils.url
account_create_url = '%s/create-account' %url_base
update_partner_url = '%s/partner/me' %url_base
send_verify_url = '%s/send-verify-email' %url_base
verify_url = '%s/verify-email' %url_base
send_reset_url = '%s/send-resetpw-email' %url_base
reset_url = '%s/reset-password' %url_base
login_url = '%s/login' %url_base
change_pw_url = '%s/change-password' %url_base
p1 = {
      'basic':{
               'username':'kmoad',
               'emailAddress':'kyle.moad@gmail.com',
               'password':'kmoadpw',
               'firstName':'Kyle',
               'lastName':'Moad',
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
######################################################################################
auth_tuple = (p1['basic']['username'],p1['basic']['password'])
# Make p1
print('Create account')
r = test_utils.post(account_create_url,
                    data=json.dumps(p1['basic']))
print(r.status_code)
print(r.text)
partner_id = r.text

# Send verify email
r = test_utils.get(send_verify_url,
                    auth=auth_tuple
                    )
print(r.status_code)
print(r.text)

# Verify email
verify_token = input('Input verification token: ')
r = test_utils.post(verify_url,
                    params={'token':verify_token},
                    )
print(r.status_code)
print(r.text)

# Send reset email
send_reset_url
r = test_utils.get(send_reset_url,
                    data=json.dumps({'username': p1['basic']['username']}),
                    )
print(r.status_code)
print(r.text)

# Reset password
reset_token = input('Input reset token: ')
new_password = 'newpassword'
reset_data = {'token': reset_token,
              'password': new_password,
              }
r = test_utils.post(reset_url,
                    data=json.dumps(reset_data),
                    )
print(r.status_code)
print(r.text)

# Check login
new_auth_tuple = (auth_tuple[0], new_password)
r = test_utils.get(login_url,
                   auth=new_auth_tuple,
                  )
print(r.status_code)
print(r.text)

# Change password
final_password = 'finalpassword'
change_data = {
              'password': final_password,
              }
r = test_utils.post(change_pw_url,
                    data=json.dumps(change_data),
                    auth=new_auth_tuple
                    )
print(r.status_code)
print(r.text)

# Check login
final_auth_tuple = (auth_tuple[0], final_password)
r = test_utils.get(login_url,
                   auth=final_auth_tuple,
                  )
print(r.status_code)
print(r.text)
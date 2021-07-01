import test_utils

token = '00000001'
auth_header_dict = test_utils.make_auth_header_dict(token)
base_url = test_utils.url
partner_url = base_url+'/partner/me'
r = test_utils.get(partner_url, headers=auth_header_dict)
print(r.status_code)
print(r.text)
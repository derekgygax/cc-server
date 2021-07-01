import test_utils
import os
import json
import requests

url_base = test_utils.url
username = 'LoriSimple0'
password = 'LoriSimple123pw'
other_username = 'TeresaMoad0'
other_password = 'TeresaMoad123pw'

basedir = os.path.dirname(__file__)
us_url = url_base + '/couple/us'
couple_img_url = us_url + '/img'

all_files = {'0':open(os.path.join(basedir,'img','c0.jpg'),'rb'),
             '1':open(os.path.join(basedir,'img','c1.jpg'),'rb'),
             '2':open(os.path.join(basedir,'img','c2.jpg'),'rb'),
             '3':open(os.path.join(basedir,'img','c3.jpg'),'rb'),
             '4':open(os.path.join(basedir,'img','c4.jpg'),'rb'),
            }
r = test_utils.post(couple_img_url,auth=(username,password),files=all_files)
print(r.status_code)
print(r.text)
  
new_1 = {'1':open(os.path.join(basedir,'img','c0-new.jpg'),'rb')}
r = test_utils.post(couple_img_url,auth=(username,password),files=new_1)
print(r.status_code)
print(r.text)

# Delete third and fifth image
del_params = {'imgnums':'2,4'}
r = test_utils.delete(couple_img_url,auth=(username,password),params=del_params)
print(r.status_code)
print(r.text)
 
r = test_utils.get(us_url, auth=(username,password))
print(r.status_code)
print(r.text)
 
main_couple = json.loads(r.text)
main_cid = main_couple['coupleId']
 
main_couple_url = url_base +'/couple/'+main_cid
r = test_utils.get(main_couple_url, auth=(other_username,other_password))
print(r.status_code)
print(r.text)
 
no_img_couple_url = url_base +'/couple/00000003-00000004'
r = test_utils.get(no_img_couple_url, auth=(other_username,other_password))
print(r.status_code)
print(r.text)

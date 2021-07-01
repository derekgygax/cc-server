import pymysql, requests, json

dbconn = pymysql.connect(host='localhost',
                         port=5555,
                         user='root',
                         password='cc2017$',
                         database='Couples_Connection')
c = dbconn.cursor();
q = 'select ID, username from Partner;'
c.execute(q)
usernames = {x[0]:x[1] for x in c.fetchall()}

change_pw_url = 'http://localhost:8080/cc-server/change-password'
login_url = 'http://localhost:8080/cc-server/login'

for id, username in usernames.items():
    pw = username+'pw'
    data = {'partnerId':id,'password':pw}
    data_json = json.dumps(data)
#     r = requests.post(change_pw_url, data=data_json)
    r = requests.get(login_url,auth=(username,pw))
    print(r.status_code)
    print(r.text)
import pymysql
import test_utils

dbconn = pymysql.connect(host='localhost',
                         port=5555,
                         user='root',
                         password='cc2017$',
                         database='Couples_Connection')
matches_cursor = dbconn.cursor()
username_cursor = dbconn.cursor()
q = 'select home_couple_id, away_couple_id from Matches;'
matches_cursor.execute(q)
url_base = test_utils.url_local
for row in matches_cursor:
    home = row[0]
    away = row[1]
    p1 = home.split('-')[0]
    q = 'select username from Partner where ID="%s"' %p1
    username_cursor.execute(q)
    username = username_cursor.fetchone()[0]
    pw = username+'pw'
    url = url_base + '/update-match/%s' %away
    r = test_utils.post(url, auth=(username,pw))
    if r.status_code != 200:
        print(r.status_code)
        print(r.text)
        break
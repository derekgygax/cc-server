import pymysql
import test_utils

dbconn = pymysql.connect(host='localhost',
                         port=5555,
                         user='root',
                         password='cc2017$',
                         database='Couples_Connection',
                         )
cursor = dbconn.cursor()
all_couples_query = 'select ID from Couples;'
cursor.execute(all_couples_query)
all_cids = [x[0] for x in cursor]
main_username = 'KyleTrump123'
def get_password(username):
    return username+'pw'
main_pw = get_password(main_username)
r = test_utils.get(test_utils.url+'/couple/us',auth=(main_username, main_pw))
main_cid = r.json()['coupleId']
for unmatched_cid in all_cids:
    r = test_utils.get(test_utils.url+'/matches/'+unmatched_cid,auth=(main_username, main_pw))
    if r.status_code == 404:
        break
# existing_matches_query = 'select away_couple_id from Matches where home_couple_id="{}";'.format(main_cid)
# cursor.execute(existing_matches_query)
# matched_cids = [x[0] for x in cursor]
# unmatched_cid = list(set(all_cids) - set(matched_cids))[0]
r = test_utils.post(test_utils.url+'/matches/'+unmatched_cid,json={'approve':'approve'},auth=(main_username, main_pw))
away_username_query = 'select p.username from Couples as c join Partner as p on c.partner_ID_lower=p.ID where c.ID="{}";'.format(unmatched_cid)
cursor.execute(away_username_query)
away_username = cursor.fetchone()[0]
away_pw = get_password(away_username)
r = test_utils.get(test_utils.url+'/matches/'+main_cid,auth=(away_username, away_pw))
print(r.json())
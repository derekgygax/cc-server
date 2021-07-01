import traceback
import pymysql
import random
from numpy.random import choice
import datetime

def hex_string(digits):
    out = ''
    for i in range(digits):
        out += random.choice('0123456789abcdef')
    return out

def get_partner_approve(first_viewer = True):
    if first_viewer:
        weights = [0.4,0.4,0.2]
    else:
        weights = [0.3,0.5,0.2]
    return choice(['none', 'approve', 'decline'], p=weights)

def get_approve_code(p1,p2):
    if p1 == 'approve' and p2 == 'approve':
        return 'approve'
    elif p1 == 'approve' and p2 == 'decline':
        return 'mixed'
    elif p1 == 'decline' and p2 == 'approve':
        return 'mixed'
    elif p1 == 'none' and p2 == 'approve':
        return 'plus'
    elif p1 == 'none' and p2 == 'decline':
        return 'minus'
    elif p1 == 'approve' and p2 == 'none':
        return 'plus'
    elif p1 == 'decline' and p2 == 'none':
        return 'minus'
    elif p1 == 'decline' and p2 == 'decline':
        return 'decline'
    elif p1 == 'none' and p2 == 'none':
        return 'none'
    else:
        raise Exception('Unhandled approvals %s %s' %(p1,p2))

def make_mysql_boolean(b):
    if b == True:
        return '1'
    elif b == False:
        return '0'
    elif b == None:
        return 'null'
    else:
        raise Exception('Unhandled approval ' + a)

def make_mysql_string(s):
    if s == None:
        return 'null'
    else:
        return '"'+s+'"'
    
def get_connection_status(c1, c2):
    return c1 == 'approve' and c2 == 'approve'

def make_match_query(c1,c2):
    if random.choice(['p1','p2']) == 'p1':
        p1_approve = get_partner_approve(first_viewer=True)
        if p1_approve is 'none':
            p2_approve = get_partner_approve(first_viewer=True)
        else:
            p2_approve = get_partner_approve(first_viewer=False)
    else:
        p2_approve = get_partner_approve(first_viewer=True)
        if p2_approve is 'none':
            p1_approve = get_partner_approve(first_viewer=True)
        else:
            p1_approve = get_partner_approve(first_viewer=False)
    approve_code = get_approve_code(p1_approve, p2_approve)
    priority = random.random()
    type = 's'
    q = 'insert into Matches set '\
        +'home_couple_id=%s, ' %make_mysql_string(c1) \
        +'away_couple_id=%s, ' %make_mysql_string(c2) \
        +'p1_approve=%s, ' %make_mysql_string(p1_approve) \
        +'p2_approve=%s, ' %make_mysql_string(p2_approve) \
        +'couple_approve=%s, ' %make_mysql_string(approve_code) \
        +'type=%s;' %make_mysql_string(type)
    return q
    
total_matches =  2000   
dbconn = pymysql.connect(host='localhost',
                         port=5555,
                         user='root',
                         password='cc2017$',
                         database='Couples_Connection')
select_cursor = dbconn.cursor()
update_cursor = dbconn.cursor()
q = 'select ID from Couples;'
select_cursor.execute(q)
couples = [x[0] for x in select_cursor.fetchall()]
update_cursor.execute('truncate Matches;')
dbconn.commit()
inserted_matches = set([])
while len(inserted_matches) < total_matches:
    c1 = random.choice(couples)
    c2 = c1
    while c2 == c1:
        c2 = random.choice(couples)
    match_id = c1+'-'+c2
    print(match_id)
    if match_id in inserted_matches:
        continue
    match_q = make_match_query(c1,c2)
    update_cursor.execute(match_q)
    inserted_matches.add(match_id)
    if random.random() > 0.2:
        symmetric_q = make_match_query(c2,c1)
        symmetric_id = c2+'-'+c1
        if symmetric_id in inserted_matches:
            continue
        update_cursor.execute(symmetric_q)
        inserted_matches.add(symmetric_id)
dbconn.commit()

q = 'select m1.home_couple_id, m1.away_couple_id from Matches as m1 join Matches as m2 '\
    +'on m1.home_couple_id=m2.away_couple_id and m1.away_couple_id=m2.home_couple_id '\
    +'where m1.couple_approve="approve" and m2.couple_approve="approve";'
print(q)
select_cursor.execute(q)
num_matches = 0
for r in select_cursor:
    hc = r[0]
    ac = r[1]
    q = 'update Matches set is_match=1 where home_couple_id="%s" and away_couple_id="%s"' %(hc, ac)
    update_cursor.execute(q)
    num_matches += 1
print('num_matches = %d' %num_matches)
dbconn.commit()

update_cursor.close()
select_cursor.close()
dbconn.close()        
        
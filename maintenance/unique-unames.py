import pymysql
from collections import Counter

dbconn = pymysql.connect(host='localhost',
                         port=5555,
                         user='root',
                         password='cc2017$',
                         database='Couples_Connection')
cursor = dbconn.cursor()

main_query = 'select ID, first_name, last_name from Partner;'
cursor.execute(main_query)
rows = cursor.fetchall()
uname_counter = Counter()
for row in rows:
    pid = row[0]
    uname_base = row[1]+row[2]
    count = uname_counter[uname_base]
    uname = uname_base+str(count)
    email = uname+'@example.com'
    update_query = 'update Partner set username="{uname}", email_address="{email}" where ID="{id}";'\
        .format(uname=uname,
                email=email,
                id=pid,
                )
    cursor.execute(update_query)
    uname_counter[uname_base] += 1
dbconn.commit()
import pymysql
import random

dbconn = pymysql.connect(host='localhost',
                         port=5555,
                         user='ccuser',
                         password='ccpw',
                         database='Couples_Connection')
c = dbconn.cursor()
q = 'select ID from Couples;'
print(q)
c.execute(q)
couples = [x[0] for x in c.fetchall()]
q = 'select Couples_ID, Couples_ID_other from Connections;'
print(q)
c.execute(q)
connects = c.fetchall()
suggestions = []
while len(suggestions) <= 736:
    (home, away) = random.choices(couples,k=2)
    if (home, away) in connects or (away,home) in connects:
        continue
    if (home, away) in suggestions or (away,home) in suggestions:
        continue
    suggestions.append((home,away))
for home,away in suggestions:
    priority = random.random()
    q = 'insert into Suggested_Connections (home_couple, away_couple, priority) values ("%s","%s",%f);' \
            %(home, away, priority)
    print(q)
    c.execute(q)
dbconn.commit()
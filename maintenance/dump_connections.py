import pymysql

dbconn = pymysql.connect(host='localhost',
                         port=5555,
                         user='ccuser',
                         password='ccpw',
                         database='Couples_Connection')
c = dbconn.cursor()
q = 'select * from Connections;'
c.execute(q)
connections = c.fetchall()
wf = open('Connections.txt','w')
for conn in connections:
    wf.write('\t'.join(map(str,conn))+'\n')
    print('\t'.join(map(str,conn)))
    
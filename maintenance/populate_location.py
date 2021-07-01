import pymysql
import requests
import json
import random
import math

dbconn = pymysql.connect(host='18.216.134.146',
                         port=5555,
                         user='ccuser',
                         password='ccpw',
                         database='Couples_Connection')
cursor = dbconn.cursor()
q = 'select ID from Couples;'
cursor.execute(q)
ids = [x[0] for x in cursor.fetchall()]
center = [38.8938297,-77.1542682]
radius_km = 20
radius_deg = radius_km/111

def pm_adjust_rect(point, pm_dist):
    return [point[0] + (2*pm_dist*random.random()-pm_dist), point[1] + (2*pm_dist*random.random()-pm_dist)]

def pm_adjust_circ(point, pm_dist):
    t = 2*math.pi*random.random()
    u = random.random()+random.random()
    if u > 1:
        r = pm_dist*(2-u)
    else:
        r = pm_dist*u
    return [point[0]+r*math.cos(t), point[1]+r*math.sin(t)]

for id in ids:
    location = pm_adjust_circ(center,radius_deg)
    q = 'update Couples set location = ST_GeomFromText("POINT({lat:f} {lon:f})") where ID="{id}";'.format(lat=location[0],lon=location[1],id=id)
    print(q)
    cursor.execute(q)
cursor.close()
dbconn.commit()
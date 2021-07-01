import pymysql
import os
import pandas as pd
from collections import defaultdict
import random

dbconn = pymysql.connect(host='localhost',
                         port=5555,
                         user='root',
                         passwd='cc2017$',
                         database='Couples_Connection')
cursor = dbconn.cursor()

# Survey_Answers
sel_cursor = dbconn.cursor()
sel_cursor.execute('select ID from Partner;')
all_pids = [x[0] for x in sel_cursor]
sel_cursor.execute('select q.question_ID, c.score from Survey_Questions as q join Survey_Choices as c on q.question_ID=c.question_ID;')
q2scores = defaultdict(list)
for qid, score in sel_cursor:
    q2scores[qid].append(score)
sel_cursor.execute('select question_ID from Survey_Questions where required="y";')
required_qids = [x[0] for x in sel_cursor]

def answer_insert_stmt(pid, qid, scores):
    return 'insert into Survey_Answers (Partner_ID, question_ID, score) values ("{0}",{1},{2}) on duplicate key update score={2};'\
        .format(pid, qid, random.choice(scores))

for pid in all_pids:
    # Answer some questions
    qids = random.sample(q2scores.keys(),random.randint(10,len(q2scores)))
    for qid in qids:
        q = answer_insert_stmt(pid, qid, q2scores[qid])
        cursor.execute(q)
    # Most people answer all required questions
    if random.random() < 0.85:
        for qid in required_qids:
            q = answer_insert_stmt(pid, qid, q2scores[qid])
            cursor.execute(q)
dbconn.commit()
import test_utils
import json
import random
from requests.auth import HTTPBasicAuth

url_base = test_utils.url
# username = 'LoriSimple123'
# password = 'LoriSimple123pw'
# username = 'DerekKim123'
# password = 'DerekKim123pw'
username = 'ZitaObama0'
password = 'ZitaObama123pw'

overview_url = url_base + '/survey'
qa_url = url_base + '/survey/qa'

r = test_utils.get(overview_url, auth=(username,password))
print(r.status_code)
print(r.text)

overview = json.loads(r.text)
incomplete_groups = []
partial_group = None
for group in overview['groups']:
    if group['numQuestions'] != group['numAnswered']:
        incomplete_groups.append(group['id'])
        if group['numAnswered'] > 0:
            partial_group = group['id']
next_group = overview['nextGroup']

r = test_utils.get(qa_url+'?group=invalidGroup', auth=(username, password))
print(r.status_code)
print(r.text)

r = test_utils.get(qa_url, auth=(username,password))
print(r.status_code)
print(r.text)

r = test_utils.get(qa_url+'?group=%s' %','.join(incomplete_groups), auth=(username,password))
print(r.status_code)
print(r.text)
all_questions = json.loads(r.text)
answers = []
for q in all_questions[partial_group]:
    choices = q['choices']
    if len(choices) > 0:
        a = {
            'questionID':q['id'],
            'value':random.choice([x['value'] for x in choices]),
            'importance':random.choice([0,1,2])
            }
    answers.append(a)
r = test_utils.post(qa_url, data=json.dumps(answers), auth=(username, password))
print(r.status_code)
print(r.text)

r = test_utils.get(qa_url+'?group=%s' %partial_group, auth=(username, password))
print(r.status_code)
print(r.text)

new_answers = json.loads(r.text)
for q in new_answers[partial_group]:
    qid = q['id']
    cur_val = q['currentValue']
    cur_imp = q['currentImportance']
    for a in answers:
        if a['questionID'] == qid:
            print(cur_val, a['value'])
            print(cur_imp, a['importance'])
            answered_correctly = cur_val == a['value'] and cur_imp == a['importance']
            if not(answered_correctly):
                print('Question %s was answered incorrectly' %qid)
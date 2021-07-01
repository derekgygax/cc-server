from test_utils import Api, PathBuilder
import json

email = 'LoriSimple0@example.com'
password = 'LoriSimple123pw'
paths = PathBuilder()
api = Api(paths.root.local, email, password)
couple = api.get(paths.us).json()
print(couple['story'])
couple['story'] = 'new story'
api.post(paths.us, data=json.dumps(couple))
couple = api.get(paths.us).json()
print(couple['story'])
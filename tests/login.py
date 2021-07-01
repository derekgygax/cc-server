from test_utils import Api, Paths
import json

url_base = Paths.ROOT_LOCAL
email = 'LoriSimple0@example.com'
password = 'LoriSimple123pw'
api = Api(Paths.ROOT_LOCAL, email, password)
print(api._auth_token)
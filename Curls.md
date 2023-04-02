## Curls for postman/insomnia

___Note__: Remember there are insomnia-collection.json and insomnia-collection.har files in the insomnia-collection folder on the project._

### Create user
_This would return a JSESSIONID cookie that should be used on the other requests, (excepts for create user)_

```bash
curl --request POST \
  --url http://localhost:8080/user/create \
  --header 'Content-Type: application/json' \
  --data '{
	"username": "user",
	"password": "pass"
}'
```

### Login
_This would return a JSESSIONID cookie that should be used on the other requests, (excepts for create user)_

```bash
curl -v --request POST \
--url http://localhost:8080/login \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data username=user \
--data password=pass
```

This would return 200 OK if it's successful.

### File upload
_You should change the JSESSIONID cookie with the one that was returned on the login request,
and also change the file route with the real one_

```bash
curl -v --request POST \
  --url http://localhost:8080/file/upload \
  --header 'Content-Type: multipart/form-data' \
  --cookie JSESSIONID=8F371B69FC098E25440F67BF6BFFCEC4 \
  --form file=@/Users/user/Desktop/image.jpg \
  --form name=file.jpg
```
This would return a json like this:
```json
{
  "id": 1,
  "name": "file.jpg",
  "createDate": "2023-04-02T00:09:43.809+00:00"
}
```

### File share
_You should change the JSESSIONID cookie with the one that was returned on the login request,
also change the `fileId` and the `usernameToShare`_

```bash
curl --request POST \
  --url http://localhost:8080/file/share \
  --header 'Content-Type: application/json' \
  --cookie JSESSIONID=8F371B69FC098E25440F67BF6BFFCEC4 \
  --data '{
	"fileId": 1,
	"usernameToShare": "user2"
}'
```
This would return 200 OK if it's successful.

### Get all files
_You should change the JSESSIONID cookie with the one that was returned on the login request_

```bash
curl -v --request GET \
--url http://localhost:8080/file/get-all \
--cookie JSESSIONID=8F371B69FC098E25440F67BF6BFFCEC4
```
This would return a json like this:
```json
[
	{
		"fileId": 1,
		"name": "file.jpg",
		"createdDate": "2023-04-02T00:08:39.000+00:00",
		"owner": true
	}
]
```

### File download
_You should change the JSESSIONID cookie with the one that was returned on the login request,
also change the output where you want the file to be downloaded_

```bash
curl -v --request GET \
  --url http://localhost:8080/file/download/2 \
  --cookie JSESSIONID=8F371B69FC098E25440F67BF6BFFCEC4
	--output /Users/user/Desktop/file.jpg
```
This downloads the file on the given route

### File delete
_You should change the JSESSIONID cookie with the one that was returned on the login request,
also change the id of the file you want to delete_

```bash
curl --request DELETE \
  --url http://localhost:8080/file/delete/1 \
  --cookie JSESSIONID=8F371B69FC098E25440F67BF6BFFCEC4
```
This would return 200 OK if it's successful.

### File remove access
_You should change the JSESSIONID cookie with the one that was returned on the login request,
also change the `username` and the `fileId`_

```bash
curl --request POST \
  --url http://localhost:8080/file/remove-access \
  --header 'Content-Type: application/json' \
  --cookie JSESSIONID=8F371B69FC098E25440F67BF6BFFCEC4 \
  --data '{
	"username": "user2",
	"fileId": 2
}'
```
This would return 200 OK if it's successful.

### File rename
_You should change the JSESSIONID cookie with the one that was returned on the login request,
also change the `newName` and the `fileId`_

```bash
curl --request POST \
  --url http://localhost:8080/file/rename \
  --header 'Content-Type: application/json' \
  --cookie JSESSIONID=8F371B69FC098E25440F67BF6BFFCEC4 \
  --data '{
	"fileId": 2,
	"newName": "flor.jpg"
}'
```
This would return 200 OK if it's successful.

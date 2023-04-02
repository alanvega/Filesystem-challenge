# Filesystem-challenge

## Description
This is a filesystem API created on Java with Spring Boot and mysql.

### Assumptions
I assumed: 
- That only the owner of the file can give, remove permissions, delete a file and rename it.
- That the owner of a file can't remove his own permission.

### Things to improve
There are some things that I would improve for a real application:
- Create a user for the mysql (at least one for dev and one for the app)
- Session should be persisted on the database, so it doesn't get lost when the server restarts (now it is only stored on memory).
- The authentication validation should be on an interceptor, also it should save the userId on the MDC to be able to have it on the whole app.
- Some lock on the file or user table when a new permission is created or a file is being deleted (for that file or user only).
- Maybe files should be stored on an S3 instead of a mysql

Some improves on security:
- Use CSRF tokens
- The session cookie should have expiration time

## Run the project
You can run the project with Docker, just by running the following shell script (if you don't have Docker it will install it):
It should start on port 8080
```bash
./startup.sh
```
This script makes a `docker compose up` with the configuration on the `Dockerfile` and `docker-compose.yml` file.

To stop it just run the shutdown script:
```bash
./shutdown.sh
```

## Run the project without Docker
You can also manually run the java app just by starting your own mysql server on port `3306` with the configuration on the `application.properties` file,
then run the [script.sql](./script.sql) inside the mysql to create the needed tables,
run `mvn clean install` and for last run `mvn spring-boot:run` to start the application.
The application should start on port 8080.

_Note that this requires having jdk-17, and maven installed._

## Curls for postman/insomnia
You can consult them on the [Curls.md](Curls.md) file.

___Note__: There are insomnia-collection.json and insomnia-collection.har files in the insomnia-collection folder on the project._


# Superhero Spring Boot RESTful API
A RESTful API etude, using Spring 6.* , Spring Boot 3.* to find solutions and build tests as example code - to store contacts of superheroes.

🚀 Start with `bin/start.sh` (more information below)

You could get data calling e.g. `/api/v1/superheroes` and other endpoints. Prior get a JWT token. You can run unit, functional and integrative tests using oci container solution ("Testcontainers" feature of spring boot). 

![Swagger UI](src/main/resources/static/img/swg.png)

## Changelog 📜

### Features 0.8.* ✨

* JWT auth, user roles
* Tests (using testcontainers, liquibase), > 90%
* Database postgres + docker
* Pagination on listing
* Soft delete for superhero and user, cleaning database scheduler (to be discussed)

### Features 0.9.* ✨
* Refresh token

### New for 0.10.* ✨
* Deployment for static websites inside of Springs Webserver, SPA

## Start project in your docker environment 🚀

👆 You will need 🐋 docker/ docker compose V2 installed on your OS.

Start with `bin/start.sh`


This will generate keys, make an image of Java project,  initialize a postgres database as docker container and provide localhost, TLS at port 8443.

### Go on using 

* Use some rest client: `GET https://localhost:8443/api/v1/superheroes/ HTTP/1.1` (Please use JWT).

* See OpenAPI definition at https://localhost:8443/swagger-ui/index.html . (Please write `index.html`, there is no redirect)

* See a chart of superhero skills (id: 1 - 16) https://localhost:8443/chart.html?id=1

## Using JWT <img src="./src/main/resources/static/img/jwt_logo.svg" width="20">

1. Get a token by authentication

        GET https://localhost:8443/api/v1/login HTTP/1.1
        Authorization: Basic admin@example.com foobar

2. You will receive an authorization as JWT in payload of response body. 

3. Use JWT 

        GET https://localhost:8443/api/v1/superheroes HTTP/1.1
        Authorization: Bearer {{your-jwt-token}}

* Administrator role: 
  * user: admin@example.com
  * password: foobar
* User role: 
  * user: chris@example.com
  * password: foobar

## Developing (using hot swapping of code) 🔧

👆 You will need docker/ docker compose V2 installed on your OS and Java 21.0.2

* Start application: `./bin/dev/start.sh`
  - Postgres database will be started and fully initialized in a docker container.
  - Spring-boot will be started.

* Run test by `mvn clean test`.

* Open database client on `psql postgresql://db:db@localhost:15432/db`.

## Usind docker deployment

Start `bin/oci/start.sh`

## Endpoints

### Authentication
* GET /api/v1/login

### Superheroes
* GET /api/v1/superheroes
* GET /api/v1/superheroes/{id}
* POST /api/v1/superheroes
* PUT /api/v1/superheroes/{id}
* DELETE /api/v1/superheroes/{id} 

### Skills
* GET /api/v1/skills
* GET /api/v1/skills/{id}

### Skill profile
* GET /api/v1/superheroes/{superheroId}/skillprofiles
* GET /api/v1/superheroes/{superheroId}/skillprofiles/{skillId}
* POST /api/v1/superheroes/{superheroId}/skillprofiles
* PUT /api/v1/superheroes/{superheroId}/skillprofiles/{skillId}
* DELETE /api/v1/superheroes/{superheroId}/skillprofiles/{skillId}

## Plans for 0.11.0 ⏳

* End to end test API against Swagger doc
* Optimize JWT claim to better store user roles
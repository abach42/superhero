# 🦸‍♂️ Superhero Spring Boot RESTful API
A RESTful API etude, using Spring 6.* , Spring Boot 3.* to find solutions and build tests as example 
code and to store contacts of your superheroes.

🚀 Start with `bin/start.sh` (see below)

You could get data calling e.g. `/api/v1/superheroes` and other endpoints. Prior get a JWT token. 
You can run unit, functional and integrative tests using oci container solution ("Testcontainers" 
feature of spring boot). 

![Swagger UI](src/main/resources/static/img/swg.png)

## 📜 Changelog

### Features 

* JWT auth, user roles
* Refresh token
* Tests (using testcontainers, liquibase)
* Database postgres + docker
* Pagination on listing
* Soft delete for superhero and user, cleaning database scheduler (to be discussed)
* Deployment for static websites inside Springs Webserver, SPA
* Optimized method security and project structure

### ✨ New for 1.1.0 
* AI search for similar skills and team recommendations

## 🚀 Start project in your docker environment

👆 You will need 🐋 docker/ docker compose V2 installed on your OS.

Start with `bin/start.sh`

This will generate keys, make an image of Java project, start it in a docker container, 
initialize a postgres database as container and provide localhost, TLS at port 8443.

(Java will be installed inside the container, you do not need it in your OS).

### Go on using 

* Use some rest client: `GET https://localhost:8443/api/v1/superheroes/ HTTP/1.1` (Please use JWT).

* See OpenAPI definition at https://localhost:8443/swagger-ui/index.html. (Please write `index.html`, there is no redirect)

* See a chart of superhero skills (id: 1 - 16) https://localhost:8443/chart.html?id=1

* For AI just run command of `update-semantic-data` in spring shell.

## <img src="./src/main/resources/static/img/jwt_logo.svg" width="20"> Using JWT

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

## 🔧 Developing (using hot swapping of code)

👆 You will need docker/ docker compose V2 installed on your OS and Java 17

* Start application: `./bin/dev/start.sh`
  - Postgres database will be started and fully initialized in a docker container.
  - Spring-boot will be started.

* Run test by `bin/test.sh`.

* Open database client on `psql postgresql://db:db@localhost:25432/db`.

## Endpoints

### Authentication
* GET /api/v1/auth/login
* GET /api/v1/auth/refresh-token

### Superheroes
* GET /api/v1/superheroes
* GET /api/v1/superheroes/{id}
* POST /api/v1/superheroes
* PATCH /api/v1/superheroes/{id}
* DELETE /api/v1/superheroes/{id} 

### Skills
* GET /api/v1/skills
* GET /api/v1/skills/{id}

### Skill profile
* GET /api/v1/superheroes/{superheroId}/skill-profiles
* GET /api/v1/superheroes/{superheroId}/skill-profiles/{skillId}
* POST /api/v1/superheroes/{superheroId}/skill-profiles
* PATCH /api/v1/superheroes/{superheroId}/skill-profiles/{skillId}
* DELETE /api/v1/superheroes/{superheroId}/skill-profiles/{skillId}

## ⏳ Plans for 1.1.0 

* End to end test API against Swagger doc
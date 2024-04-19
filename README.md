# superhero
An RESTful API etude.

tbd: what does it do?

## Start project in your OCI of docker environment

🐋 tbd.

## Developing

1. 🐋 Start database: `docker-compose up` 

        Postgresql database will be started an initialized in a docker container. 

2. 🚀 Start application: `mvn spring:boot-run` (server.port: 8888)

3. 🍽️ Use rest client: GET {{domain}}/api/superheros/

4. 💡See openapi definition at {{domain}}/swagger-ui.html

5. 🤖 Run test by `mvn clean test` 

6. 🥫Open database client on `psql postgresql://db:db@localhost:15432/db`

## Using JWT

tbd. 

## Cleaning data

Enter container or development console, wait for shell:> and type `manually-erase-marked-as-deleted` (Manually start to erase records which are marked as deleted.) to delete records, which are marked as `deleted` on DELETE api command. 
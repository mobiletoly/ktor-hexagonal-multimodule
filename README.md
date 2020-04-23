# Hexagonal architecture for AddressBook

Backend API application built using hexagonal architecture and implemented with Kotlin's ktor.

# What tools/frameworks do we use?

- **gradle** - our build system of choice
- **kotlin 1.3** - our language of choice
- **ktor** for creating web application: https://github.com/ktorio/ktor
- **ExposedSQL** to access database: https://github.com/JetBrains/Exposed
- **HikariCP** for high-performance JDBC connection pool: https://github.com/brettwooldridge/HikariCP
- **Koin** for dependency injection: https://insert-koin.io/ 
- **PostgreSQL** for database: https://www.postgresql.org/
- **kotlin-logging** for logging: https://github.com/MicroUtils/kotlin-logging
- **HOCON** for application configuration: https://github.com/lightbend/config/
- **jackson** for JSON serialization/deserialization: https://github.com/FasterXML/jacksons

and for testing:
- **Testcontainers** for testing with a real database in Docker: https://github.com/testcontainers/testcontainers-java
- **junit** + **spek2** for writing tests: https://github.com/spekframework/spek/

and some other misc stuff:
- **ktlint** for Kotlin checkstyle
- **jacoco** for code coverage metrics

# Setup

## Prerequisites

#### Docker

Docker is not required to run this code locally, but makes setup a little easier. We recommend installing it.

#### PostgreSQL

If you have a local PostgreSQL running on your computer - you are all set.
If you don't have PostgreSQL installed, you can do it now by installing from:
https://www.postgresql.org/download/
and set it up with user name and password that can later be used to configure.

Another option is to create a docker image with PostgreSQL database running: 

```bash
$ docker run --name localpostgres -d -p 5432:5432 -e POSTGRES_PASSWORD=postgresspass postgres:alpine
```

This will create and run a local instance of PostgreSQL database with user name "postgres" and password "postgresspass".

Or use

```bash
$ docker start localpostgres
```

if localpostgres container was already created. 

Make sure to create **addrbook** database in your PostgreSQL instance.

## Run application locally

#### Run application with gradle

Application uses HOCON configuration files and some parameters rely on environment variable, so you need to setup them
first:

APP_DEPLOYMENT_ENV=local;APP_DB_USERNAME=postgres;APP_DB_PASSWORD=postgresspass;APP_DB_URI=jdbc:postgresql://localhost:5432/addrbook;APP_VERSION=0.0;APP_BUILD_NUMBER=0

- Deployment target. Application can have multiple configurations (local, dev, sit, prod etc) and therefore
multiple resource files, such as `adapters/src/main/resources/config-local.conf` (`config-prod.conf` etc) are available.
Depending on a target specified in APP_DEPLOYMENT_ENV environment variable - different configuration files
will be selected

        $ export APP_DEPLOYMENT_ENV=local

- Application version:

        $ export APP_VERSION=0.1

- Application build number:

        $ export APP_BUILD_NUMBER=1

- PostgreSQL database username, password and connection URI:

        $ export APP_DB_USERNAME=postgres
        $ export APP_DB_PASSWORD=postgresspass
        $ export APP_DB_URI=jdbc:postgresql://localhost:5432/addrbook
        
Next step is to run application via gradle:

        ./gradlew build :app:run --args='-config=app/src/main/resources/application-dev.conf'

If you don't specify --args argument, then by default application.conf will be loaded. Both files are very similar,
but application-dev.conf can contain some settings helpful during a development phase (e.g. auto-reload support).

Application exposes HTTP 8080 port for it's API.

#### Run application with IntelliJ

Import addrbook-hexagonal-ktor project into your IntelliJ IDE. Choose **Run / Edit Configuration** menu to create new
launch configuration. On the left side click **[+]** and select **Application**. Name it **API Server** (or pick other name)
and fill up some essential fields:

- Main class: `io.ktor.server.netty.EngineMain`
- VM options: `-Dkotlinx.coroutines.debug`
- Program arguments: `-config=app/src/main/resources/application-dev.conf`
- Environment variables: `APP_DEPLOYMENT_ENV=local;APP_DB_USERNAME=postgres;APP_DB_PASSWORD=postgresspass;APP_DB_URI=jdbc:postgresql://localhost:5432/addrbook;APP_VERSION=0.1;APP_BUILD_NUMBER=1`
- Use classpath or module: `addrbook-hexagon-ktor.app.main`

You should be able to run/debug your app now.

#### Run with Docker locally

Build an application first:

        $ ./gradlew build

Then upload and tag it in docker:

        $ docker build -t addrbook-api-server . 

Now you are ready to lunch it:
```
$ docker run -it \ 
    -e APP_DEPLOYMENT_ENV=local \
    -e APP_VERSION=0.1 \
    -e APP_BUILD_NUMBER=1 \
    -e APP_DB_USERNAME=postgres \
    -e APP_DB_PASSWORD=postgresspass \
    -e APP_DB_URI=jdbc:postgresql://your-local-ip-address:5432/addrbook \
    -p 8080:8080
    --rm addrbook-api-server
```

Make sure to replace *your-local-ip-address* in APP_DB_URI in command above to an actual IP address of your machine
that you can find with **ifconfig** or **ipconfig** shell commands (you cannot use *localhost* anymore, because localhost
inside AddressBook application docker container will be pointing to that container instead of your host machine).

## Generate coverage report

To generate a coverage report you must run:

    $ ./gradlew clean jacocoFullReport
    
It will run unit tests and generate coverage report in HTML format into root project's directory: `./build/reports/jacoco/jacocoFullReport/html`

# Hexagonal architecture for Address Book application

Backend API service built using hexagonal architecture and implemented with Kotlin's ktor.
This code may seem like an overkill for a simple application such as Address Book, but treat
it as a foundation for much larger services.

# Overview

## What tools/frameworks do we use?

- **gradle** - our build system of choice (using Kotlin DSL)
- **kotlin** - our language of choice
- **ktor** for creating web application: https://github.com/ktorio/ktor
- **ExposedSQL** to access database: https://github.com/JetBrains/Exposed
- **HikariCP** for high-performance JDBC connection pool: https://github.com/brettwooldridge/HikariCP
- **Koin** for dependency injection: https://insert-koin.io/
- **PostgreSQL** for database: https://www.postgresql.org/
- **HOCON** for application configuration: https://github.com/lightbend/config/

and for testing:
- **Testcontainers** for testing with a real database in Docker: https://github.com/testcontainers/testcontainers-java
- **kotest** for writing tests: https://kotest.io

and some other misc stuff:
- **ktlint** for Kotlin checkstyle
- **kover** for code coverage metrics


## Goal

Simple Address Book web service written according to Hexagonal architectural patterns to add/delete/fetch
address book items. It demonstrates database access as well as using external REST service to assist in generating
random address book items. You can find Postman collection to access this web service in `integration/postman`
directory.

## Hexagonal architecture overview

Project uses Hexagonal (or "ports and adapters") architecture. There are a lot of good materials about
hexagonal architecture, so please google it, it is really cool and understanding of it is needed to understand
this project's layout.

In this project we separate functionality into few modules - **adapters** (with submodules), **core**, **infra**,
with only few modules having dependencies on other modules.

Here is a brief overview of each module:

- **core** - contains business logic (services that implement business use cases, definition of ports for adapters,
business models).
- **adapters** - provides platform/framework specific functionality (e.g. your database repository classes
go here)
- **infra** - application configuration and launcher
- **common** - light-weight miscellaneous code that can be useful for other modules

Now let's take a look at each module in more details

#### Core module

This is a module containing a business logic of our application. The main idea is that business logic should
know nothing about frameworks used to implement an application, it should have zero knowledge about database
used, database ORM technology used, HTTP client used or what is Kafka. In our application Core module contains
multiple services, such as `AddressBookService`, `RandomPersonService` etc implementing our core business logic:
add, update and delete Address Book entries.

###### Depends on
- It has no dependencies on other modules.

###### What should be in Core
- Service code to provide business logic functionality.
- Business data models
- Definitions of output ports
- Definitions of input ports (a.k.a. use cases).

###### What should not be in Core
- Frameworks (less are better)
- Database, network clients (no SQL Exposed calls, no Kafka calls, no SQS call etc)
- No transport logic (e.g. no JSON parsing, no JSON annotations etc). It might be tempting to use JSON objects
received from web service controllers, but don't do this, use core models to describe your business logic.
It will require extra work and some extra effort to write data structures that might look very similar,
but when you decide to change your JSON payload and your business logic will remain the same - it will pay off.

###### What is ports

- Ports is a bridge between Adapters and Core modules. Core declare interfaces it requires from Adapters and put
them in `outport` package. Then Adapters provides implementations (injected via Dependency Injection, we use
Koin framework in our application) for these port interfaces. These ports are called **output ports** and 
adapters implementing output ports are called **secondary adapters** (also known as **driven adapters**).
- Secondary adapters should never call business logic directly, it can only return data (models, exceptions etc)
in response for core logic calls.

There is a concept called **primary adapters** (also known as **driver adapters**). In our code we have only
one primary adapter called `primary-web`. Unlike secondary adapters, that can only respond to core logic calls,
primary adapters are the ones that initiate calls to core logic via **input ports**. In our code we call input
ports - **use cases**. Core logic code is responsible to provide service classes that implement use case interfaces.
In our code our primary adapter is ktor application that provides REST handlers. REST handler calls use case
interface that is implemented in Core module and Core module might decide to call secondary adapter via output
port to perform framework-specific operation, for example to add data into database.


#### Adapter modules

Platform-specific code. Don't put any of your business logic here. These modules implement platform specific
functionality, such as database access or HTTP calls. As we already mentioned, the special case of adapters is
*primary adapters* that contain functionality that trigger business logic via interfaces in core's `usecase`
interfaces, in our case this is ktor's web controllers (routes).

Adapters usually deal with repository classes (SQL repo, HTTP client repo etc) and can work with DTO
(data transfer objects) to communicate with repositories. These DTO objects should not be exposed to core's
business logic, since if they change - it should not impact business logic. If adapter wants to return data
back to core business logic, this DTO object should be converted to business model data class first. Same is
for input, core module will be sending model data classes as input parameter to adapter (via output port)
and adapter might need to convert these models to DTO object first.

###### Depends on
- **core** module. Primary adapters should only use `usecase` package and model data classes used by use cases
(these model classes may reside outside of `usecase` classes, e.g. in our app we use `error` and `models` packages
as well). Secondary adapters should only use `outport` package (and model data classes).

###### What should be in adapters
- Web Service controllers (in our app it is **ktor** routes)
- HTTP/REST clients
- Database repositories to provide access to underlying database.
- For example Kafka or ActiveMQ code to send or receive data.
- DAO objects. Yes, DAO objects should not be in Ports or Core modules. First of all, they can contain
  framework-specific code or annotations (e.g. JPA annotations). Second, you don't want to have your business
  entities to reflect database table layout.
- DTO (Data Transfer Object) entities, usually specific to repository.

###### What should not be in adapters
- Avoid any business logic code


#### Infra (infrastructure) module

Application launcher. Should be a very simple code that performs dependency injection of classes from other
modules. Launches primary adapter.

###### Depends on
All other modules.


#### Common module

**shared** module should not be used too often. It may contain some utility functionality, for example in our app we
have some logger utility functions in the common module, because logging required in both **adapter** and **core**
modules.

###### Depends on
No module dependencies


### Workflow example

```
    [1 REST controller] --> [2 Use Case]:[Service] --> [3 Output Port][Adapter] --> [4 Repository]
```

1. User performs HTTP POST request to `/persons` to add new person. This request is handled by
ktor's route implemented in `adapters.primaryweb.routes.SaveAddressBookEntryRoute` class of
primary adapter module `primary-web`. POST body request is represented
by `adapters.primaryweb.gen.models.RestSavePersonRequest` data class (generate from OpenAPI spec).
REST request model is transformed into `core.models.PersonEntry` business model class defined
in core's module. Then primary adapter's asks core module to create new person via
`core.usecase.AddPersonUsecase` interface (this interface is supplied via dependency injection
mechanism).<br><br>

2. Core module's code in `core.services.AddPersonService` class implements `AddPersonUsecase`
and therefore responsible to performs business logic related to validating and storing `PersonEntry`
object it has received from REST controller. At some point of time business logic requires storing
of new entry in persistent storage. The communication with persistence storage should be performed
via output ports and for this specific use case the perfect match will be `core.outport.AddPersonPort`
port interface.<br><br> 

3. `persis` adapter's code in `adapters.persist.addressbook.SavePersonAdapter` class
implements `AddPersonPort` interface and therefore responsible to handle persistence logic
needed by core module. First it performs validations required, converts business entity
`PersonEntry` into two data classes - `PersonSqlEntity` and `PostalAddressSqlEntity` and
pass these two objects two database repository to save.<br><br> 

4. Repository uses ExposedSQL framework to store `PersonSqlEntity` and `PostalAddressSqlEntity`
entities into database.

Once this operation is performed, repository will return new SQL entities (with updated ids),
where they will be converted by adapter back into `PersonEntry` and returned to core's service.
Core service will return it back to REST controller where this entry will be converted to
REST response `adapters.primaryweb.gen.models.RestPersonResponse`.


# Setup

## Prerequisites

#### Docker

Docker not required to run this code locally, but makes setup a little easier. We recommend installing it.

#### PostgreSQL

If you have a local PostgreSQL running on your computer - you are all set.
If you don't have PostgreSQL installed, you can do it now by installing from:
https://www.postgresql.org/download/
and set it up with user name and password that can later be used to configure.

Another option is to create a docker image with PostgreSQL database running:

```bash
$ docker run --name localpostgres -d -p 5432:5432 -e POSTGRES_PASSWORD=postgrespass postgres:alpine
```

This will create and run a local instance of PostgreSQL database with user name "postgres" and password "postgrespass".

Or use

```bash
$ docker start localpostgres
```

if localpostgres container was already created.

Make sure to create **addrbook** database in your PostgreSQL instance.

## Run application locally

#### Run application with gradle

Application uses HOCON configuration files and some parameters rely on environment variable,
so you need to set them up first:

APP_DEPLOYMENT_ENV=local;APP_DB_USERNAME=postgres;APP_DB_PASSWORD=postgrespass;APP_DB_URI=jdbc:postgresql://localhost:5432/addrbook;APP_VERSION=0.0;APP_BUILD_NUMBER=0

- Deployment target. Application can have multiple configurations (local, dev, sit, prod etc) and therefore
  multiple resource files, such as `infra/src/main/resources/config-local.conf` (`config-prod.conf` etc) are available.
  Depending on a target specified in APP_DEPLOYMENT_ENV environment variable - different configuration files
  will be selected

        $ export APP_DEPLOYMENT_ENV=local

- Application version:

        $ export APP_VERSION=0.1

- Application build number:

        $ export APP_BUILD_NUMBER=1

- PostgreSQL database username, password and connection URI:

        $ export APP_DB_USERNAME=postgres
        $ export APP_DB_PASSWORD=postgrespass
        $ export APP_DB_URI=jdbc:postgresql://localhost:5432/addrbook

Next step is to run application via gradle:

        ./gradlew build :app:infra:run --args='-config=src/main/resources/application-dev.conf'

If you don't specify --args argument, then by default application.conf will be loaded. Both files are very similar,
but application-dev.conf could contain some settings helpful during a development phase (e.g. auto-reload support).

Application exposes HTTP 8080 port for its API.

#### Run application with IntelliJ

Import ktor-hexagonal-multimodule project into your IntelliJ IDE. Choose **Run / Edit Configuration** menu to create new
launch configuration. On the left side click **[+]** and select **Application**. Name it **API Server** (or pick other name)
and fill up some essential fields:

- Module: ktor-hexagonal-multimodule.app.infra.main
- Main class: `io.ktor.server.netty.EngineMain`
- Program arguments: `-config=app/infra/src/main/resources/application-dev.conf`
- Environment variables: `APP_DEPLOYMENT_ENV=local;APP_DB_USERNAME=postgres;APP_DB_PASSWORD=postgrespass;APP_DB_URI=jdbc:postgresql://localhost:5432/addrbook;APP_VERSION=0.1;APP_BUILD_NUMBER=1`

You should be able to run/debug your app now.

#### Run with Docker locally

Build an application first:

        $ ./gradlew build shadowJar

This step will build fat jar at `app/infra/build/libs/infra-all.jar`

Then upload and tag it in docker:

        $ docker build -t addrbook-api-server . 

Now you are ready to lunch it:
```
$ docker run -it \ 
    -e APP_DEPLOYMENT_ENV=local \
    -e APP_VERSION=0.1 \
    -e APP_BUILD_NUMBER=1 \
    -e APP_DB_USERNAME=postgres \
    -e APP_DB_PASSWORD=postgrespass \
    -e APP_DB_URI=jdbc:postgresql://your-local-ip-address:5432/addrbook \
    -p 8080:8080 \
    --rm addrbook-api-server
```

Make sure to replace *your-local-ip-address* in APP_DB_URI in command above to an actual IP address of your machine
that you can find with **ifconfig** or **ipconfig** shell commands (you cannot use *localhost* anymore, because localhost
inside AddressBook application docker container will be pointing to that container instead of your host machine).

## Unit tests

Unit tests automatically run when you perform a build. All tests are light-weight and use mocks, with the noticeable
exception of `app/adapters/persist` module. Here we use Test Containers to spin up docker image with PostgreSQL,
so tests for adapters are run with a real PostgreSQL database, no database mocks are used. This will allow us to
be more confident in our code that interacts with database.

## Generate coverage report

To generate a coverage report in HTML format you must run:

    $ ./gradlew build koverMergedHtmlReport

It will run unit tests and generate coverage report in HTML format into project's directory: `./app/adapters/persist/build/reports/kover/merged/html/index.html`

There are other tasks (such as koverMergedXmlReport, etc. available).

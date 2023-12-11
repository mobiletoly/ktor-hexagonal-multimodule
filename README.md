# Hexagonal architecture for Address Book application

Backend API service built using hexagonal architecture and implemented with Kotlin's ktor.
This code may seem like an overkill for a simple application such as Address Book, but treat
it as a foundation for much larger services.

# Overview

## What tools/frameworks do we use?

- **gradle** - our build system of choice (using Kotlin DSL)
- **kotlin** - our language of choice
- **ktor 2** for creating web application: https://github.com/ktorio/ktor
- **ExposedSQL** to access database: https://github.com/JetBrains/Exposed
- **HikariCP** for high-performance JDBC connection pool: https://github.com/brettwooldridge/HikariCP
- **Koin** for dependency injection: https://insert-koin.io/
- **PostgreSQL** for database: https://www.postgresql.org/
- **HOCON** for application configuration: https://github.com/lightbend/config/
- **OpenAPI** to generate REST data models

and for testing:
- **Testcontainers** for testing with a real database in Docker: https://github.com/testcontainers/testcontainers-java
- **kotest** for writing tests: https://kotest.io

and some other misc stuff:
- **ktlint** for Kotlin checkstyle (https://pinterest.github.io/ktlint/)
- **kover** for code coverage metrics (https://github.com/Kotlin/kotlinx-kover)


## Goal

Simple Address Book web service written according to Hexagonal architectural patterns to add/delete/fetch
address book items. It demonstrates database access as well as usage of external REST service to assist in
generating random address book items.

## Hexagonal architecture overview

Project uses Hexagonal (or "ports and adapters") architecture. There are a lot of good materials about
hexagonal architecture, so please google it, it is really cool and understanding of it is needed to understand
this project's layout.

In this project we separate functionality into few modules - **adapters** (with submodules), **core**, and **infra**,
with only few modules having dependencies on other modules.

Here is a brief overview of each module:

- **core** - contains business logic (services that implement business use cases, business data models, and
declares port interfaces for adapters).
- **adapters** - provides platform/framework specific functionality (e.g. your database repository classes
go here)
- **infra** - application configuration and launcher
- **common** - light-weight miscellaneous code that can be useful for other modules (logger helpers is a good
examples)

Now let's take a look at each module in more details.

### Core module

This is a module containing a business logic of our application. The main idea is that business logic should
know nothing about frameworks used to implement an application, it should have zero knowledge about database
used, ORM technology used, HTTP client used or what is Kafka, Flink or SQS. In our application Core module
contains services, such as `AddressBookService`, implementing our core business logic: add, update and delete
Address Book entries.

###### Depends on
- It has no dependencies on other modules.

###### What should be in Core
- Service code to provide business logic functionality.
- Business data models
- Declaration of output ports
- Declaration of input ports (a.k.a. use cases).

###### What should not be in Core
- Frameworks (less are better)
- Database, network clients (no Exposed SQL calls, no HTTP calls, etc)
- No transport logic (e.g. no JSON parsing, no JSON annotations etc). It might be tempting to use JSON objects
received from web service controllers, but don't do this, use core models to describe your business logic
and provide mappings between JSON objects received from REST controllers and domain business models. It will
require extra work and some extra effort to write data structures that might look very similar,
but when you decide to change your JSON payload and your business logic will remain the same - it will pay off.

###### What is ports

- Ports are the bridges between Adapters and Core modules. Core declare interfaces it requires from Adapters and put
them in `outport` package. Then Adapters provides implementations (injected via Dependency Injection, we use
Koin framework in our application) for these port interfaces. These ports are called **output ports** and 
adapters implementing output ports are called **secondary adapters** (also known as **driven adapters**).
Secondary adapters should never call business logic directly, it can only return data (models, exceptions etc)
in response for core business logic calls.

There is a concept called **primary adapters** (also known as **driver adapters**). In our code we have only
one primary adapter called `primary-web`. Unlike secondary adapters that can only respond to core logic calls,
primary adapters are the ones that initiate calls to core logic via **input ports**. In our code we name input
ports as **use cases**. Core logic code is responsible to provide service classes that implement use case interfaces.
In our code our primary adapter is ktor application that provides REST handlers. REST handler invokes use case
interface implemented in Core module and Core module might decide to call secondary adapter via output
port to perform framework-specific operation, for example to add data into database.


#### Adapter modules

Platform-specific code. Don't put any of your business logic here. These modules implement platform specific
functionality, such as database access or HTTP calls. As we already mentioned, the special case of adapters is
**primary adapters** containing functionality that trigger business logic via interfaces in core's `usecase`
interfaces, in our case this is ktor's web controllers (routes).

Adapters usually deal with repository classes (SQL repo, HTTP client repo etc) and can work with DTO
(data transfer objects) to communicate with repositories. These DTO objects should not be exposed to core
business logic, because if they change - it should not impact business logic. If adapter wants to return data
back to core business logic, this DTO object should be converted to business model data class first. Same is
for flow from core layer to adapter - core module will be sending model data classes as input parameter to
adapter (via output port) and adapter might need to convert these models to DTO objects first.

###### Depends on
- **core** module. Primary adapters should only use `usecase` package and model data classes used by use cases
(these model classes may reside outside of `usecase` package, e.g. in our app we use `error` and `models` packages
as well). Secondary adapters should only use `outport` package (and model data classes, errors etc).

###### What should be in adapters
- Web Service controllers (in our app it is **ktor** routes)
- HTTP/REST clients
- Database repositories to provide access to underlying database.
- For example Kafka or ActiveMQ code to send or receive data.
- DAO/DTO objects. Yes, DAO objects should not be in Ports or Core modules. First of all, they can contain
  framework-specific code or annotations (e.g. JPA annotations). Second, you don't want to have your business
  entities to reflect database table layout.

###### What should not be in adapters
- Avoid any business logic code


#### Infra (infrastructure) module

Application launcher. Should be a very simple code that performs dependency injection of classes from other
modules. Launches primary adapter.

###### Depends on
All other modules.


#### Common module

**common** module should not be used too often. It may contain some utility functionality, for example in our app we
have some logger utility functions in the common module, because logging required in both **adapter** and **core**
modules.

###### Depends on
No module dependencies


### Workflow examples

#### Add new person workflow

One of the workflows we have is ability to add new person into a persistent storage:

```
[1 REST controller]--> [2 Use Case]:[Service]---> [3 Output Port][Adapter]--> [4 Persist repository]
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


#### Generate new random person workflow

Another example of workflow is an ability to generate a new random person and store it in a database.
Our logic will generate a random person information using remote service located at https://randomuser.me/api
and store it in persistent storage.

```
[1 REST controller]--> [2 Use Case]:[Service]---> [3 Output Port][Adapter]--> [4 Persist repository]
                                                |
                                                + --> [5 Output Port][Adapter]--> [6 Remoting repository]
```

1. User performs HTTP POST request to `/persons/random` to generate random person contact.
Port `PopulateRandomPersonUsecase` will be used.<br><br>

2. Core module's code in `core.services.RandomPersonService` class implements `PopulateRandomPersonUsecase`
and it uses to output ports to perform it work: `GenerateRandomPersonPort` to obtain a new random person
from a remote service and `AddPersonPort` (similar to our first example) to persist this random person
into a database.<br><br>

3. `adapters.persist.addressbook.SavePersonAdapter` class in module `persist` implements `AddPersonPort` and performs
entity transformations and calls to persist repository to store data.<br><br>

4. Repository class uses ExposedSQL to store SQL entities into database.<br><br>

5. `adapters.remoting.randomperson.RandomPersonAdapter` class in module `remoting` implements
`GenerateRandomPersonPort` port and performs call to remoting repository.<br><br>

6. In our case remoting repository is represented by `RandomPersonHttpClient` class that perform HTTP call (via
ktor client library) to randomuser.me service.


### Koin modules

We use Koin framework for dependency injection. The way how we organize our code for dependency
injection is that we have a separate Koin modules for each gradle module in our project.
For instance, you can find a file `adapters/persist/_PersistenceModule.kt` that
contains `val persistenceModule = module { ... }` to declare dependencies for Persist adapter
module. We have similar approach for other modules as well. Last step is to wire all dependencies
together, and we do it from `infra` module (specifically in `App.kt` file). Now your dependencies
will be available through the entire application.

# Setup for run

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

APP_DEPLOYMENT_ENV=local;APP_DB_USERNAME=baeldung;APP_DB_PASSWORD=baeldung;APP_DB_URI=jdbc:postgresql://localhost:5432/addrbook;APP_VERSION=0.0;APP_BUILD_NUMBER=0

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

Make sure to replace *your-local-ip-address* in APP_DB_URI in command above to the actual IP address of your machine
that you can find with **ifconfig** or **ipconfig** shell commands (you cannot use *localhost* anymore, because localhost
inside AddressBook application docker container will be pointing to that container instead of your host machine).


## HOCON configuration

Application configurations are stored in popular and powerful HOCON format (https://github.com/lightbend/config).
Our config files location is at `app/infra/src/main/resource` and here you can find files such
as `config-common.conf`, `config-local.conf` and `config-prod.conf`. Common file is used to keep
all configurations that can be shared between all deployments, while `-local` and `-prod` means
that configuration will be loading for specific environments (you can add your own if needed).
This second part after dash `-` must match what you pass via `APP_DEPLOYMENT_ENV` environment
variable.


## REST data models

We use a hybrid approach to build REST endpoints in our ktor code. All ktor routes are created manually in
the code, while data models (payloads) for PUT/POST requests as well as all data model responses - declared
in OpenAPI spec. You can find them in `app/adapters/primary-web/src/main/resources/openapi/addrbook.yaml` file.
By using openapi generator plugin we generate kotlin data classes based on this file. If you decide to
add/change REST models, you can edit this file and run code generator after that:

        $ ./gradlew openApiGenerate


## Testing

### Unit tests

Unit tests automatically run when you perform a build. All tests are light-weight and use mocks, with the noticeable
exception of `app/adapters/persist` module. Here we use Test Containers to spin up docker image with PostgreSQL,
so tests for adapters are run with a real PostgreSQL database, no database mocks are used. This will allow us to
be more confident in our code that interacts with database.

Not that if you want to run unit tests per file or class level in IntelliJ, you must install Kotest plugin
from marketplace.

### Manual tests

There is an OpenAPI specification in `app/adapters/primary-web/src/main/resources/openapi/addrbook.yaml` file,
you can import it into Postman if you want to manually call app's REST APIs.


## Generate coverage report

To generate a coverage report in HTML format you must run:

    $ ./gradlew build koverHtmlReport

It will run unit tests and generate coverage report in HTML format into project's directory:
`./app/adapters/persist/build/reports/kover/merged/html/index.html`

There are other tasks (such as koverMergedXmlReport, etc. available).


## Logging

This application uses simple logging based on slf4j. Note that our code supports X-Request-Id HTTP header
that you can specify when perform HTTP request (or it will be auto-generated). The value of this header
will be attached to every log line (in form of `CallRequestId=...`) which will assist you in troubleshooting
your log files (or in Splunk).

## Helper functionality

### MustBeCalledInTransactionContext annotation

This annotation is used to mark functions that must be called in a transaction context. It is used
in `app/adapters/persist` module to make sure that all database calls are performed in a transaction
context. If you forget to wrap a function with this annotation in a transaction context - you will
get a compile time error. 

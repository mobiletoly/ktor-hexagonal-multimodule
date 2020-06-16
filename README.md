# Hexagonal architecture for AddressBook

Backend API service built using hexagonal architecture and implemented with Kotlin's ktor.
This code may seems like an overkill for a simple application such as Address Book, but treat
is a foundation for much larger services. 

# Overview

## What tools/frameworks do we use?

- **gradle** - our build system of choice (using Kotlin DSL)
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


## Goal

Simple Address Book web service to add/delete/fetch address book items. It demonstrates database access
as well as using external REST service to assist in generating random address book items.
You can find Postman collection to access this web service in `integration/postman` directory.

## Hexagonal architecture overview

Project uses Hexagonal (or "ports and adapters") architecture (google it, it is really cool) to separate functionality
into 5 modules - **core**, **adapters**, **ports**, **shared**, **configuration**, with only few modules having dependencies
on other modules.

So here is a brief overview of each module:

- **core** - contains business logic (services that implement use cases)
- **adapters** - provides platform/framework specific functionality (e.g. your database repository classes
go here)
- **ports** - set of interfaces and data classes (aka POJO) that is used by core module to interact with adapters
module.
- **configuration** - application configuration and launcher
- **shared** - miscellaneous code that can be useful for all other modules 

Now let's take a look at each module in more details

#### Core module

This is a module containing a business logic of our application. The main idea is that business logic should
have no idea about frameworks used to implement an application, it should have zero knowledge about database
used, database ORM technology used, HTTP client used or (for instance) what is Kafka. For example in our
application Core module contains multiple service, such as `LoadAddressBookEntryService`,
`SaveAddressBookEntryService` etc implementing our core business logic:
add, update and delete Address Book entries.

###### Depends on
- **Ports** module. Core uses Ports module to provide service functionality to Adapters module
via Use Case interfaces declared in Ports. Adapters module uses this use case interfaces
to call business logic from Web controllers (routes).

###### What should be in Core
- Service code to provide business logic functionality.

###### What should not be in Core
- Frameworks (less are better)
- Database, network clients (no SQL Exposed calls, no Kafka calls, no SQS call etc)
- No transport logic (e.g. no JSON parsing, no JSON annotations etc). It might be tempting to use JSON objects received
from web service controllers, but don't do this, use **ports** for DTO (Data Transfer Objects) to communicate with
**adapters** module. It will require extra work and some extra effort to write data structures that might look very
similar, but when you decide to change your JSON payload, and your business logic will remain the same - it will pay off.
- DAO 

#### Ports module

Ports is a bridge between Adapters and Core. Core declare interfaces it requires from Adapters and put them
in `ports.input` package. Then Adapters provides implementations (injected via Dependency Injection, we use
Koin framework in our application). If Core wants to share some functionality (usually business services) with
Adapters (e.g. services with business logic to be called from Adapter's web controllers) - then it will provide
interfaces in `ports.output` for Adapters to use. Also, Ports can declare data
classes (or POJOs) to allow Core and Adapters to communicate to each other (if boundaries is not
clear and flow goes in both directions - we use `ports.models` to declare this kind
of data classes).

###### Depends on
No module dependencies

###### What should be in ports
- `ports.input` - interfaces for Adapters module services *required* by Core to perform its business logic.
   This is a place for application's Use Case interfaces as well (implemented by Adapter module).
- `ports.output` - interfaces for *provided* Core services to be called from Adapters module (in our case Core
services will be called from web controllers/routes that reside in Adapters module).
- `ports.models` - DTO objects for Core<=>Adapters communication can be declared here if they
logically they do not fit in input and output packages.

###### What should not be in ports
- Anything else


#### Adapters module

Platform-specific code. Don't put any of your business logic here. This module
implements platform specific functionality, such as database access or HTTP calls.
Adapters module contains two logical parts - `driver` (primary adapters) and `driven` (secondary adapters).
Primary adapters contain functionality that triggers business logic via `ports.input` interfaces, in our case
this is ktor's web controllers (routes). To make it more obvious we put primary code in
`adapters.primary` package (we don't do the same for secondary adapters, they reside high-level under `adapters`
package).

###### Depends on
- **Ports** module. Adapters use this module to fulfill Core module needs. For example Core needs access to persistent
storage (and Core module does not really care how data must be stored) to add/delete/update Address Book entries.
In this case Core module will add its requirements into `ports.input` package and Adapters module should fulfill
these requirements. For example in our app Core declares `AddAddressBookEntryUseCase` interface
and Adapters provides a SQL database implementation of this interface via
`AddressBookPersistenceAdapter` class.

###### What should be in adapters
- Web Service controllers (in our app it is **ktor** routes)
- HTTP/REST clients
- Database repositories to provide access to underlying database.
- For example Kafka or ActiveMQ code to send or receive data.
- DAO objects. Yes, DAO objects should not be in Ports or Core modules. First of all, they can contain
framework-specific code or annotations (e.g. JPA annotations). Second, you don't want to have your business
entities to reflect database table layout.
- DTO (Data Transfer Object) entities. It could be JSON payloads associated with HTTP request/response calls
and they must be converted to business entities (declared in Ports module) before being passed
to Core module (if needed).

###### What should not be in adapters
- Avoid any business logic code


#### Configuration module

Application launcher. Should be a very simple code. Can contain application specific resources.
Contains application configuration files as well.

###### Depends on
All other modules.


#### Shared module

**shared** module should not be used too often. It may contain some utility functionality, for example in our app we
have some logger utility functions in the shared module, because logging required in both **adapter** and **core**
modules.

###### Depends on
No module dependencies


### Example of workflow in modules
 
1. User performs HTTP POST request to `/addressBookEntries` to add new AddressBook entry.
This request handled by REST controller in `adapters.primary.web.routes.addressbook.SaveAddressBookEntryRoute`
class. JSON request payload is validated and deserialized into
`adapters.primary.web.routes.addressbook.dto.AddressBookEntryDto` data class, validated and copied into
`ports.models.AddressBookEntry` data class required by Core service.
2. Web controller calls `addAddressBookEntry()` method of injected interface `ports.output.addressbook.SaveAddressBookEntryPort`
(and implemented by Core module in `core.addressbook.SaveAddressBookEntryService` class).
Previously created AddressBookEntry object will be passed to this method.
3. Core module's code in class `SaveAddressBookEntryService` performs business logic related to validating and
storing AddressBookEntry object from Web controller. At some point of time business logic requires storing
of new entry in persistent storage. Core code calls `addAddressBookEntry()` method of injected interface
`ports.output.addressbook.SaveAddressBookEntryPort` implemented by Adapter's module class
`adapters.persistence.addressbook.AddressBookPersistenceAdapter`.
4. Adapter converts AddressBookEntry into two DAO objects `AddressBookItemSqlEntity` and `PostalAddressSqlEntity`
and store them in SQL database (via repository classes). Once DAO objects are stored, new resuled DAO
objects are converted back to new AddressBookEntry and returned to Core module.
4. Core service code returns AddressBookEntry object back to Adapters Web controller when it gets serialized into
JSON response (`AddressBookEntryResponseDto` object) and returned to a caller.


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

        ./gradlew build :application:configuration:run --args='-config=application/configuration/src/main/resources/application-dev.conf'

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

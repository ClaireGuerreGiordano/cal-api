# CAL API

This POC scope is to provide an API to upsert/read coins into database from compressed files  with batch capability.

## How to run in local

- docker-compose up
- sbt run

## How to run IT tests in local

- docker-compose up
- sbt run

## Limitations

- Vault related attributes are not supported.

- Tokens are not supported.

## Technical stack

### Libraries

- Scala Typelevel

        cats validated for domain model validation

        circe for json codec for database storage and read purposes

        http4s for API layer

- tapir for automatic OpenAPI documentation generation

- better files for file processing

- posgresql 13

## Data model

For resiliency purpose, we choose to put validation into domain model as much as possible.

Some rules are enforced by data types:

- family attributes can only be one of the following: Bitcoin, Ethereum, Ripple 

- Network type can only be one of the following: Main, Test

- Networks list should not be empty

- Units list should not be empty

Others are enforced  and validated using cats validated:

- Only one network of type Main is allowed

- A coin has at least one unit with magnitude 0

From database point of view

- family and network type are Postgresql enumeration types.

- Primary key is ticker + name.

## Validation

### Using cats Validated

Uploaded files are validated at parsing stage.

## Upload from files

Database is fed with data parsed and validated from uploaded files. These files are named common.json and reside under directories and sub directories as for now in CAL repository. They must be bundled into a zip file prior to be submitted through a POST request.

File processing is made using better-files library, which allows us to create a temporary directory to unzip content and dispose it once it is useless.

## Storage
### Format

Attributes of type list are stored into database as JSON. This choice limits query capacity (for example, we could not easily query all coin with a Test network), but it simplifies the database schema.

## API

- /cal/insert endpoint

- /cal/coin/ticker/name endpoint

- /cal/coins endpoint

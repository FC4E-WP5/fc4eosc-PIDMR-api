# PID Meta Resolver

The PID Meta Resolver is a generalized resolver for mapping items into records. Actually the  PID Meta Resolver will  know where to route different types of identifier – ex. DOI, URN:NBN. 
PID Meta Resolver which should improve a machine based data processing and allows to get digital object information without in-depth knowledge of the resolution mechanism of different PID systems. 
That enhances the collection and analysis of data collections originating not only from different sources also referenced by different PID systems. 
The PID Meta Resolver should return a minimal set of information. This creates the connection with the PID Kernel Information.

## Expose OpenAPI Specifications

OpenAPI Specifications are accessible at `/open-api` endpoint.

## Swagger UI

Swagger UI is accessible at `/swagger-ui` endpoint.


### Access the Dev Service Database

To access the dev database, please execute the following command:

`mysql -h localhost -P 3306 -u pidmr -p pidmr --protocol=tcp`

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/pid-meta-resolver-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

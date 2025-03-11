# PID Meta Resolver

The PID Meta Resolver is a generalized resolver for mapping items into records. Actually the  PID Meta Resolver will  know where to route different types of identifier – ex. DOI, URN:NBN. 
PID Meta Resolver which should improve a machine based data processing and allows to get digital object information without in-depth knowledge of the resolution mechanism of different PID systems. 
That enhances the collection and analysis of data collections originating not only from different sources also referenced by different PID systems. 
The PID Meta Resolver should return a minimal set of information. This creates the connection with the PID Kernel Information.

## Expose OpenAPI Specifications

OpenAPI Specifications are accessible at `/open-api` endpoint.

## Swagger UI

Swagger UI is accessible at `/swagger-ui` endpoint.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

### Access the Dev Service Database

To access the dev database, please execute the following command:

`psql -h localhost -U pidmr -d pidmr`

### Obtain an access token from Dev Service Keycloak

To obtain an access token from Dev Service Keycloak, please follow the instructions above.

#### Instructions

1. Navigate to the http://localhost:8080

   Open your preferred web browser and navigate to the http://localhost:8080 where the access token can be obtained.

2. Locate the Access Token Button

   Once the web page loads, locate the button that triggers the access token retrieval process. The button should be visible on the web page.

3. Click the Obtain an Access Token button

   Click the access token button to initiate the process of retrieving an access token. This will trigger the necessary steps to obtain the token from the Dev Service Keycloak.

4. Log into Dev Service Keycloak

   Use the following credentials to log into Dev Service Keycloak:

    1) admin user
        - username : `admin`
        - password : `admin`
    2) provider_admin user
        - username : `alice`
        - password : `alice`
    3) plain user
        - username : `evald`
        - password : `evald`
5. Retrieve the Access Token

   After providing the necessary information, the web page will communicate with the authentication server to retrieve the access token. This process may take a few moments. Once the retrieval is successful, the access token will be displayed on the web page.

6. Use the Access Token

   Once you have obtained the access token, you can use it for authenticating API requests. Follow the documentation or guidelines provided by the API to understand how to include the access token in the appropriate HTTP requests using the Bearer Authentication scheme.

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
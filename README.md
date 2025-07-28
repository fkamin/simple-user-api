# Simple user API

## Table of Contents
1. [About project](#about-project)
2. [Tech stack](#tech-stack)
3. [How to start](#how-to-start)
4. [How to stop](#how-to-stop)

## About project

It is a simple service that allows you to:
- Authorize and authenticate user with stateless JWT
- Add items to currently authorized user
- Get all items from currently authorized user

## Tech stack

Technology stack used in project:
- Spring Boot v3.5.4
- Spring Data JPA
- Spring Security, OAuth2, Validation
- Java 21
- MySQL
- Flyway
- Docker
- Swagger
- Testcontainers

## How to run application

In order to run application, first you need to check that you have:
- JDK 21
- Docker

After that, navigate to the root directory of this project and execute this commands:

#### Create jar file
```bash
./mvnw clean install
```

#### Startup docker container
```bash
docker compose up -d
```
Once the container is up and running you can explore the aplication using Swagger
```
http://localhost:8080/swagger-ui/index.html
```

## How to stop application

In order to stop containers, you just need to run this command:
#### Stop docker container
```bash
docker compose down
```

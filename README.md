# Scaling Spring Boot Microservices

It is an example how to scale Spring Boot microservices based on a chat application.

## How to build frontend

1. Move to `frontend/chatapp` dir
2. Run `npm install`
3. Run `npm run build`

After that Spring will use `build` dir as a source for static files.

## How to run backend

1. Build frontend
2. `docker/docker-compose.yml` contains all necessary datasources (PostgreSQL, RabbitMQ, Redis). Run it with `docker-compose up -d` command.
3. Run backend with a command ` ./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8080`. Use different ports if you want to run multiple instances simultaneously.
4. Go to http://localhost:8080. Migrations will create 6 users: *Neo*, *Boris Britva*, *Gendalf*, *Thor*, *Forrest Gump* and *Tyler Durden* with `123` passwords.

## Branches

1. **master** branch contains initial state of the application that is not scalable.
2. **scalable** branch contains fully-scalable application, using RabbitMQ as an external message broker, Redis for cache, and PostgreSQL for sessions.
3. **scalable-crazy** branch contains an example of an approach to fix a problem of **@SubscribeMapping** in a *BeanPostProcessor* manner. It is fully-scalable, but
has a huge disadvantage - every subscribed user gets an initial response.

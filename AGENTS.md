# AGENTS.md

## Purpose
This is a small Java/Quarkus REST service using Quarkus 3.35.3 and Java release 25.

## Agent rules
- Use `./mvnw`, not system `mvn`;
- Use Conventional Commits by default for commit messages.
- Do not assume formatter, linter, static-analysis config, CI workflows, or a lockfile exist here.
- Make sure all tests pass before completing any change task.

## Commands
- Dev mode: `./mvnw quarkus:dev`.
- JVM package: `./mvnw package`; run with `java -jar target/quarkus-app/quarkus-run.jar`.
- Uber jar package: `./mvnw package -Dquarkus.package.jar.type=uber-jar`; run with `java -jar target/*-runner.jar`.
- Native package: `./mvnw package -Dnative`; container native package: `./mvnw package -Dnative -Dquarkus.native.container-build=true`.
- Unit tests: `./mvnw test`; focused unit test.
- Integration tests are skipped by default via `skipITs=true`; run them with `./mvnw verify -DskipITs=false`.
- Focused JVM integration test: `./mvnw verify -DskipITs=false -Dit.test=GreetingResourceIT`.
- Native verification enables integration tests: `./mvnw verify -Dnative`.

## Docker notes
- Package before Docker builds because `.dockerignore` excludes everything except built `target` artifacts.
- `src/main/docker/Dockerfile.jvm` expects `./mvnw package`.
- `src/main/docker/Dockerfile.legacy-jar` expects `./mvnw package -Dquarkus.package.jar.type=legacy-jar`.
- `src/main/docker/Dockerfile.native` expects `./mvnw package -Dnative`.

## Specialized files
- None yet.
- Put future specialized agent docs under `docs/` and link them from this section only after they exist.

# AGENTS.md

## Project
REST API for managing a sports tournament.

## Technologies
- Java
- Spring Boot
- Maven
- REST API

## Project structure
- controller — HTTP request handling and response mapping
- service — business logic and validation
- repository — in-memory data storage
- model — business entities
- dto — request data transfer objects
- factory — business entity creation
- exception — centralized exception handling
- annotation — custom Spring component annotation

## Development rules
- Keep controllers, services and repositories separated by responsibility.
- Use constructor injection for dependencies.
- Use the Cumponent annotation on Spring beans as required by the assignment.
- Preserve existing Spring annotations such as RestController, Service and Repository.
- Return appropriate HTTP status codes for REST operations.
- Validate incoming data and entity relationships.
- Run Maven tests after code changes.
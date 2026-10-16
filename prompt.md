# Project Description

## Project name
Tournament Service

## Purpose
A REST API for managing sports tournaments, teams, users, team applications and matches.

## Main resources
- Users: /api/users
- Teams: /api/teams
- Tournaments: /api/tournaments
- Team applications: /api/team-applications
- Matches: /api/matches

## REST operations
The API supports GET, POST, PUT, PATCH and DELETE operations.

## Architecture
The application separates responsibilities between controllers, services, repositories, business entities, DTOs and factory classes.

## Error handling
The application uses centralized exception handling to return appropriate HTTP error responses.

## Development
The project uses Java, Spring Boot and Maven.
# Movie Database API

A REST API for managing a movie database, built with Spring Boot, Spring Data JPA, and SQLite.

## Project Overview

The API allows a film society to manage their movie collection digitally. It supports full CRUD operations for movies, genres, and actors, with many-to-many relationships between them.

**Tech stack:** Java 21 · Spring Boot 4 · Spring Data JPA · SQLite · Hibernate

## Setup & Installation

### Prerequisites
- Java 21
- Gradle (wrapper included)

### Running the application

```bash
./gradlew bootRun
```

The API starts on `http://localhost:8080`. On first run, the database is seeded with sample data (5 genres, 15 actors, 20 movies).

### Database

SQLite database file is created at `movies.db` in the project root. No installation required.

## API Reference

### Pagination

All list endpoints support pagination via query parameters:

| Parameter | Default | Constraints | Description |
|-----------|---------|-------------|-------------|
| `page` | `0` | >= 0 | Zero-based page number |
| `size` | `10` | 1–100 | Items per page |

Example: `GET /api/movies?page=0&size=10`

### Genres

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/genres` | Create a genre |
| `GET` | `/api/genres` | List all genres (paginated) |
| `GET` | `/api/genres/{id}` | Get genre by ID |
| `PATCH` | `/api/genres/{id}` | Update genre name |
| `DELETE` | `/api/genres/{id}` | Delete genre |
| `GET` | `/api/genres/{id}/movies` | Get all movies in a genre |

**Create genre:**
```json
POST /api/genres
{ "name": "Action" }
```

**Update genre:**
```json
PATCH /api/genres/1
{ "name": "Action & Adventure" }
```

### Movies

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/movies` | Create a movie |
| `GET` | `/api/movies` | List / filter movies (paginated) |
| `GET` | `/api/movies/search?title={title}` | Search movies by title (partial, case-insensitive) |
| `GET` | `/api/movies/{id}` | Get movie by ID |
| `PATCH` | `/api/movies/{id}` | Update movie |
| `DELETE` | `/api/movies/{id}` | Delete movie |
| `GET` | `/api/movies/{id}/actors` | Get all actors in a movie |

**Filtering** (mutually exclusive; priority: actor → genre → year):

```
GET /api/movies?genre=1
GET /api/movies?year=1999
GET /api/movies?actor=2
GET /api/movies/search?title=matrix
```

Filtering by a non-existent genre or actor id returns `404 Not Found`.

**Create movie:**
```json
POST /api/movies
{
  "title": "Inception",
  "releaseYear": 2010,
  "duration": 148,
  "genreIds": [1, 3],
  "actorIds": [4]
}
```

**Partial update (only include fields to change):**
```json
PATCH /api/movies/1
{
  "title": "Inception (Director's Cut)",
  "actorIds": [4, 7]
}
```

### Actors

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/actors` | Create an actor |
| `GET` | `/api/actors` | List / filter actors (paginated) |
| `GET` | `/api/actors/{id}` | Get actor by ID |
| `PATCH` | `/api/actors/{id}` | Update actor |
| `DELETE` | `/api/actors/{id}` | Delete actor |
| `GET` | `/api/actors/{id}/movies` | Get all movies an actor appears in |

**Filter by name** (partial, case-insensitive):
```
GET /api/actors?name=tom
```

**Create actor** (`birthDate` in ISO 8601 format, `movieIds` optional):
```json
POST /api/actors
{
  "name": "Tom Hanks",
  "birthDate": "1956-07-09",
  "movieIds": [7, 13]
}
```

**Partial update (only include fields to change; `movieIds` replaces the full movie list):**
```json
PATCH /api/actors/1
{
  "name": "Keanu Charles Reeves",
  "movieIds": [11]
}
```

## Deleting resources with relationships

By default, deleting a resource that has existing relationships returns `400 Bad Request`:

```
DELETE /api/genres/1
→ 400: "Cannot delete genre 'Action' because it has 5 associated movies"
```

To force-delete (removes relationships and deletes the resource):

```
DELETE /api/genres/1?force=true
→ 204 No Content
```

## Testing

A ready-made Postman collection is included at
[`postman/Movie Database API.postman_collection.json`](postman/Movie%20Database%20API.postman_collection.json) —
import it into Postman to test every endpoint, including error scenarios.
It uses a `baseUrl` collection variable that defaults to `http://localhost:8080`.

Unit/integration tests run against an in-memory H2 database (configured in
`src/test/resources/application.yaml`), so they never touch `movies.db`:

```bash
./gradlew test
```

## HTTP Status Codes

| Status | Meaning |
|--------|---------|
| `200 OK` | Successful GET or PATCH |
| `201 Created` | Successful POST |
| `204 No Content` | Successful DELETE |
| `400 Bad Request` | Invalid input, validation failure, or relationship conflict |
| `404 Not Found` | Resource does not exist |

## Error Response Format

Single error:
```json
{
  "error": "Not Found",
  "message": "Movie not found with id: 99"
}
```

Validation error:
```json
{
  "error": "Validation Failed",
  "fields": [
    { "field": "title", "message": "must not be blank" },
    { "field": "releaseYear", "message": "must not be null" }
  ]
}
```

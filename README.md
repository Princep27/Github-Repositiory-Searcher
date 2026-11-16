GitHub Repository Searcher

Spring Boot (Java 21) app that searches GitHub repositories, saves them in PostgreSQL (existing ones are updated, not duplicated), and lets you retrieve the saved data with filters.


HOW TO RUN

You need Docker.

1. (Optional) Set a GitHub token for a higher rate limit (30 instead of 10 searches per minute):
   export GITHUB_TOKEN=your_token_here

2. Build and start the database and the app:
   docker compose up --build

3. Open Swagger UI: http://localhost:8080/swagger-ui.html

4. Stop: docker compose down
   (add -v to also delete the saved data)

To run the app without Docker: start the database with "docker compose up -d postgres", then run "mvn spring-boot:run".
To run the tests (no database needed, they use H2): mvn test


API 1: SEARCH AND SAVE

POST /api/github/search

Request:
{
"query": "spring boot",
"language": "Java",
"sort": "stars",
"order": "desc"
}

query     required, repository name (partial or full match)
language  optional
sort      optional: stars (default), forks, updated
order     optional: desc (default), asc

Response:
{
"message": "Repositories fetched and saved successfully",
"repositories": [
{
"id": 123456,
"name": "spring-boot-example",
"description": "An example repository",
"owner": "user123",
"language": "Java",
"stars": 450,
"forks": 120,
"watchers": 450,
"openIssues": 3,
"lastUpdated": "2024-01-01T12:00:00Z"
}
]
}

If nothing is found: status 200, message "No repositories found", empty list.


API 2: GET STORED REPOSITORIES

GET /api/github/repositories

All parameters are optional: language, owner, minStars, sort (stars, forks, updated), order (desc, asc)

Example:
GET /api/github/repositories?owner=user123&language=Java&minStars=100&sort=stars&order=desc

Response: { "repositories": [ ... ] }  (same repository format as above)


ERRORS

Format: { "status": 400, "message": "..." }

400  invalid input (empty query, bad sort/order, bad minStars, invalid JSON)
429  GitHub rate limit reached
502  GitHub is down or returned an invalid response


PROJECT STRUCTURE

controller, service, client (GitHub API), repository, entity, dto, mapper, exception, config (Swagger)
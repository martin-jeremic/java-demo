# Weather Atlas

A small full-stack learning sample for a .NET/Angular developer exploring Java, Spring Boot, and React. The Spring API searches cities and retrieves current conditions plus a five-day forecast from [Open-Meteo](https://open-meteo.com/). The browser talks only to the sample API; the provider integration stays on the backend.

## Run locally with Docker

Requirements: Docker Desktop with Docker Compose.

```sh
docker compose up --build
```

Open [http://localhost:5173](http://localhost:5173). The React application is served by Nginx, which forwards `/api` requests to Spring Boot. The backend is also available directly at [http://localhost:8080](http://localhost:8080).

Stop the services with `Ctrl+C`, or run `docker compose down` in another terminal.

## Explore the API

```text
GET /api/weather/locations?query=Copenhagen
GET /api/weather/forecast?latitude=55.68&longitude=12.57&name=Copenhagen
```

Coordinates and search terms are validated by the API. Provider outages return `502 Bad Gateway`; invalid requests return `400 Bad Request`. Open-Meteo does not require an API key for this sample.

## Project structure

```text
backend/   Spring Boot API, provider client, service, and JUnit tests
frontend/  React + TypeScript UI, API client, and Vitest component tests
docker-compose.yml
```

The backend separates HTTP endpoints (`WeatherController`), application orchestration (`WeatherService`), and Open-Meteo HTTP integration (`OpenMeteoClient`). Constructor injection makes dependencies explicit. The frontend keeps HTTP calls in `src/api.ts` and presents loading, empty, and error states.

## Run without Docker

For backend development, install JDK 17 and Maven, then run from `backend/`:

```sh
mvn test
mvn spring-boot:run
```

For frontend development, install Node.js 22 and run from `frontend/`:

```sh
npm ci
npm test -- --run
npm run dev
```

Vite proxies `/api` to `http://localhost:8080` during development. The Compose setup builds and tests the backend image and serves the production frontend build.

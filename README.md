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

Install JDK 25 (LTS), Maven 3.10.0, and Node.js 24 (LTS, pinned in `.nvmrc`). Spring Boot 4.1.1 supports Java 25. Open two terminals from the project root.

**Terminal 1 — backend:**

```powershell
Set-Location .\backend
mvn test
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. Its local CORS allowlist defaults to the Vite origin `http://localhost:5173`. To allow a different origin, set `WEATHER_CORS_ALLOWED_ORIGINS` to a comma-separated list before starting Spring Boot.

**Terminal 2 — frontend:**

```powershell
Set-Location .\frontend
npm ci
npm test -- --run
npm run dev
```

Open `http://localhost:5173`. Vite hot-reloads frontend changes.

## Environment-specific API URL

The frontend reads `VITE_API_BASE_URL` from Vite's mode-specific env files:

- `.env.development` is used by `npm run dev` and points directly to `http://localhost:8080/api`. Spring allows the Vite origin through its configured CORS allowlist.
- `.env.production` is used by `npm run build` and defaults to `/api`, which Nginx proxies to the backend in Docker Compose.

To point a production build at a separately hosted API, create `frontend/.env.production.local` (ignored by Git) with, for example:

```dotenv
VITE_API_BASE_URL=https://weather-api.example.com/api
```

Then rebuild the frontend. Vite embeds `VITE_*` values in the generated JavaScript at build time, so these values are public and must not contain secrets. A separately hosted API must allow the frontend's origin via `WEATHER_CORS_ALLOWED_ORIGINS`. For a Compose image build, you can override the API base with `VITE_API_BASE_URL=https://weather-api.example.com/api` in the environment running `docker compose build`.

## Run and debug both apps in VS Code

Install the **Extension Pack for Java** and make sure JDK 25 and Maven 3.10.0 are available to VS Code. Open this project root as the workspace. Choose **Weather Atlas (full stack)** from Run and Debug and press **F5**:

- VS Code builds and starts the Spring Boot API in a Java debug session.
- A background task starts the Vite server, then opens the app in Chrome with frontend source-map debugging.
- Java breakpoints work in backend source; browser breakpoints work in frontend TypeScript/React source. Stop the compound session to stop both.

You can also launch **Weather API (Spring Boot)** alone and start Vite separately with `npm run dev`. The launch configurations and Vite task are in `.vscode/launch.json` and `.vscode/tasks.json`.

For frontend-only checks, run `npm test -- --run` and `npm run build` from `frontend/`. For backend checks, run `mvn test` from `backend/`.

# College Library Portal

A beginner-friendly Library Management System for a college administration desk. The working project keeps the requested separation: `libraryapp/` is the Spring Boot + JPA REST API and `library-frontend/` is the React + Vite client. The application persists books, members, and issue/return transactions in a MySQL-compatible database.

## Requirements

Install Java 21+, Maven 3.9+, Node.js 20+, pnpm 11+, and MySQL 8+ for a local setup. The managed project environment already supplies Java, Node, pnpm, and a project-scoped MySQL-compatible `DATABASE_URL`; use the commands below from the repository root.

## Start the project

Keep the backend and frontend running in separate terminals. The Vite proxy forwards `/api` requests to `http://127.0.0.1:8080`; if the backend is not running, Vite reports `ECONNREFUSED`.

### Option A — local MySQL

Create the database once:

```sql
CREATE DATABASE library_db;
```

Start the backend with local credentials (change them if needed):

```bash
SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/library_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' \
SPRING_DATASOURCE_USERNAME=root \
SPRING_DATASOURCE_PASSWORD=root \
./scripts/start-backend.sh
```

On Windows PowerShell, set the database settings and start the backend:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3306/library_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:SPRING_DATASOURCE_USERNAME = 'root'
$env:SPRING_DATASOURCE_PASSWORD = 'root'
pnpm backend
```

In a second terminal, start the React client:

```bash
pnpm install
pnpm --dir library-frontend dev
```

Open `http://localhost:3000`.

### Option B — managed project database

The managed environment exposes `DATABASE_URL` as a MySQL-style URL. The helper converts it to the JDBC URL Spring Boot expects and starts the API on port 8080:

```bash
pnpm install
./scripts/start-backend.sh
```

On Windows, if `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` are already configured in the environment, start the backend with `pnpm backend`.

In a second terminal, start the frontend:

```bash
pnpm --dir library-frontend dev
```

The Vite development server listens on port 3000 and proxies `/api` to Spring Boot on port 8080. Spring Boot creates/updates the three tables and inserts sample books and members only when the tables are empty.

Confirm the API is available at `http://localhost:8080/api/health` before using the frontend.

## Deploy to Render

The repository includes a Render Blueprint for a static frontend and Spring Boot API. The frontend build receives the API hostname automatically, and the API allows the frontend's Render origin.

1. Create a MySQL-compatible TiDB Cloud Starter instance and enable its TLS-protected public endpoint. Copy its JDBC connection URL, username, and password from the provider dashboard; do not commit these values.
2. In Render, choose **New → Blueprint**, connect `31Aamna/college-library-portal`, and deploy the `main` branch. Render will prompt for `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`. Enter the TiDB values there as secret environment variables.
3. If TiDB requires an IP allowlist, allow the Render API service's outbound addresses. Keep TLS enabled.
4. Wait for both services to finish deploying. Open the `college-library-frontend` URL; the API health check is available at the `college-library-api` URL plus `/api/health`.

The included Render services use the free plan, which is suitable for a demo but may spin down when idle and take about a minute to wake. Check current Render and database-provider limits before use. This app has no authentication; anyone who can reach the public site can modify its demo books, members, and transactions. Do not enter real patron information.

## Build and test commands

```bash
pnpm --dir library-frontend build
mvn -f libraryapp/pom.xml test
mvn -f libraryapp/pom.xml package
```

The API health check is `GET http://localhost:8080/api/health`. The fine rate defaults to 1.50 per overdue day and can be changed with `LIBRARY_FINE_RATE=2.00`.

## Main rules for a viva

The Controller receives HTTP requests, the Service layer applies business rules, the Repository layer talks to JPA/MySQL, and the Model layer represents the three tables. An issue operation checks both foreign-key records and available copies, then reduces the copy count and saves the transaction inside one `@Transactional` method. A return operation restores the copy, records the return date, and calculates `overdue days × fine rate`. The delete endpoints reject books or members that still have an active issued transaction; once all issues are returned, their historical transactions are removed with the deleted parent so the foreign-key relationship remains consistent.

See [`docs/API.md`](docs/API.md) for endpoint details, [`docs/ER-DIAGRAM.md`](docs/ER-DIAGRAM.md) for the ER diagram, and [`docs/TEST-CASES.md`](docs/TEST-CASES.md) for demonstration test cases.

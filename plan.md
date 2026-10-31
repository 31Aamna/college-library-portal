# College Library Portal — Implementation Plan

## Scope and approach

Build a beginner-friendly college library administration portal with a React/Vite frontend and a Spring Boot REST API backed by the project’s managed MySQL-compatible database. The frontend uses React Router for pages, Axios for API calls, and a small reusable UI vocabulary for tables, forms, status badges, notices, and confirmation dialogs. The backend follows Controller → Service → Repository → Model layering, keeps issue/return operations transactional, and exposes a dashboard aggregation endpoint for the summary cards.

The managed project keeps the requested folder names: `libraryapp/` contains the Spring Boot backend, and `library-frontend/` contains the React client. The managed Preview runs the Vite development server on port 3000 and proxies `/api` calls to Spring Boot on port 8080; local and deployment instructions document both processes explicitly. `DATABASE_URL` is converted to a JDBC URL by the supplied backend start script when running against the managed database, while a normal local MySQL JDBC URL remains the default.

## Project structure

- `libraryapp/pom.xml`: Spring Boot dependencies and Java 21 build settings.
- `libraryapp/src/main/java/com/dbit/libraryapp/model`: JPA entities and transaction status enum.
- `libraryapp/src/main/java/com/dbit/libraryapp/repository`: Spring Data repositories and search/count queries.
- `libraryapp/src/main/java/com/dbit/libraryapp/service`: validation, business rules, fine calculation, and transaction-safe issue/return workflows.
- `libraryapp/src/main/java/com/dbit/libraryapp/controller`: REST resources for books, members, transactions, dashboard, and health.
- `libraryapp/src/main/java/com/dbit/libraryapp/exception`: domain exceptions and JSON error handling.
- `libraryapp/src/main/resources`: application properties and schema/data bootstrap.
- `library-frontend/src/components`: page-level components required by the brief, plus shared layout primitives.
- `library-frontend/src/services/LibraryService.js`: Axios client and API methods; no record arrays are hardcoded in the UI.
- `library-frontend/src/styles/style.css`: responsive navy/teal academic design system.
- `library-frontend/public/manus-routes.json`: route manifest for the managed preview.
- `docs/API.md`, `docs/ER-DIAGRAM.md`, `docs/TEST-CASES.md`, `README.md`: setup, endpoint, ER, testing, and viva material.

## Data and business rules

`books` stores title, author, unique ISBN, category, total copies, and available copies. `members` stores a unique member code, name, email, phone, and registration date. `transactions` references one book and one member, stores issue/due/return dates, status, and the calculated fine. Issuing is allowed only for a registered member and a book with an available copy. Returning is idempotence-protected by active status, restores one available copy, and calculates `max(0, overdue days) × fine rate`. Deleting a book or member with an active issued transaction is rejected. The default fine rate is configurable through `LIBRARY_FINE_RATE`/`library.fine-rate` and defaults to 1.50 per overdue day.

## Design direction

Three possible directions considered: **Quiet Academic Utility** (0.06), **Civic Archive** (0.04), and **Editorial Catalog** (0.08). The selected direction is **Quiet Academic Utility**.

- **Design movement:** restrained institutional software influenced by university registrar and library catalog interfaces.
- **Core principles:** scan-first hierarchy, calm contrast, visible system status, and low-friction daily workflows.
- **Color philosophy:** deep navy anchors trust and structure; teal marks action and progress; off-white reduces visual fatigue; amber/red are reserved for overdue and error states.
- **Layout paradigm:** a persistent left navigation rail paired with a compact context header and dense, well-aligned content panels rather than a decorative centered dashboard.
- **Signature elements:** slim teal rule accents, compact uppercase section labels, and status pills that use color only when it communicates state.
- **Interaction philosophy:** every destructive action is explicit; common actions stay near the relevant table; feedback is immediate and plain-language.
- **Animation:** short 120–180ms fades and hover transitions only; no looping or ornamental motion.
- **Typography system:** Inter for all interface text; semibold headings, 12px uppercase labels, 14–15px body text, and tabular numerals for metrics.
- **Brand essence:** a credible daily workspace for college library staff — focused, reliable, and practical.
- **Brand voice:** direct and service-oriented. Example lines: “Keep the collection moving.” and “Returns are recorded against the original issue.”
- **Wordmark & logo:** a simple open-book mark built from two opposing teal strokes beside the `COLLEGE LIBRARY` wordmark.
- **Signature brand color:** `#0F766E`, used for primary actions and the book-mark motif.

## Implementation and serving

The React client is served by Vite on `0.0.0.0:3000`, with a development proxy from `/api` to `http://127.0.0.1:8080`. Spring Boot serves the REST API on `0.0.0.0:8080` and accepts CORS from the Vite origins. The database schema is created by Hibernate with `ddl-auto=update`, and a guarded bootstrap inserts realistic sample records only when the tables are empty. For a normal local MySQL installation, create `library_db` and run the documented commands; for the managed project, use the supplied DSN conversion script so the same database is used by development and published environments.

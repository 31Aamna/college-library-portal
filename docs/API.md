# REST API documentation

Base URL: `http://localhost:8080/api` during local development. The React client calls the same paths through Vite's `/api` proxy.

## Books

| Method | Path | Purpose |
|---|---|---|
| GET | `/books?search=&category=` | List books; search matches title, author, ISBN, or category. |
| GET | `/books/{id}` | Fetch one book. |
| POST | `/books` | Create a book. Body: `title`, `author`, `isbn`, `category`, `totalCopies`. |
| PUT | `/books/{id}` | Update book details and total copies. |
| DELETE | `/books/{id}` | Delete a book unless it has an active issue. |

## Members

| Method | Path | Purpose |
|---|---|---|
| GET | `/members?search=` | List or search by member ID, name, or email. |
| GET | `/members/{id}` | Fetch one member. |
| POST | `/members` | Register a member. Body: `memberCode`, `name`, `email`, `phone`. |
| PUT | `/members/{id}` | Update a member. |
| DELETE | `/members/{id}` | Delete unless the member has an active issue. |

## Transactions

| Method | Path | Purpose |
|---|---|---|
| GET | `/transactions?status=ISSUED` | Read transaction history, optionally filtered by `ISSUED` or `RETURNED`. |
| GET | `/transactions?overdue=true` | Read currently issued transactions past their due date. |
| GET | `/transactions/dashboard` | Return total books, available copies, members, issued books, and overdue books. |
| GET | `/transactions/fine-rate` | Return the configured per-day fine. |
| POST | `/transactions/issue` | Issue a book. Body: `bookId`, `memberId`, `dueDate`. |
| POST | `/transactions/{id}/return` | Record a return and calculate any fine. |
| GET | `/health` | Basic service health check. |

Successful issue/return responses are flattened for the UI: they contain `bookTitle`, `memberName`, `memberCode`, `issueDate`, `dueDate`, `returnDate`, `status`, `fine`, and `overdue`. Validation errors use HTTP 400, missing records use 404, uniqueness conflicts use 400/409, and unexpected server failures use 500 with a readable `message`.

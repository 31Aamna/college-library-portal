# Delivery outcomes

- [x] **Working full-stack project and integration**
  - Built the application from scratch with a React/Vite frontend, Spring Boot backend, and MySQL-compatible persistence.
  - Kept field names, API paths, and data types consistent across frontend, backend, and database layers.
  - All displayed and modified records come from the backend/database rather than hardcoded frontend arrays.
  - Exact commands for MySQL, Spring Boot, and React are documented in `README.md`.
  - Evidence: Vite production build passed; Maven test suite passed; live health, books, members, dashboard, issue, return, and transaction-history endpoints passed through the Vite `/api` proxy.

- [x] **Dashboard and navigation**
  - Implemented a persistent left sidebar, compact top header, responsive page shell, and navigation for Dashboard, Books, Members, Issue & Return, and Transaction History.
  - Dashboard shows total books, available copies, registered members, issued books, overdue books, and recent activity.

- [x] **Book management**
  - Implemented add, view, edit, delete, search, and category filtering for books; search covers title, author, ISBN, and category.
  - Stores title, author, ISBN, category, total copies, and available copies.
  - Prevents duplicate ISBNs and invalid copy counts; active transactions block deletion, while historical transactions are removed with a successfully deleted parent to preserve foreign-key consistency.

- [x] **Member management**
  - Implemented register, view, edit, search, and delete for members.
  - Stores member ID/code, name, email, phone, and registration date.
  - Prevents deletion during an active issue and validates required fields plus unique member ID/email.

- [x] **Issue, return, overdue, and fines**
  - Issues available books only to registered members, stores issue/due dates, and automatically updates available copies.
  - Processes returns, restores available copies, prevents issuing unavailable books, and rejects invalid member/book IDs.
  - Identifies overdue books and calculates a configurable per-day fine.
  - Automated service tests cover issue copy decrement, unavailable books, invalid member IDs, return copy restoration, and overdue fine calculation.

- [x] **Transaction history and UX quality**
  - Displays issued and returned books, member details, dates, status, and fines.
  - Includes loading indicators, success/error messages, search/filter controls, empty states, clear action buttons, confirmation before deletion/return, status badges, and responsive tables/forms.
  - Uses the professional navy/teal/off-white academic visual system with Inter typography and Lucide icons.

- [x] **Documentation and verification**
  - Includes database setup/schema instructions, API endpoint documentation, an ER diagram, test cases, and a short college demonstration/viva explanation.
  - Exercised the live issue-return workflow and ran the automated backend service suite with 7 passing tests.

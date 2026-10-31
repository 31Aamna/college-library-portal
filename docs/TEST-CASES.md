# Test cases

The project includes automated service coverage in `libraryapp/src/test/java/com/dbit/libraryapp/service/BookServiceTest.java` and `TransactionServiceTest.java`. Run it with `mvn -f libraryapp/pom.xml test` before the live API walkthrough below.

| ID | Scenario | Steps | Expected result |
|---|---|---|---|
| TC-01 | Add book | Open Books → Add book, enter valid details, save. | HTTP 201; the book appears in the catalog and available copies equal total copies. |
| TC-02 | Duplicate ISBN | Add another book with an existing ISBN. | Save is rejected with a readable uniqueness message; no duplicate row is created. |
| TC-03 | Invalid copies | Submit a book with zero or negative copies. | Client validation blocks submission or API returns HTTP 400. |
| TC-04 | Edit book copies | Edit a book and increase total copies. | Available copies increase by the same amount when no copies are issued. |
| TC-05 | Member registration | Register a member with a unique ID and valid email. | HTTP 201; record appears in Members. |
| TC-06 | Issue valid book | Select a member, an available book, and a future due date. | Transaction is ISSUED and available copies decrease by one. |
| TC-07 | Invalid member ID | POST `/transactions/issue` with a non-existent `memberId`. | HTTP 404; no copy count or transaction changes. |
| TC-08 | Unavailable book | Attempt issue after available copies reach zero. | HTTP 400 with “No available copies” and no new transaction. |
| TC-09 | Return on time | Return an issued book before/on its due date. | Status becomes RETURNED, return date is recorded, fine is 0.00, and available copies increase. |
| TC-10 | Overdue fine | Return after the due date. | Fine equals overdue days × configured per-day rate and appears in history. |
| TC-11 | Delete active record | Try to delete an actively issued book or member. | HTTP 400; deletion is blocked. |
| TC-12 | Search/filter | Search title/author/ISBN and filter a category; search member name/email. | Tables show only matching backend records. |
| TC-13 | Dashboard | Issue and return sample records, then open Dashboard. | Total books, copies, members, issued count, overdue count, and recent activity reflect database state. |

For a live demonstration, show TC-01, TC-05, TC-06, TC-09, and TC-10 in that order, then point to the controller/service/repository separation.

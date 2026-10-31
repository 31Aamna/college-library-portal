# Entity relationship diagram

```mermaid
erDiagram
    BOOKS ||--o{ TRANSACTIONS : "is issued in"
    MEMBERS ||--o{ TRANSACTIONS : "borrows"

    BOOKS {
        BIGINT id PK
        VARCHAR title
        VARCHAR author
        VARCHAR isbn UK
        VARCHAR category
        INT total_copies
        INT available_copies
        DATETIME created_at
    }

    MEMBERS {
        BIGINT id PK
        VARCHAR member_code UK
        VARCHAR name
        VARCHAR email UK
        VARCHAR phone
        DATE registration_date
    }

    TRANSACTIONS {
        BIGINT id PK
        BIGINT book_id FK
        BIGINT member_id FK
        DATE issue_date
        DATE due_date
        DATE return_date
        VARCHAR status
        DECIMAL fine
    }
```

A book or member can have many historical transactions. Each transaction references exactly one book and one member. `available_copies` is maintained by the service layer: it decreases on issue and increases on return. The foreign keys prevent orphaned transactions.

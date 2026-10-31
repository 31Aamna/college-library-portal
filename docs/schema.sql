CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE IF NOT EXISTS books (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(160) NOT NULL,
  author VARCHAR(120) NOT NULL,
  isbn VARCHAR(30) NOT NULL UNIQUE,
  category VARCHAR(80) NOT NULL,
  total_copies INT NOT NULL,
  available_copies INT NOT NULL,
  created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS members (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  member_code VARCHAR(30) NOT NULL UNIQUE,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  phone VARCHAR(30) NOT NULL,
  registration_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_id BIGINT NOT NULL,
  member_id BIGINT NOT NULL,
  issue_date DATE NOT NULL,
  due_date DATE NOT NULL,
  return_date DATE NULL,
  status VARCHAR(20) NOT NULL,
  fine DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  CONSTRAINT fk_transactions_book FOREIGN KEY (book_id) REFERENCES books(id),
  CONSTRAINT fk_transactions_member FOREIGN KEY (member_id) REFERENCES members(id)
);

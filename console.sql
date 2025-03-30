-- MySQL/PostgreSQL
CREATE TABLE books (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       genre VARCHAR(100),
                       isbn VARCHAR(20) UNIQUE,
                       available BOOLEAN DEFAULT TRUE

);
-- The UPDATE command to modify books table should be separate
-- Update availability when a book is borrowed
UPDATE books SET available = false WHERE id = ?;

-- Update availability when a book is returned
UPDATE books SET available = true WHERE id = ?;
ALTER TABLE books ADD due_date DATE;

-- Create transaction_history table
CREATE TABLE transaction_history (
                                     id INT PRIMARY KEY AUTO_INCREMENT,
                                     user_id INT,
                                     book_id INT,
                                     transaction_type VARCHAR(20),  -- 'borrow' or 'return'
                                     transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Now you can reference transaction_history table
SELECT book_id, COUNT(*) AS borrow_count FROM transaction_history WHERE transaction_type = 'borrow' GROUP BY book_id ORDER BY borrow_count DESC LIMIT 5;
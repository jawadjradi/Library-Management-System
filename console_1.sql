CREATE TABLE transaction_history (
                                     id INT PRIMARY KEY AUTO_INCREMENT,
                                     user_id INT,
                                     book_id INT,
                                     transaction_type VARCHAR(20),  -- 'borrow' or 'return'
                                     transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

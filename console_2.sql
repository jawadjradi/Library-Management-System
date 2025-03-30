CREATE TABLE users (
             id INT PRIMARY KEY AUTO_INCREMENT,
             username VARCHAR(255) UNIQUE,
             password VARCHAR(255),
             role VARCHAR(20) DEFAULT 'user'
);

CREATE DATABASE IF  stocks_db;
USE stocks_db;

CREATE TABLE IF NOT EXISTS utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS article (
    reference INT PRIMARY KEY AUTO_INCREMENT,
    designation VARCHAR(100) NOT NULL,
    puHt DOUBLE NOT NULL,
    qteStock INT DEFAULT 0,
    created_by INT,
    FOREIGN KEY (created_by) REFERENCES utilisateur(id)
);
-- Optionnel : Insérer un utilisateur de test (mot de passe: admin123)
INSERT INTO utilisateur (username, password, email) VALUES
    ('admin', 'admin123', 'admin@example.com');

-- Optionnel : Insérer quelques données de test
INSERT INTO article (designation, puHt, qteStock, created_by) VALUES
    ('Article Test 1', 19.99, 10, 1),
    ('Article Test 2', 29.99, 5, 1);

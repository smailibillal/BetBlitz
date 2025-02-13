-- Création de la base de données
DROP DATABASE IF EXISTS stocks_db;
CREATE DATABASE stocks_db;
USE stocks_db;

-- Création de la table utilisateurs
CREATE TABLE utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
    capital DECIMAL(10,2) DEFAULT 100.00,
    age INT,
    pays VARCHAR(50)
);

-- Création de la table paris
CREATE TABLE pari (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id INT,
    home_team VARCHAR(100) NOT NULL,
    away_team VARCHAR(100) NOT NULL,
    type VARCHAR(10) NOT NULL, -- '1', 'N', '2'
    cote DECIMAL(5,2) NOT NULL,
    montant DECIMAL(10,2) NOT NULL,
    date_pari DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(20) DEFAULT 'EN_ATTENTE', -- 'EN_ATTENTE', 'GAGNE', 'PERDU'
    dans_corbeille BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id)
);

-- Insertion d'utilisateurs de test
INSERT INTO utilisateur (username, password, email, date_creation, capital, age, pays) VALUES
('JohnDoe', 'motdepasse123', 'john.doe@email.com', '2023-12-01 10:00:00', 2500.50, 28, 'France'),
('AlicePro', 'alice123', 'alice.pro@email.com', '2023-12-05 14:30:00', 4800.75, 32, 'Belgique'),
('MaxParieur', 'maxmax', 'max.parieur@email.com', '2023-12-10 09:15:00', 150.25, 25, 'France'),
('EmmaWin', 'emma456', 'emma.win@email.com', '2023-12-15 16:45:00', 3200.00, 30, 'Suisse'),
('LucasBot', 'lucas789', 'lucas.bot@email.com', '2023-12-20 11:20:00', 50.00, 22, 'France'),
('SophieTop', 'sophie123', 'sophie.top@email.com', '2024-01-05 13:10:00', 1800.50, 27, 'France'),
('TomPro', 'tompro123', 'tom.pro@email.com', '2024-01-10 15:30:00', 5200.75, 35, 'Belgique'),
('LeaParis', 'lea456', 'lea.paris@email.com', '2024-01-15 10:45:00', 750.25, 29, 'France'),
('MarcoWin', 'marco789', 'marco.win@email.com', '2024-01-20 14:20:00', 2900.00, 31, 'Italie'),
('JulieBet', 'julie123', 'julie.bet@email.com', '2024-01-25 09:15:00', 1200.50, 26, 'France');

-- Insertion de paris de test
INSERT INTO pari (utilisateur_id, home_team, away_team, type, cote, montant, date_pari, statut) VALUES
-- Paris gagnés
(1, 'PSG', 'Marseille', '1', 1.85, 100.00, '2024-01-01 20:00:00', 'GAGNE'),
(1, 'Lyon', 'Monaco', 'N', 3.20, 50.00, '2024-01-05 20:00:00', 'GAGNE'),
(2, 'Real Madrid', 'Barcelona', '2', 2.50, 200.00, '2024-01-10 21:00:00', 'GAGNE'),
(2, 'Manchester City', 'Liverpool', '1', 1.95, 150.00, '2024-01-15 17:30:00', 'GAGNE'),
(3, 'Bayern Munich', 'Dortmund', '1', 1.75, 30.00, '2024-01-20 20:30:00', 'GAGNE'),

-- Paris perdus
(1, 'Inter', 'Milan', '1', 2.10, 75.00, '2024-01-25 20:45:00', 'PERDU'),
(2, 'Ajax', 'PSV', '2', 2.30, 100.00, '2024-01-28 16:30:00', 'PERDU'),
(3, 'Porto', 'Benfica', 'N', 3.10, 25.00, '2024-01-30 21:15:00', 'PERDU'),
(4, 'Juventus', 'Napoli', '1', 1.90, 120.00, '2024-02-01 20:45:00', 'PERDU'),
(5, 'Arsenal', 'Chelsea', '2', 2.40, 40.00, '2024-02-03 18:30:00', 'PERDU'),

-- Paris en attente
(1, 'Lille', 'Nice', '1', 2.15, 80.00, '2024-02-05 21:00:00', 'EN_ATTENTE'),
(2, 'Lens', 'Rennes', 'N', 3.30, 120.00, '2024-02-07 19:00:00', 'EN_ATTENTE'),
(3, 'Atletico Madrid', 'Sevilla', '2', 2.60, 35.00, '2024-02-09 21:00:00', 'EN_ATTENTE'),
(4, 'Manchester United', 'Tottenham', '1', 1.85, 150.00, '2024-02-11 17:30:00', 'EN_ATTENTE'),
(5, 'Leipzig', 'Leverkusen', 'N', 3.40, 25.00, '2024-02-13 20:30:00', 'EN_ATTENTE'),

-- Paris dans la corbeille
(1, 'Roma', 'Lazio', '1', 2.20, 90.00, '2024-01-02 20:45:00', 'PERDU', TRUE),
(2, 'Feyenoord', 'AZ Alkmaar', '2', 2.45, 80.00, '2024-01-08 16:45:00', 'GAGNE', TRUE),
(3, 'Sporting', 'Braga', 'N', 3.15, 20.00, '2024-01-12 21:15:00', 'PERDU', TRUE);

-- Ajout de plus de paris pour certains utilisateurs actifs
INSERT INTO pari (utilisateur_id, home_team, away_team, type, cote, montant, date_pari, statut) VALUES
(1, 'Valencia', 'Villarreal', '1', 2.10, 100.00, '2024-02-14 21:00:00', 'EN_ATTENTE'),
(1, 'AC Milan', 'AS Roma', '2', 2.50, 150.00, '2024-02-15 20:45:00', 'EN_ATTENTE'),
(2, 'Dortmund', 'Frankfurt', '1', 1.85, 200.00, '2024-02-16 20:30:00', 'EN_ATTENTE'),
(2, 'Lyon', 'PSG', '2', 1.95, 180.00, '2024-02-17 21:00:00', 'EN_ATTENTE'),
(3, 'Barcelona', 'Atletico Madrid', 'N', 3.20, 40.00, '2024-02-18 21:00:00', 'EN_ATTENTE');

-- Mise à jour des capitaux des utilisateurs en fonction de leurs paris
UPDATE utilisateurs SET capital = 2500.50 WHERE id = 1;
UPDATE utilisateurs SET capital = 4800.75 WHERE id = 2;
UPDATE utilisateurs SET capital = 150.25 WHERE id = 3;
UPDATE utilisateurs SET capital = 3200.00 WHERE id = 4;
UPDATE utilisateurs SET capital = 50.00 WHERE id = 5; 
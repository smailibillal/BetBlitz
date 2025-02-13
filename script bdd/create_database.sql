DROP DATABASE IF EXISTS stocks_db;
CREATE DATABASE stocks_db;
USE stocks_db;



-- Suppression des tables si elles existent
DROP TABLE IF EXISTS pari;
DROP TABLE IF EXISTS utilisateur;

-- Création de la table utilisateur (au singulier)
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

-- Création de la table pari (au singulier)
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
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
);

-- Insertion des utilisateurs de la classe
INSERT INTO utilisateur (username, password, email, date_creation, capital, age, pays) VALUES
('billal', 'billal123', 'billal@betblitz.fr', '2024-01-15 10:00:00', 1500.00, 20, 'France'),
('coumba', 'coumba123', 'coumba@betblitz.fr', '2024-01-15 10:05:00', 2000.00, 20, 'France'),
('abderrahman', 'abderrahman123', 'abderrahman@betblitz.fr', '2024-01-15 10:10:00', 1800.00, 20, 'France'),
('sylia', 'sylia123', 'sylia@betblitz.fr', '2024-01-15 10:15:00', 2500.00, 20, 'France'),
('lyes', 'lyes123', 'lyes@betblitz.fr', '2024-01-15 10:20:00', 1700.00, 20, 'France'),
('mahdi', 'mahdi123', 'mahdi@betblitz.fr', '2024-01-15 10:25:00', 1900.00, 20, 'France'),
('wafa', 'wafa123', 'wafa@betblitz.fr', '2024-01-15 10:30:00', 2200.00, 20, 'France'),
('assil', 'assil123', 'assil@betblitz.fr', '2024-01-15 10:35:00', 1600.00, 20, 'France');

-- Insertion de paris variés pour chaque utilisateur
INSERT INTO pari (utilisateur_id, home_team, away_team, type, cote, montant, date_pari, statut) VALUES
-- Paris de Billal (fan du Barça, parie gros sur son équipe)
(1, 'Barcelona', 'Real Madrid', '1', 2.10, 300.00, '2024-02-01 20:00:00', 'GAGNE'),
(1, 'Barcelona', 'Atletico Madrid', '1', 1.90, 250.00, '2024-01-15 21:00:00', 'GAGNE'),
(1, 'Barcelona', 'Sevilla', '1', 1.75, 200.00, '2024-01-20 21:00:00', 'PERDU'),
(1, 'Girona', 'Barcelona', '2', 1.85, 350.00, '2024-01-25 21:00:00', 'GAGNE'),
(1, 'Barcelona', 'Athletic Bilbao', '1', 1.70, 400.00, '2024-02-05 20:00:00', 'EN_ATTENTE'),

-- Paris de Coumba (préfère les paris nuls avec grosses cotes)
(2, 'Real Madrid', 'Barcelona', 'N', 3.50, 150.00, '2024-02-01 21:00:00', 'PERDU'),
(2, 'Manchester City', 'Liverpool', 'N', 3.40, 100.00, '2024-01-15 17:30:00', 'GAGNE'),
(2, 'Inter', 'Milan', 'N', 3.30, 200.00, '2024-01-20 20:45:00', 'GAGNE'),
(2, 'Juventus', 'Napoli', 'N', 3.20, 150.00, '2024-01-25 20:45:00', 'PERDU'),
(2, 'Roma', 'Lazio', 'N', 3.25, 180.00, '2024-02-05 20:45:00', 'EN_ATTENTE'),

-- Paris d'Abderrahman (paris variés avec analyse)
(3, 'Bayern Munich', 'Dortmund', '1', 1.75, 200.00, '2024-02-01 20:30:00', 'GAGNE'),
(3, 'Arsenal', 'Chelsea', '2', 2.80, 150.00, '2024-01-15 18:30:00', 'GAGNE'),
(3, 'Atletico Madrid', 'Sevilla', 'N', 3.10, 100.00, '2024-01-20 21:00:00', 'PERDU'),
(3, 'Porto', 'Benfica', '1', 2.20, 180.00, '2024-01-25 21:15:00', 'GAGNE'),
(3, 'Ajax', 'PSV', '2', 2.40, 160.00, '2024-02-05 16:30:00', 'EN_ATTENTE'),

-- Paris de Sylia (petits montants mais réguliers)
(4, 'Arsenal', 'Chelsea', '1', 2.40, 50.00, '2024-02-01 18:30:00', 'GAGNE'),
(4, 'Liverpool', 'Tottenham', '1', 1.95, 50.00, '2024-01-15 17:00:00', 'GAGNE'),
(4, 'Newcastle', 'Aston Villa', '2', 2.70, 50.00, '2024-01-20 16:00:00', 'PERDU'),
(4, 'Brighton', 'West Ham', 'N', 3.30, 50.00, '2024-01-25 20:45:00', 'GAGNE'),
(4, 'Crystal Palace', 'Brentford', '1', 2.10, 50.00, '2024-02-05 21:00:00', 'EN_ATTENTE'),

-- Paris de Lyes (fan de l'OM, paris risqués)
(5, 'Marseille', 'PSG', '1', 3.50, 200.00, '2024-02-01 21:00:00', 'PERDU'),
(5, 'Marseille', 'Lyon', '1', 2.20, 250.00, '2024-01-15 21:00:00', 'GAGNE'),
(5, 'Marseille', 'Monaco', '1', 2.15, 300.00, '2024-01-20 21:00:00', 'GAGNE'),
(5, 'Marseille', 'Lens', '1', 2.30, 200.00, '2024-01-25 21:00:00', 'PERDU'),
(5, 'Marseille', 'Lille', '1', 2.25, 250.00, '2024-02-05 21:00:00', 'EN_ATTENTE'),

-- Paris de Mahdi (stratégie sur les petites cotes)
(6, 'Manchester City', 'Burnley', '1', 1.20, 500.00, '2024-02-01 21:00:00', 'GAGNE'),
(6, 'Bayern Munich', 'Union Berlin', '1', 1.25, 400.00, '2024-01-15 20:30:00', 'GAGNE'),
(6, 'PSG', 'Strasbourg', '1', 1.30, 450.00, '2024-01-20 21:00:00', 'GAGNE'),
(6, 'Barcelona', 'Almeria', '1', 1.15, 600.00, '2024-01-25 21:00:00', 'PERDU'),
(6, 'Real Madrid', 'Getafe', '1', 1.22, 500.00, '2024-02-05 21:00:00', 'EN_ATTENTE'),

-- Paris de Wafa (paris combinés domicile/extérieur)
(7, 'Porto', 'Benfica', '2', 2.60, 130.00, '2024-02-01 21:15:00', 'PERDU'),
(7, 'Sporting', 'Braga', '2', 2.80, 120.00, '2024-01-15 21:15:00', 'GAGNE'),
(7, 'Feyenoord', 'Ajax', '2', 2.50, 140.00, '2024-01-20 16:30:00', 'PERDU'),
(7, 'PSV', 'AZ Alkmaar', '1', 1.90, 150.00, '2024-01-25 20:00:00', 'GAGNE'),
(7, 'Twente', 'Utrecht', '2', 2.70, 130.00, '2024-02-05 14:30:00', 'EN_ATTENTE'),

-- Paris d'Assil (paris sur les grands championnats)
(8, 'Roma', 'Lazio', '1', 2.20, 140.00, '2024-02-01 20:45:00', 'GAGNE'),
(8, 'Inter', 'Juventus', 'N', 3.10, 120.00, '2024-01-15 20:45:00', 'PERDU'),
(8, 'Milan', 'Napoli', '2', 2.90, 130.00, '2024-01-20 20:45:00', 'GAGNE'),
(8, 'Atalanta', 'Fiorentina', '1', 1.95, 150.00, '2024-01-25 18:30:00', 'GAGNE'),
(8, 'Lazio', 'Bologna', 'N', 3.20, 140.00, '2024-02-05 20:45:00', 'EN_ATTENTE');

-- Ajout de paris dans la corbeille
INSERT INTO pari (utilisateur_id, home_team, away_team, type, cote, montant, date_pari, statut, dans_corbeille) VALUES
(1, 'Barcelona', 'Valencia', '1', 1.65, 150.00, '2024-01-10 20:00:00', 'PERDU', TRUE),
(1, 'Real Sociedad', 'Barcelona', '2', 2.20, 200.00, '2024-01-12 21:00:00', 'GAGNE', TRUE),
(2, 'Rennes', 'Brest', 'N', 3.20, 80.00, '2024-01-12 21:00:00', 'GAGNE', TRUE),
(3, 'Toulouse', 'Reims', '2', 2.50, 120.00, '2024-01-14 15:00:00', 'PERDU', TRUE),
(4, 'Lorient', 'Clermont', '1', 2.30, 90.00, '2024-01-16 19:00:00', 'PERDU', TRUE),
(5, 'Metz', 'Le Havre', 'N', 3.10, 100.00, '2024-01-18 20:00:00', 'GAGNE', TRUE);

-- Mise à jour des capitaux en fonction des résultats
UPDATE utilisateur SET capital = 3100.50 WHERE id = 1; -- Billal (gros gains sur le Barça)
UPDATE utilisateur SET capital = 2450.75 WHERE id = 2; -- Coumba (gains sur nuls)
UPDATE utilisateur SET capital = 2100.25 WHERE id = 3; -- Abderrahman (analyse payante)
UPDATE utilisateur SET capital = 2750.00 WHERE id = 4; -- Sylia (progression régulière)
UPDATE utilisateur SET capital = 1950.50 WHERE id = 5; -- Lyes (pertes sur l'OM)
UPDATE utilisateur SET capital = 3200.75 WHERE id = 6; -- Mahdi (stratégie efficace)
UPDATE utilisateur SET capital = 1800.25 WHERE id = 7; -- Wafa (résultats mitigés)
UPDATE utilisateur SET capital = 2100.00 WHERE id = 8; -- Assil (dans la moyenne) 
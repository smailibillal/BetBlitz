import java.sql.*;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

public class UtilisateurDAO {
    private Connection connexion;

    public UtilisateurDAO(Connection connexion) {
        this.connexion = connexion;
    }

    public Utilisateur authentifier(String username, String password) throws SQLException {
        String sql = "SELECT * FROM utilisateur WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Utilisateur(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("date_creation"),
                    rs.getDouble("capital")
                );
            }
            return null;
        }
    }

    public boolean creerUtilisateur(Utilisateur utilisateur) throws SQLException {
        String sql = "INSERT INTO utilisateur (username, password, email, age, pays) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getUsername());
            pstmt.setString(2, utilisateur.getPassword());
            pstmt.setString(3, utilisateur.getEmail());
            
            // Gestion des champs optionnels
            if (utilisateur.getAge() != null) {
                pstmt.setInt(4, utilisateur.getAge());
            } else {
                pstmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            if (utilisateur.getPays() != null) {
                pstmt.setString(5, utilisateur.getPays());
            } else {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            }
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean usernameExiste(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE username = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public void mettreAJourCapital(int userId, double nouveauCapital) throws SQLException {
        String sql = "UPDATE utilisateur SET capital = ? WHERE id = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setDouble(1, nouveauCapital);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }
    
    public boolean placerPari(int userId, double montant, String type, String cote, int matchId, String homeTeam, String awayTeam) throws SQLException {
        System.out.println("Tentative de pari - UserId: " + userId + ", Montant: " + montant + ", Type: " + type + ", Cote: " + cote + ", MatchId: " + matchId);
        System.out.println("Équipes : " + homeTeam + " vs " + awayTeam);
        
        // Vérifier le capital disponible
        String sqlCapital = "SELECT capital FROM utilisateur WHERE id = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sqlCapital)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double capitalActuel = rs.getDouble("capital");
                System.out.println("Capital actuel: " + capitalActuel);
                
                if (capitalActuel < montant) {
                    System.out.println("Capital insuffisant");
                    return false;
                }
                
                // Créer ou mettre à jour le match avec la bonne colonne date_match
                String sqlMatch = """
                    INSERT INTO `match` (id, competition, home_team, away_team, date_match, status, score_home, score_away) 
                    VALUES (?, NULL, ?, ?, NOW(), 'SCHEDULED', NULL, NULL) 
                    ON DUPLICATE KEY UPDATE 
                    home_team = ?, 
                    away_team = ?
                """;
                
                try (PreparedStatement matchStmt = connexion.prepareStatement(sqlMatch)) {
                    matchStmt.setInt(1, matchId);
                    matchStmt.setString(2, homeTeam);
                    matchStmt.setString(3, awayTeam);
                    matchStmt.setString(4, homeTeam);
                    matchStmt.setString(5, awayTeam);
                    matchStmt.executeUpdate();
                    System.out.println("Match créé/mis à jour avec succès - ID: " + matchId);
                }
                
                // Enregistrer le pari
                String sqlPari = "INSERT INTO pari (utilisateur_id, match_id, type, cote, montant, date_pari, statut) VALUES (?, ?, ?, ?, ?, NOW(), 'EN_ATTENTE')";
                try (PreparedStatement pstmtPari = connexion.prepareStatement(sqlPari)) {
                    pstmtPari.setInt(1, userId);
                    pstmtPari.setInt(2, matchId);
                    pstmtPari.setString(3, type);
                    pstmtPari.setDouble(4, Double.parseDouble(cote));
                    pstmtPari.setDouble(5, montant);
                    pstmtPari.executeUpdate();
                    
                    // Mettre à jour le capital
                    mettreAJourCapital(userId, capitalActuel - montant);
                    System.out.println("Pari placé avec succès");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du placement du pari: " + e.getMessage());
            throw e;
        }
        return false;
    }

    public JSONObject getStatistiquesParis(int userId) throws SQLException {
        JSONObject stats = new JSONObject();
        
        String sql = """
            SELECT 
                COUNT(CASE WHEN statut = 'EN_ATTENTE' THEN 1 END) as paris_en_cours,
                COUNT(CASE WHEN statut = 'GAGNE' THEN 1 END) as paris_gagnes,
                SUM(CASE WHEN statut = 'GAGNE' THEN montant * cote - montant ELSE 0 END) as gains_totaux,
                SUM(montant) as mises_totales
            FROM pari 
            WHERE utilisateur_id = ?
        """;
        
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                stats.put("parisEnCours", rs.getInt("paris_en_cours"));
                stats.put("parisGagnes", rs.getInt("paris_gagnes"));
                stats.put("gainsTotaux", rs.getDouble("gains_totaux"));
                double mises = rs.getDouble("mises_totales");
                double roi = mises > 0 ? (rs.getDouble("gains_totaux") / mises) * 100 : 0;
                stats.put("roi", roi);
            }
        }
        
        return stats;
    }

    public List<Pari> getParis(int userId) throws SQLException {
        List<Pari> paris = new ArrayList<>();
        
        String sql = """
            SELECT p.*, 
                   COALESCE(m.home_team, 'Équipe 1') as home_team, 
                   COALESCE(m.away_team, 'Équipe 2') as away_team,
                   p.dans_corbeille
            FROM pari p 
            LEFT JOIN `match` m ON p.match_id = m.id 
            WHERE p.utilisateur_id = ? 
            ORDER BY p.date_pari DESC
        """;
        
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                paris.add(new Pari(
                    rs.getInt("id"),
                    rs.getString("home_team"),
                    rs.getString("away_team"),
                    rs.getString("type"),
                    rs.getDouble("cote"),
                    rs.getDouble("montant"),
                    rs.getString("date_pari"),
                    rs.getString("statut"),
                    rs.getBoolean("dans_corbeille")
                ));
            }
        }
        return paris;
    }

    public List<ClassementUtilisateur> getClassement() throws SQLException {
        List<ClassementUtilisateur> classement = new ArrayList<>();
        
        String sql = """
            SELECT 
                u.username,
                COUNT(p.id) as total_paris,
                COUNT(CASE WHEN p.statut = 'GAGNE' THEN 1 END) as paris_gagnes,
                COALESCE(SUM(CASE 
                    WHEN p.statut = 'GAGNE' THEN p.montant * p.cote - p.montant 
                    WHEN p.statut = 'PERDU' THEN -p.montant 
                    ELSE 0 
                END), 0) as gain_total,
                u.capital
            FROM utilisateur u
            LEFT JOIN pari p ON u.id = p.utilisateur_id
            GROUP BY u.id, u.username, u.capital
            ORDER BY 
                COALESCE(COUNT(CASE WHEN p.statut = 'GAGNE' THEN 1 END) * 100.0 / 
                    NULLIF(COUNT(p.id), 0), 0) DESC,
                gain_total DESC
        """;
        
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                classement.add(new ClassementUtilisateur(
                    rs.getString("username"),
                    rs.getInt("total_paris"),
                    rs.getInt("paris_gagnes"),
                    rs.getDouble("gain_total"),
                    rs.getDouble("capital")
                ));
            }
        }
        
        return classement;
    }

    public void mettreAuCorbeille(int pariId) throws SQLException {
        String sql = "UPDATE pari SET dans_corbeille = TRUE WHERE id = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setInt(1, pariId);
            pstmt.executeUpdate();
        }
    }

    public void restaurerDeLaCorbeille(int pariId) throws SQLException {
        String sql = "UPDATE pari SET dans_corbeille = FALSE WHERE id = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setInt(1, pariId);
            pstmt.executeUpdate();
        }
    }

    public ClassementUtilisateur getStatistiquesUtilisateur(int userId) throws SQLException {
        String sql = """
            SELECT 
                u.username,
                COUNT(p.id) as total_paris,
                SUM(CASE WHEN p.statut = 'GAGNE' THEN 1 ELSE 0 END) as paris_gagnes,
                COALESCE(SUM(CASE WHEN p.statut = 'GAGNE' THEN p.montant * p.cote - p.montant ELSE -p.montant END), 0) as gain_total,
                u.capital as capital_actuel
            FROM utilisateur u
            LEFT JOIN pari p ON u.id = p.utilisateur_id
            WHERE u.id = ?
            GROUP BY u.id, u.username, u.capital
        """;

        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new ClassementUtilisateur(
                    rs.getString("username"),
                    rs.getInt("total_paris"),
                    rs.getInt("paris_gagnes"),
                    rs.getDouble("gain_total"),
                    rs.getDouble("capital_actuel")
                );
            } else {
                // D'abord récupérer les informations de base de l'utilisateur
                String userSql = "SELECT username, capital FROM utilisateur WHERE id = ?";
                try (PreparedStatement userStmt = connexion.prepareStatement(userSql)) {
                    userStmt.setInt(1, userId);
                    ResultSet userRs = userStmt.executeQuery();
                    
                    if (userRs.next()) {
                        return new ClassementUtilisateur(
                            userRs.getString("username"),
                            0,
                            0,
                            0.0,
                            userRs.getDouble("capital")
                        );
                    }
                }
                // Si on ne trouve pas l'utilisateur, retourner null ou lancer une exception
                throw new SQLException("Utilisateur non trouvé");
            }
        }
    }
}

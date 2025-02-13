import java.sql.*;
import java.util.concurrent.*;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;

public class GestionnaireParis {
    private final Connection connexion;
    private final ScheduledExecutorService scheduler;
    
    public GestionnaireParis(Connection connexion) {
        this.connexion = connexion;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    public void demarrer() {
        // Vérifier les résultats toutes les heures
        scheduler.scheduleAtFixedRate(
            this::verifierResultatsMatchs,
            0, 1, TimeUnit.HOURS
        );
    }
    
    private void verifierResultatsMatchs() {
        String sql = """
            SELECT p.*, m.* 
            FROM pari p 
            JOIN `match` m ON p.match_id = m.id 
            WHERE p.statut = 'EN_ATTENTE'
        """;
        
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                int matchId = rs.getInt("match_id");
                JSONObject matchDetails = ApiFootball.getMatch(matchId);
                
                if (matchDetails.getString("status").equals("FINISHED")) {
                    mettreAJourPari(
                        rs.getInt("id"),
                        rs.getInt("utilisateur_id"),
                        rs.getString("type"),
                        rs.getDouble("montant"),
                        rs.getDouble("cote"),
                        matchDetails
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void mettreAJourPari(int pariId, int userId, String type, double montant, double cote, JSONObject match) {
        String resultat = determinerResultat(type, match);
        boolean estGagnant = resultat.equals("GAGNE");
        
        try {
            // Mettre à jour le statut du pari
            String sqlPari = "UPDATE pari SET statut = ? WHERE id = ?";
            try (PreparedStatement pstmt = connexion.prepareStatement(sqlPari)) {
                pstmt.setString(1, resultat);
                pstmt.setInt(2, pariId);
                pstmt.executeUpdate();
            }
            
            // Si le pari est gagnant, créditer l'utilisateur
            if (estGagnant) {
                double gain = montant * cote;
                String sqlUser = "UPDATE utilisateur SET capital = capital + ? WHERE id = ?";
                try (PreparedStatement pstmt = connexion.prepareStatement(sqlUser)) {
                    pstmt.setDouble(1, gain);
                    pstmt.setInt(2, userId);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String determinerResultat(String type, JSONObject match) {
        int scoreHome = match.getInt("scoreHome");
        int scoreAway = match.getInt("scoreAway");
        
        switch (type) {
            case "1": return scoreHome > scoreAway ? "GAGNE" : "PERDU";
            case "N": return scoreHome == scoreAway ? "GAGNE" : "PERDU";
            case "2": return scoreHome < scoreAway ? "GAGNE" : "PERDU";
            default: return "PERDU";
        }
    }
    
    public boolean verifierValiditePari(String type, double montant, double cote) {
        System.out.println("Vérification du pari - Type: " + type + ", Montant: " + montant + ", Cote: " + cote);
        
        // Vérifier que le type est valide (ajout de la vérification explicite pour "N")
        if (!type.equals("1") && !type.equals("N") && !type.equals("2")) {
            System.out.println("Type de pari invalide: " + type);
            return false;
        }
        
        // Vérifier que le montant est positif
        if (montant <= 0) {
            System.out.println("Montant invalide: " + montant);
            return false;
        }
        
        // Vérifier que la cote est valide
        if (cote <= 1.0) {
            System.out.println("Cote invalide: " + cote);
            return false;
        }
        
        return true;
    }
} 
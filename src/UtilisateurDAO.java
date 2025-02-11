import java.sql.*;

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
                    rs.getString("date_creation")
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
}

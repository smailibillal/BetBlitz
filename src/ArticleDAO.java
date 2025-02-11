import java.sql.*;
import javax.swing.table.DefaultTableModel;

/**
 * Classe d'accès aux données contenues dans la table article
 * @version 1.1
 * */
public class ArticleDAO {
    private Connection connexion;

    /**
     * Constructeur de la classe
     * @param connexion la connexion à la base de données
     */
    public ArticleDAO(Connection connexion) {
        this.connexion = connexion;
    }

    /**
     * Permet de récupérer un modèle de tableau pour afficher les articles
     * @return un modèle de tableau
     */
    public DefaultTableModel getTableModel() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rend la table non éditable
            }
        };
        
        model.addColumn("Référence");
        model.addColumn("Désignation");
        model.addColumn("Prix HT");
        model.addColumn("Quantité");
        model.addColumn("Créé par");

        try {
            String sql = "SELECT a.*, u.username FROM article a LEFT JOIN utilisateur u ON a.created_by = u.id";
            try (Statement stmt = connexion.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("reference"),
                        rs.getString("designation"),
                        rs.getDouble("puHt"),
                        rs.getInt("qteStock"),
                        rs.getString("username")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return model;
    }

    /**
     * Permet d'ajouter un article dans la table article
     * la référence de l'article est produite automatiquement par la base de données en utilisant une séquence
     * Le mode est auto-commit par défaut : chaque insertion est validée
     * @param article l'article à ajouter
     * @return true si l'article a été ajouté, false sinon
     */
    public boolean ajouter(Article article) {
        String sql = "INSERT INTO article (designation, puHt, qteStock, created_by) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setString(1, article.getDesignation());
            pstmt.setDouble(2, article.getPuHt());
            pstmt.setInt(3, article.getQteStock());
            pstmt.setInt(4, article.getCreatedBy());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permet de modifier un article dans la table article
     * @param article l'article à modifier
     * @return true si l'article a été modifié, false sinon
     */
    public boolean modifier(Article article) {
        String sql = "UPDATE article SET designation = ?, puHt = ?, qteStock = ? WHERE reference = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setString(1, article.getDesignation());
            pstmt.setDouble(2, article.getPuHt());
            pstmt.setInt(3, article.getQteStock());
            pstmt.setInt(4, article.getReference());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permet de supprimer un article dans la table article
     * @param reference la référence de l'article à supprimer
     * @return true si l'article a été supprimé, false sinon
     */
    public boolean supprimer(int reference) {
        String sql = "DELETE FROM article WHERE reference = ?";
        try (PreparedStatement pstmt = connexion.prepareStatement(sql)) {
            pstmt.setInt(1, reference);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

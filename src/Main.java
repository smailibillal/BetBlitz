import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        try {
            // Set Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Créer la fenêtre de connexion mais ne pas l'afficher tout de suite
            LoginFenetre loginFenetre = new LoginFenetre(null);  // On passera la connexion plus tard
            
            // Afficher le splash screen avec la référence à la fenêtre de connexion
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
            
            // Vérifier que le driver est disponible
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null,
                    "Driver MySQL introuvable. Vérifiez que mysql-connector-j est dans le dossier lib/",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Établir la connexion à la base de données
            try {
                Connection connexion = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/stocks_db?serverTimezone=UTC",
                    "root",
                    ""
                );
                
                // Mettre à jour la connexion dans la fenêtre de login
                loginFenetre.setConnection(connexion);
                
                // Démarrer le splash screen avec la référence à la fenêtre de connexion
                splash.startProgress(loginFenetre);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erreur de connexion à la base de données: " + e.getMessage() + 
                    "\nVérifiez que MySQL est démarré et que la base stocks_db existe.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

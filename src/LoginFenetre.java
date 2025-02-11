import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginFenetre extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UtilisateurDAO utilisateurDAO;
    private static Utilisateur utilisateurConnecte;
    private static final int MIN_WIDTH = 350;
    private static final int MIN_HEIGHT = 500;
    private static final int PREFERRED_WIDTH = 400;
    private static final int PREFERRED_HEIGHT = 600;

    public LoginFenetre(Connection connexion) {
        this.utilisateurDAO = new UtilisateurDAO(connexion);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        initialiserComposants();
        setTitle("BetBlitz - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250));
    }

    private void initialiserComposants() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Logo
        ImageIcon originalIcon = new ImageIcon("src/img/logo.png");
        int logoWidth = Math.min(180, getWidth() / 2);
        Image originalImage = originalIcon.getImage();
        double ratio = (double) originalIcon.getIconHeight() / originalIcon.getIconWidth();
        int logoHeight = (int) (logoWidth * ratio);
        
        Image resizedImage = originalImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        
        JLabel logoLabel = new JLabel(resizedIcon);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(logoLabel, gbc);

        // Panel pour les champs de saisie
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(mainPanel.getBackground());
        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.insets = new Insets(5, 10, 5, 10);
        fieldGbc.weightx = 1.0;

        // Champs de connexion avec taille minimale
        usernameField = createStyledTextField("Nom d'utilisateur");
        passwordField = createStyledPasswordField("Mot de passe");
        
        // Labels avec style amélioré
        JLabel userLabel = createStyledLabel("Nom d'utilisateur");
        JLabel passLabel = createStyledLabel("Mot de passe");

        // Ajout des composants au panel de champs
        fieldGbc.gridx = 0;
        fieldGbc.gridy = 0;
        fieldsPanel.add(userLabel, fieldGbc);
        fieldGbc.gridy = 1;
        fieldsPanel.add(usernameField, fieldGbc);
        fieldGbc.gridy = 2;
        fieldsPanel.add(passLabel, fieldGbc);
        fieldGbc.gridy = 3;
        fieldsPanel.add(passwordField, fieldGbc);

        // Ajout du panel de champs
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 15, 10, 15);
        mainPanel.add(fieldsPanel, gbc);

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        buttonPanel.setBackground(mainPanel.getBackground());
        
        JButton loginButton = createStyledButton("Se connecter", new Color(79, 70, 229));
        JButton signupButton = createStyledButton("Créer un compte", new Color(16, 185, 129));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        gbc.gridy = 3;
        gbc.insets = new Insets(5, 15, 15, 15);
        mainPanel.add(buttonPanel, gbc);

        // Actions
        loginButton.addActionListener(e -> seConnecter());
        signupButton.addActionListener(e -> ouvrirInscription());

        // Ajout d'un listener pour le redimensionnement
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateComponentSizes();
            }
        });

        setContentPane(mainPanel);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(55, 65, 81));
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 40));
        field.setMinimumSize(new Dimension(180, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 40));
        field.setMinimumSize(new Dimension(180, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 40));
        button.setMinimumSize(new Dimension(150, 35));
        return button;
    }

    private void updateComponentSizes() {
        int width = getWidth();
        int height = getHeight();
        
        // Ajuster les tailles des composants en fonction de la taille de la fenêtre
        int fieldWidth = Math.max(180, Math.min(300, width - 100));
        usernameField.setPreferredSize(new Dimension(fieldWidth, 40));
        passwordField.setPreferredSize(new Dimension(fieldWidth, 40));
        
        revalidate();
        repaint();
    }

    private void seConnecter() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            Utilisateur utilisateur = utilisateurDAO.authentifier(username, password);
            if (utilisateur != null) {
                utilisateurConnecte = utilisateur;
                new DashboardFenetre(utilisateur);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Nom d'utilisateur ou mot de passe incorrect",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la connexion: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ouvrirInscription() {
        new SignupFenetre(utilisateurDAO);
    }

    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void setConnection(Connection connexion) {
        this.utilisateurDAO = new UtilisateurDAO(connexion);
    }
}

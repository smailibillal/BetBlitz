import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SignupFenetre extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField ageField;
    private JComboBox<String> paysComboBox;
    private UtilisateurDAO utilisateurDAO;
    private static final int MIN_WIDTH = 350;
    private static final int MIN_HEIGHT = 600;
    private static final int PREFERRED_WIDTH = 400;
    private static final int PREFERRED_HEIGHT = 700;

    public SignupFenetre(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
        initialiserComposants();
        setTitle("BetBlitz - Inscription");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250));
        setVisible(true);
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

        // Création des champs
        usernameField = createStyledTextField();
        emailField = createStyledTextField();
        passwordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();
        ageField = createStyledTextField();
        
        // ComboBox des pays
        String[] pays = {
            "Sélectionnez un pays", "France", "Belgique", "Suisse", 
            "Canada", "Maroc", "Algérie", "Tunisie", "Sénégal"
        };
        paysComboBox = new JComboBox<>(pays);
        paysComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        paysComboBox.setBackground(Color.WHITE);
        ((JComponent) paysComboBox.getRenderer()).setOpaque(true);

        // Labels
        JLabel userLabel = createStyledLabel("Nom d'utilisateur *");
        JLabel emailLabel = createStyledLabel("Email *");
        JLabel passLabel = createStyledLabel("Mot de passe *");
        JLabel confirmPassLabel = createStyledLabel("Confirmer le mot de passe *");
        JLabel ageLabel = createStyledLabel("Âge (optionnel)");
        JLabel paysLabel = createStyledLabel("Pays (optionnel)");

        // Ajout des composants
        fieldGbc.gridx = 0;
        fieldGbc.gridy = 0;
        fieldsPanel.add(userLabel, fieldGbc);
        fieldGbc.gridy = 1;
        fieldsPanel.add(usernameField, fieldGbc);
        fieldGbc.gridy = 2;
        fieldsPanel.add(emailLabel, fieldGbc);
        fieldGbc.gridy = 3;
        fieldsPanel.add(emailField, fieldGbc);
        fieldGbc.gridy = 4;
        fieldsPanel.add(passLabel, fieldGbc);
        fieldGbc.gridy = 5;
        fieldsPanel.add(passwordField, fieldGbc);
        fieldGbc.gridy = 6;
        fieldsPanel.add(confirmPassLabel, fieldGbc);
        fieldGbc.gridy = 7;
        fieldsPanel.add(confirmPasswordField, fieldGbc);
        fieldGbc.gridy = 8;
        fieldsPanel.add(ageLabel, fieldGbc);
        fieldGbc.gridy = 9;
        fieldsPanel.add(ageField, fieldGbc);
        fieldGbc.gridy = 10;
        fieldsPanel.add(paysLabel, fieldGbc);
        fieldGbc.gridy = 11;
        fieldsPanel.add(paysComboBox, fieldGbc);

        // Ajout du panel de champs
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 15, 10, 15);
        mainPanel.add(fieldsPanel, gbc);

        // Boutons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        buttonPanel.setBackground(mainPanel.getBackground());
        
        JButton signupButton = createStyledButton("S'inscrire", new Color(16, 185, 129));
        JButton loginButton = createStyledButton("Retour à la connexion", new Color(79, 70, 229));
        
        buttonPanel.add(signupButton);
        buttonPanel.add(loginButton);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 15, 15, 15);
        mainPanel.add(buttonPanel, gbc);

        // Actions
        signupButton.addActionListener(e -> sInscrire());
        loginButton.addActionListener(e -> dispose());

        setContentPane(mainPanel);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(55, 65, 81));
        return label;
    }

    private JTextField createStyledTextField() {
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

    private JPasswordField createStyledPasswordField() {
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

    private void sInscrire() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String ageStr = ageField.getText().trim();
        String pays = paysComboBox.getSelectedIndex() == 0 ? null : 
                     (String) paysComboBox.getSelectedItem();

        // Validation des champs obligatoires
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Les champs marqués d'un * sont obligatoires",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validation de l'email
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Veuillez entrer une adresse email valide",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validation du mot de passe
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Les mots de passe ne correspondent pas",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validation de l'âge si renseigné
        Integer age = null;
        if (!ageStr.isEmpty()) {
            try {
                age = Integer.parseInt(ageStr);
                if (age < 18 || age > 120) {
                    JOptionPane.showMessageDialog(this,
                        "L'âge doit être compris entre 18 et 120 ans",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez entrer un âge valide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            // Créer un nouvel utilisateur avec un capital initial de 1000€
            Utilisateur nouvelUtilisateur = new Utilisateur(username, password, email, 1000.0);
            
            if (ageStr != null && !ageStr.isEmpty()) {
                nouvelUtilisateur.setAge(Integer.parseInt(ageStr));
            }
            if (pays != null) {
                nouvelUtilisateur.setPays(pays);
            }
            
            if (utilisateurDAO.creerUtilisateur(nouvelUtilisateur)) {
                JOptionPane.showMessageDialog(this,
                    "Inscription réussie ! Vous pouvez maintenant vous connecter.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'inscription",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'inscription: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

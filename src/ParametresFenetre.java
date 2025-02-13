import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;

public class ParametresFenetre extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color DANGER_COLOR = new Color(220, 38, 38);
    
    private final Utilisateur utilisateur;
    private final Connection connexion;
    private final UtilisateurDAO utilisateurDAO;

    public ParametresFenetre(JFrame parent, Utilisateur utilisateur, Connection connexion) {
        super(parent, "Paramètres", true);
        this.utilisateur = utilisateur;
        this.connexion = connexion;
        this.utilisateurDAO = new UtilisateurDAO(connexion);

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // En-tête
        mainPanel.add(createHeaderPanel());
        mainPanel.add(Box.createVerticalStrut(25));

        // Scroll pane pour gérer le contenu
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Paramètres de notification
        contentPanel.add(createNotificationsPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        // Paramètres de sécurité
        contentPanel.add(createSecurityPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        // Paramètres d'affichage
        contentPanel.add(createDisplayPanel());
        contentPanel.add(Box.createVerticalStrut(25));

        // Boutons d'action
        contentPanel.add(createActionPanel());

        // Ajout du scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        mainPanel.add(scrollPane);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Paramètres");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createNotificationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Notifications",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
            ),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Notifications par email
        JCheckBox emailNotifCheckbox = createStyledCheckbox("Recevoir les notifications par email");
        JLabel emailDescription = createDescriptionLabel("Recevez des mises à jour sur vos paris par email");

        // Notifications de paris
        JCheckBox betNotifCheckbox = createStyledCheckbox("Notifications des résultats de paris");
        JLabel betDescription = createDescriptionLabel("Soyez informé dès qu'un de vos paris est terminé");

        // Notifications promotionnelles
        JCheckBox promoNotifCheckbox = createStyledCheckbox("Recevoir les offres promotionnelles");
        JLabel promoDescription = createDescriptionLabel("Recevez des bonus et offres spéciales");

        // Ajout des composants avec espacement
        panel.add(emailNotifCheckbox);
        panel.add(emailDescription);
        panel.add(Box.createVerticalStrut(10));
        panel.add(betNotifCheckbox);
        panel.add(betDescription);
        panel.add(Box.createVerticalStrut(10));
        panel.add(promoNotifCheckbox);
        panel.add(promoDescription);

        return panel;
    }

    private JPanel createSecurityPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Sécurité",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
            ),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Bouton changement de mot de passe avec description
        JButton changePasswordButton = createStyledButton("Changer le mot de passe", PRIMARY_COLOR);
        JLabel passwordDescription = createDescriptionLabel("Modifiez votre mot de passe pour sécuriser votre compte");
        
        // Double authentification avec description
        JCheckBox twoFactorCheckbox = createStyledCheckbox("Activer la double authentification");
        JLabel twoFactorDescription = createDescriptionLabel("Ajoutez une couche de sécurité supplémentaire à votre compte");

        // Ajout des composants avec espacement
        panel.add(changePasswordButton);
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordDescription);
        panel.add(Box.createVerticalStrut(15));
        panel.add(twoFactorCheckbox);
        panel.add(Box.createVerticalStrut(5));
        panel.add(twoFactorDescription);

        return panel;
    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Affichage",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)
            ),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Langue
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        langPanel.setBackground(BACKGROUND_COLOR);
        JLabel langLabel = new JLabel("Langue :");
        langLabel.setFont(REGULAR_FONT);
        String[] languages = {"Français", "English", "Español"};
        JComboBox<String> langComboBox = new JComboBox<>(languages);
        langComboBox.setFont(REGULAR_FONT);
        langPanel.add(langLabel);
        langPanel.add(langComboBox);

        // Description de la langue
        JLabel langDescription = createDescriptionLabel("Choisissez la langue d'affichage de l'application");

        // Mode sombre
        JCheckBox darkModeCheckbox = createStyledCheckbox("Mode sombre");
        JLabel darkModeDescription = createDescriptionLabel("Activez le thème sombre pour réduire la fatigue visuelle");

        // Ajout des composants avec espacement
        panel.add(langPanel);
        panel.add(langDescription);
        panel.add(Box.createVerticalStrut(15));
        panel.add(darkModeCheckbox);
        panel.add(darkModeDescription);

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(BACKGROUND_COLOR);

        // Bouton Sauvegarder
        JButton saveButton = createStyledButton("Sauvegarder", new Color(16, 185, 129));
        saveButton.addActionListener(e -> sauvegarderParametres());

        // Bouton Supprimer le compte
        JButton deleteButton = createStyledButton("Supprimer le compte", DANGER_COLOR);
        deleteButton.addActionListener(e -> supprimerCompte());

        panel.add(saveButton);
        panel.add(deleteButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Effet hover
        Color darkerColor = color.darker();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(darkerColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private JCheckBox createStyledCheckbox(String text) {
        JCheckBox checkbox = new JCheckBox(text);
        checkbox.setFont(REGULAR_FONT);
        checkbox.setBackground(BACKGROUND_COLOR);
        checkbox.setFocusPainted(false);
        return checkbox;
    }

    private JLabel createDescriptionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(107, 114, 128));
        label.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 0));
        return label;
    }

    private void changerMotDePasse() {
        // TODO: Implémenter le changement de mot de passe
        JOptionPane.showMessageDialog(this,
            "Fonctionnalité de changement de mot de passe à venir",
            "En développement",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void sauvegarderParametres() {
        // TODO: Implémenter la sauvegarde des paramètres
        JOptionPane.showMessageDialog(this,
            "Paramètres sauvegardés avec succès",
            "Succès",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void supprimerCompte() {
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            // TODO: Implémenter la suppression du compte
            JOptionPane.showMessageDialog(this,
                "Fonctionnalité de suppression de compte à venir",
                "En développement",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 
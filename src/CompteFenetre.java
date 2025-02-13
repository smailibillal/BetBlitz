import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class CompteFenetre extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    private final Utilisateur utilisateur;
    private final Connection connexion;
    private final UtilisateurDAO utilisateurDAO;
    private final DecimalFormat df = new DecimalFormat("#,##0.00€");

    public CompteFenetre(JFrame parent, Utilisateur utilisateur, Connection connexion) {
        super(parent, "Mon Compte", true);
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // En-tête
        JPanel headerPanel = createHeaderPanel();
        
        // Informations du compte
        JPanel infoPanel = createInfoPanel();
        
        // Statistiques
        JPanel statsPanel = createStatsPanel();
        
        // Boutons d'action
        JPanel actionPanel = createActionPanel();

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(statsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(actionPanel);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Mon Compte");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        
        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            "Informations personnelles"
        ));

        addField(panel, "Nom d'utilisateur:", utilisateur.getUsername());
        addField(panel, "Email:", utilisateur.getEmail());
        addField(panel, "Capital actuel:", df.format(utilisateur.getCapital()));
        addField(panel, "Date d'inscription:", utilisateur.getDateCreation());
        if (utilisateur.getPays() != null) {
            addField(panel, "Pays:", utilisateur.getPays());
        }

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            "Statistiques"
        ));

        try {
            ClassementUtilisateur stats = utilisateurDAO.getStatistiquesUtilisateur(utilisateur.getId());
            addField(panel, "Paris gagnés:", stats.getParisGagnes() + "/" + stats.getTotalParis());
            addField(panel, "Taux de réussite:", String.format("%.1f%%", stats.getTauxReussite()));
            addField(panel, "Gains totaux:", df.format(stats.getGainTotal()));
        } catch (SQLException e) {
            e.printStackTrace();
            panel.add(new JLabel("Erreur lors du chargement des statistiques"));
        }

        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(BACKGROUND_COLOR);

        // Bouton Modifier le profil (style bleu)
        JButton modifierButton = new JButton("Modifier le profil");
        modifierButton.setFont(REGULAR_FONT);
        modifierButton.setForeground(Color.WHITE);
        modifierButton.setBackground(PRIMARY_COLOR);
        modifierButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        modifierButton.setFocusPainted(false);
        modifierButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover pour le bouton modifier
        modifierButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                modifierButton.setBackground(new Color(67, 56, 202)); // Bleu plus foncé
            }
            @Override
            public void mouseExited(MouseEvent e) {
                modifierButton.setBackground(PRIMARY_COLOR);
            }
        });

        // Bouton Recharger le compte (style vert)
        JButton rechargerButton = new JButton("Recharger le compte");
        rechargerButton.setFont(REGULAR_FONT);
        rechargerButton.setForeground(Color.WHITE);
        rechargerButton.setBackground(new Color(16, 185, 129)); // Vert
        rechargerButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(16, 185, 129)),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        rechargerButton.setFocusPainted(false);
        rechargerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover pour le bouton recharger
        rechargerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                rechargerButton.setBackground(new Color(5, 150, 105)); // Vert plus foncé
            }
            @Override
            public void mouseExited(MouseEvent e) {
                rechargerButton.setBackground(new Color(16, 185, 129));
            }
        });

        modifierButton.addActionListener(e -> ouvrirModificationProfil());
        rechargerButton.addActionListener(e -> ouvrirRechargerCompte());

        panel.add(modifierButton);
        panel.add(rechargerButton);

        return panel;
    }

    private void addField(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        JLabel valueComponent = new JLabel(value);
        
        labelComponent.setFont(REGULAR_FONT);
        valueComponent.setFont(REGULAR_FONT);
        
        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    private void ouvrirModificationProfil() {
        // TODO: Implémenter la modification du profil
        JOptionPane.showMessageDialog(this,
            "Fonctionnalité de modification du profil à venir",
            "En développement",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void ouvrirRechargerCompte() {
        String montantStr = JOptionPane.showInputDialog(this,
            "Entrez le montant à recharger :",
            "Recharger le compte",
            JOptionPane.QUESTION_MESSAGE);
            
        if (montantStr != null && !montantStr.trim().isEmpty()) {
            try {
                double montant = Double.parseDouble(montantStr.replace(",", "."));
                if (montant <= 0) {
                    throw new NumberFormatException();
                }
                
                utilisateurDAO.mettreAJourCapital(
                    utilisateur.getId(),
                    utilisateur.getCapital() + montant
                );
                
                utilisateur.setCapital(utilisateur.getCapital() + montant);
                
                JOptionPane.showMessageDialog(this,
                    "Compte rechargé avec succès de " + df.format(montant),
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                    
                dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez entrer un montant valide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la recharge du compte",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 
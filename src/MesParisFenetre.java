import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import org.json.JSONObject;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import javax.swing.border.AbstractBorder;

public class MesParisFenetre extends JDialog {
    private Utilisateur utilisateur;
    private Connection connexion;
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color SECONDARY_COLOR = new Color(16, 185, 129);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(31, 41, 55);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final int CARD_RADIUS = 15;
    private JPanel parisContainer;
    private JComboBox<String> filtreStatut;
    private List<Pari> tousLesParis;

    public MesParisFenetre(JFrame parent, Utilisateur utilisateur, Connection connexion) {
        super(parent, "Mes Paris", true);
        this.utilisateur = utilisateur;
        this.connexion = connexion;
        this.tousLesParis = new ArrayList<>();
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        initialiserComposants();
    }

    private void initialiserComposants() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // En-tête
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Mes Paris");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Ajout du filtre
        JPanel filtrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtrePanel.setBackground(BACKGROUND_COLOR);
        filtrePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel filtreLabel = new JLabel("Filtrer par statut : ");
        filtreLabel.setFont(REGULAR_FONT);
        filtreStatut = new JComboBox<>(new String[]{"Tous", "EN_ATTENTE", "GAGNE", "PERDU", "CORBEILLE"});
        filtreStatut.setFont(REGULAR_FONT);
        filtreStatut.addActionListener(e -> filtrerParis());
        
        filtrePanel.add(filtreLabel);
        filtrePanel.add(filtreStatut);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(filtrePanel);
        headerPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Conteneur pour les paris avec scroll
        parisContainer = new JPanel();
        parisContainer.setLayout(new BoxLayout(parisContainer, BoxLayout.Y_AXIS));
        parisContainer.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(parisContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Charger tous les paris
        chargerParis();
        
        setContentPane(mainPanel);
    }

    private void chargerParis() {
        try {
            UtilisateurDAO dao = new UtilisateurDAO(connexion);
            tousLesParis = dao.getParis(utilisateur.getId());
            filtrerParis(); // Applique le filtre actuel
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des paris : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrerParis() {
        parisContainer.removeAll();
        String filtreSelectionne = (String) filtreStatut.getSelectedItem();
        
        for (Pari pari : tousLesParis) {
            boolean afficher = false;
            
            if (filtreSelectionne.equals("CORBEILLE")) {
                afficher = pari.isDansCorbeille();
            } else if (filtreSelectionne.equals("Tous")) {
                afficher = !pari.isDansCorbeille();
            } else {
                afficher = !pari.isDansCorbeille() && pari.getStatut().equals(filtreSelectionne);
            }
            
            if (afficher) {
                parisContainer.add(creerCartePari(pari));
                parisContainer.add(Box.createVerticalStrut(10));
            }
        }
        
        parisContainer.revalidate();
        parisContainer.repaint();
    }

    private JPanel creerCartePari(Pari pari) {
        JPanel carte = new JPanel(new BorderLayout(15, 0));
        carte.setBackground(Color.WHITE);
        carte.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER_COLOR, CARD_RADIUS),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Partie gauche : informations du match
        JPanel matchInfo = new JPanel(new GridLayout(3, 1, 0, 5));
        matchInfo.setOpaque(false);
        
        JLabel equipes = new JLabel(pari.getHomeTeam() + " vs " + pari.getAwayTeam());
        equipes.setFont(BOLD_FONT);
        
        JLabel typePari = new JLabel("Type: " + pari.getType() + " (Cote: " + pari.getCote() + ")");
        typePari.setFont(REGULAR_FONT);
        
        JLabel date = new JLabel(pari.getDate());
        date.setFont(REGULAR_FONT);
        date.setForeground(new Color(107, 114, 128));
        
        matchInfo.add(equipes);
        matchInfo.add(typePari);
        matchInfo.add(date);
        
        carte.add(matchInfo, BorderLayout.WEST);

        // Partie centrale : montant et gain potentiel
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        infoPanel.setOpaque(false);

        JLabel montant = new JLabel(String.format("Mise : %.2f€", pari.getMontant()));
        montant.setFont(BOLD_FONT);
        
        JLabel gainPotentiel = new JLabel(String.format("Gain potentiel : %.2f€", 
            pari.getMontant() * pari.getCote()));
        gainPotentiel.setFont(REGULAR_FONT);
        gainPotentiel.setForeground(SECONDARY_COLOR);

        JLabel statut = new JLabel(pari.getStatut());
        statut.setFont(BOLD_FONT);
        switch (pari.getStatut()) {
            case "GAGNE":
                statut.setForeground(SECONDARY_COLOR);
                break;
            case "PERDU":
                statut.setForeground(new Color(239, 68, 68));
                break;
            default:
                statut.setForeground(PRIMARY_COLOR);
                break;
        }

        infoPanel.add(montant);
        infoPanel.add(gainPotentiel);
        infoPanel.add(statut);
        
        carte.add(infoPanel, BorderLayout.CENTER);

        // Partie droite : bouton d'action
        if (!pari.getStatut().equals("EN_ATTENTE")) {
            JPanel boutonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            boutonsPanel.setOpaque(false);

            JButton actionButton;
            if (pari.isDansCorbeille()) {
                actionButton = new JButton("Restaurer");
                actionButton.setBackground(SECONDARY_COLOR);
            } else {
                actionButton = new JButton("Supprimer");
                actionButton.setBackground(new Color(220, 38, 38));
            }
            
            actionButton.setForeground(Color.WHITE);
            actionButton.setBorderPainted(false);
            actionButton.setFocusPainted(false);
            actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            actionButton.setFont(REGULAR_FONT);
            actionButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            actionButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    actionButton.setBackground(actionButton.getBackground().darker());
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    actionButton.setBackground(pari.isDansCorbeille() ? SECONDARY_COLOR : new Color(220, 38, 38));
                }
            });

            actionButton.addActionListener(e -> {
                try {
                    UtilisateurDAO dao = new UtilisateurDAO(connexion);
                    if (pari.isDansCorbeille()) {
                        dao.restaurerDeLaCorbeille(pari.getId());
                    } else {
                        dao.mettreAuCorbeille(pari.getId());
                    }
                    chargerParis(); // Recharger les paris
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Erreur lors de la modification du pari",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            
            boutonsPanel.add(actionButton);
            carte.add(boutonsPanel, BorderLayout.EAST);
        }

        return carte;
    }

    private class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }
            
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }
    }
} 
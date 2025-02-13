import javax.swing.*;
import java.awt.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.swing.border.AbstractBorder;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardFenetre extends JFrame {
    private Utilisateur utilisateur;
    private Connection connexion;
    private JPanel mainPanel;
    private JPanel contentPanel;
    
    // Nouvelles couleurs correspondant au design de login
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);    // Violet/Indigo
    private static final Color SECONDARY_COLOR = new Color(16, 185, 129); // Vert
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255); // Blanc
    private static final Color HOVER_COLOR = new Color(245, 247, 250);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(31, 41, 55);
    private static final Color SIDEBAR_COLOR = new Color(249, 250, 251);  // Gris très clair
    private static final Color SIDEBAR_HOVER_COLOR = new Color(237, 233, 254); // Violet très clair
    private static final int HEADER_HEIGHT = 70;
    private static final int SIDEBAR_WIDTH = 250;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color ACCENT_COLOR = new Color(99, 102, 241);    // Indigo plus vif
    private static final int CARD_RADIUS = 15;
    private static final Color CARD_SHADOW = new Color(0, 0, 0, 20);
    private static final int LOGO_HEIGHT = 50;  // Hauteur fixe du logo
    private static final Color MATCH_HOVER_COLOR = new Color(249, 250, 251);
    private static final int TEAM_LOGO_SIZE = 30;
    private static final int MATCH_CARD_HEIGHT = 80;

    private JLabel capitalLabel;
    private String currentHomeTeam;
    private String currentAwayTeam;
    private JComboBox<String> filtreChampionnat;
    private static final String[] CHAMPIONNATS = {
        "Tous les championnats",
        "Premier League",
        "Ligue 1",
        "La Liga",
        "Serie A",
        "Bundesliga"
    };

    public DashboardFenetre(Utilisateur utilisateur, Connection connexion) {
        this.utilisateur = utilisateur;
        this.connexion = connexion;
        initialiserComposants();
        setTitle("BetBlitz - Tableau de Bord");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initialiserComposants() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = creerHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebarPanel = creerMenuLateral();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Content avec marge à gauche
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Ajouter le filtre des championnats avant le panneau des matchs
        JPanel filtrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtrePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel filtreLabel = new JLabel("Filtrer par championnat : ");
        filtreLabel.setFont(REGULAR_FONT);
        filtreLabel.setForeground(TEXT_COLOR);
        
        filtreChampionnat = new JComboBox<>(CHAMPIONNATS);
        filtreChampionnat.setFont(REGULAR_FONT);
        filtreChampionnat.setBackground(Color.WHITE);
        filtreChampionnat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        filtreChampionnat.addActionListener(e -> mettreAJourMatchs());
        
        filtrePanel.add(filtreLabel);
        filtrePanel.add(filtreChampionnat);

        // Ajouter le filtre au contentPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 0, 20);
        contentPanel.add(filtrePanel, gbc);

        // Panneau des matchs à venir avec nouvelle position
        JPanel matchesPanel = creerPanneauMatchs();
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 20, 20, 20);
        contentPanel.add(matchesPanel, gbc);

        // Panneau des paris populaires
        JPanel betsPanel = creerPanneauParis();
        gbc.gridy = 2;
        contentPanel.add(betsPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel creerHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), HEADER_HEIGHT));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Panel pour le logo et le titre
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        brandPanel.setOpaque(false);

        // Ajout du logo avec maintien des proportions
        try {
            BufferedImage logoImage = ImageIO.read(new File("src/img/logo.png"));
            // Calcul du ratio pour maintenir les proportions
            double ratio = (double) logoImage.getWidth() / logoImage.getHeight();
            int newWidth = (int) (LOGO_HEIGHT * ratio);
            
            // Utilisation de getScaledInstance avec SCALE_AREA_AVERAGING pour une meilleure qualité
            Image scaledLogo = logoImage.getScaledInstance(newWidth, LOGO_HEIGHT, Image.SCALE_AREA_AVERAGING);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
            brandPanel.add(logoLabel);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo: " + e.getMessage());
            // Fallback au texte si le logo ne peut pas être chargé
            JLabel titleLabel = new JLabel("BetBlitz");
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setForeground(PRIMARY_COLOR);
            brandPanel.add(titleLabel);
        }

        headerPanel.add(brandPanel, BorderLayout.WEST);

        // Panel utilisateur
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        // Ajout de l'affichage du capital
        capitalLabel = new JLabel(String.format("Capital : %.2f€", utilisateur.getCapital()));
        capitalLabel.setFont(REGULAR_FONT);
        capitalLabel.setForeground(TEXT_COLOR);
        userPanel.add(capitalLabel);

        JLabel userLabel = new JLabel(utilisateur.getUsername());
        userLabel.setFont(REGULAR_FONT);
        userLabel.setForeground(TEXT_COLOR);
        userPanel.add(userLabel);

        JButton logoutButton = new JButton("Déconnexion");
        styleModernButton(logoutButton, SECONDARY_COLOR);
        logoutButton.addActionListener(e -> deconnexion());
        userPanel.add(logoutButton);

        headerPanel.add(userPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private void styleModernButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(REGULAR_FONT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private JPanel creerMenuLateral() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));
        sidebar.add(Box.createVerticalStrut(20));

        // Navigation
        ajouterBoutonSidebar(sidebar, "Accueil", e -> {}, true);
        ajouterBoutonSidebar(sidebar, "Paris en Direct", e -> ouvrirParisEnDirect(), false);
        ajouterBoutonSidebar(sidebar, "Mes Paris", e -> ouvrirMesParis(), false);
        ajouterBoutonSidebar(sidebar, "Classement", e -> {
            ClassementFenetre classementFenetre = new ClassementFenetre(this, connexion);
            classementFenetre.setVisible(true);
        }, false);
        ajouterBoutonSidebar(sidebar, "Compte", e -> ouvrirCompte(), false);
        ajouterBoutonSidebar(sidebar, "Paramètres", e -> ouvrirParametres(), false);

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private void ajouterBoutonSidebar(JPanel sidebar, String text, java.awt.event.ActionListener action, boolean isActive) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(SIDEBAR_WIDTH - 20, 45));
        button.setFont(REGULAR_FONT);
        button.setForeground(isActive ? PRIMARY_COLOR : TEXT_COLOR);
        button.setBackground(isActive ? SIDEBAR_HOVER_COLOR : SIDEBAR_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!isActive) {
                    button.setBackground(SIDEBAR_HOVER_COLOR);
                    button.setForeground(PRIMARY_COLOR);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isActive) {
                    button.setBackground(SIDEBAR_COLOR);
                    button.setForeground(TEXT_COLOR);
                }
            }
        });

        button.addActionListener(action);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private JPanel creerPanneauMatchs() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        // Titre
        JLabel title = new JLabel("Matchs à venir");
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        // Conteneur des matchs avec scroll
        JPanel matchesContainer = new JPanel();
        matchesContainer.setLayout(new BoxLayout(matchesContainer, BoxLayout.Y_AXIS));
        matchesContainer.setBackground(BACKGROUND_COLOR);

        try {
            JSONArray matches = ApiFootball.getUpcomingMatches();
            for (int i = 0; i < matches.length(); i++) {
                JSONObject match = matches.getJSONObject(i);
                JPanel matchCard = creerCarteMatch(match);
                matchesContainer.add(matchCard);
                matchesContainer.add(Box.createVerticalStrut(10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(matchesContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel creerCarteMatch(JSONObject match) {
        try {
            // Extraire les informations du match
            String homeTeam = match.getJSONObject("homeTeam").getString("name");
            String awayTeam = match.getJSONObject("awayTeam").getString("name");
            String homeLogo = match.getJSONObject("homeTeam").getString("crest");
            String awayLogo = match.getJSONObject("awayTeam").getString("crest");
            String competition = match.getJSONObject("competition").getString("name");
            
            // Formater la date
            String dateStr = match.getString("utcDate");
            Instant instant = Instant.parse(dateStr);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM - HH:mm", new Locale("fr", "FR"))
                .withZone(ZoneId.systemDefault());
            String formattedDate = formatter.format(instant);

            // Récupérer les cotes
            JSONArray oddsArray = ApiFootball.getMatchOdds(homeTeam, awayTeam);
            String odd1 = "1.95";  // Valeurs par défaut
            String oddX = "3.40";
            String odd2 = "2.10";

            if (oddsArray.length() > 0) {
                JSONObject game = oddsArray.getJSONObject(0);
                JSONArray bookmakers = game.getJSONArray("bookmakers");
                if (bookmakers.length() > 0) {
                    JSONObject bookmaker = bookmakers.getJSONObject(0);
                    JSONArray markets = bookmaker.getJSONArray("markets");
                    if (markets.length() > 0) {
                        JSONObject market = markets.getJSONObject(0);
                        JSONArray outcomes = market.getJSONArray("outcomes");
                        for (int j = 0; j < outcomes.length(); j++) {
                            JSONObject outcome = outcomes.getJSONObject(j);
                            if (outcome.getString("name").equals(homeTeam)) {
                                odd1 = String.format("%.2f", outcome.getDouble("price"));
                            } else if (outcome.getString("name").equals("Draw")) {
                                oddX = String.format("%.2f", outcome.getDouble("price"));
                            } else if (outcome.getString("name").equals(awayTeam)) {
                                odd2 = String.format("%.2f", outcome.getDouble("price"));
                            }
                        }
                    }
                }
            }

            // Créer le panneau du match
            return creerPanneauMatch(
                homeTeam,
                homeLogo,
                awayTeam,
                awayLogo,
                formattedDate,
                odd1, oddX, odd2,
                competition
            );
        } catch (Exception e) {
            e.printStackTrace();
            JPanel errorPanel = new JPanel();
            errorPanel.add(new JLabel("Erreur lors du chargement du match"));
            return errorPanel;
        }
    }

    private JPanel creerPanneauMatch(String homeTeam, String homeLogo, 
                                   String awayTeam, String awayLogo,
                                   String date, String odd1, String oddX, String odd2,
                                   String competition) {
        this.currentHomeTeam = homeTeam;
        this.currentAwayTeam = awayTeam;
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setPreferredSize(new Dimension(-1, MATCH_CARD_HEIGHT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Panel gauche avec équipes et logos
        JPanel teamsPanel = new JPanel(new GridBagLayout());
        teamsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        // Équipe domicile
        JPanel homeTeamPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        homeTeamPanel.setOpaque(false);
        ajouterLogoEquipe(homeTeamPanel, homeLogo);
        JLabel homeLabel = new JLabel(homeTeam);
        homeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        homeTeamPanel.add(homeLabel);
        gbc.gridx = 0;
        teamsPanel.add(homeTeamPanel, gbc);

        // VS
        JLabel vsLabel = new JLabel("vs");
        vsLabel.setForeground(new Color(156, 163, 175));
        vsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 1;
        teamsPanel.add(vsLabel, gbc);

        // Équipe extérieur
        JPanel awayTeamPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        awayTeamPanel.setOpaque(false);
        ajouterLogoEquipe(awayTeamPanel, awayLogo);
        JLabel awayLabel = new JLabel(awayTeam);
        awayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        awayTeamPanel.add(awayLabel);
        gbc.gridx = 2;
        teamsPanel.add(awayTeamPanel, gbc);

        // Date
        JLabel dateLabel = new JLabel(date);
        dateLabel.setForeground(new Color(107, 114, 128));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 3;
        gbc.insets = new Insets(0, 20, 0, 0);
        teamsPanel.add(dateLabel, gbc);

        // Competition
        JLabel competitionLabel = new JLabel(competition);
        competitionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        competitionLabel.setForeground(new Color(107, 114, 128));
        gbc.gridx = 4;
        gbc.insets = new Insets(0, 20, 0, 0);
        teamsPanel.add(competitionLabel, gbc);

        panel.add(teamsPanel, BorderLayout.WEST);

        // Panel des cotes avec espacement uniforme
        JPanel oddsPanel = new JPanel();
        oddsPanel.setLayout(new BoxLayout(oddsPanel, BoxLayout.X_AXIS));
        oddsPanel.setOpaque(false);
        oddsPanel.add(Box.createHorizontalStrut(10));
        ajouterBoutonCoteModerne(oddsPanel, "1", odd1);
        oddsPanel.add(Box.createHorizontalStrut(10));
        ajouterBoutonCoteModerne(oddsPanel, "X", oddX);
        oddsPanel.add(Box.createHorizontalStrut(10));
        ajouterBoutonCoteModerne(oddsPanel, "2", odd2);
        panel.add(oddsPanel, BorderLayout.EAST);

        // Effet hover amélioré
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(MATCH_HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(Color.WHITE);
            }
        });

        // Panel principal pour le match avec BorderLayout
        JPanel matchMainPanel = new JPanel(new BorderLayout());
        matchMainPanel.setBackground(Color.WHITE);
        matchMainPanel.add(panel, BorderLayout.CENTER);

        // Bouton "Voir plus" avec style moderne
        JButton voirPlusBtn = new JButton("Voir plus");
        voirPlusBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        voirPlusBtn.setForeground(PRIMARY_COLOR);
        voirPlusBtn.setBackground(Color.WHITE);
        voirPlusBtn.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER_COLOR, 4),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        voirPlusBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voirPlusBtn.setFocusPainted(false);

        // Effet hover sur le bouton
        voirPlusBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                voirPlusBtn.setBackground(new Color(243, 244, 246));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                voirPlusBtn.setBackground(Color.WHITE);
            }
        });

        // Action du bouton
        voirPlusBtn.addActionListener(e -> ouvrirDetailsMatch(homeTeam, homeLogo, awayTeam, awayLogo, date));

        // Panel pour le bouton avec marge
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 15));
        btnPanel.add(voirPlusBtn);

        matchMainPanel.add(btnPanel, BorderLayout.SOUTH);

        return matchMainPanel;
    }

    private void ajouterLogoEquipe(JPanel panel, String logoUrl) {
        try {
            // Créer une image temporaire en attendant le chargement
            JLabel logoLabel = new JLabel();
            logoLabel.setPreferredSize(new Dimension(TEAM_LOGO_SIZE, TEAM_LOGO_SIZE));
            
            // Charger le logo en arrière-plan
            new Thread(() -> {
                try {
                    URL url = new URL(logoUrl);
                    BufferedImage img = ImageIO.read(url);
                    Image scaledImg = img.getScaledInstance(TEAM_LOGO_SIZE, TEAM_LOGO_SIZE, Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> {
                        logoLabel.setIcon(new ImageIcon(scaledImg));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
            panel.add(logoLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Classe pour les bordures arrondies
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

    private void ajouterBoutonCoteModerne(JPanel panel, String label, String odd) {
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setBackground(BACKGROUND_COLOR);
        // Augmenter la taille minimale du conteneur
        buttonContainer.setPreferredSize(new Dimension(100, 65));
        buttonContainer.setMinimumSize(new Dimension(100, 65));
        buttonContainer.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER_COLOR, 12),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        // Label du type de pari avec ellipsis si trop long
        JLabel typeLabel = new JLabel("<html><div style='width: 70px; text-align: center'>" + label + "</div></html>");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLabel.setForeground(new Color(107, 114, 128));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Cote avec style amélioré
        JLabel oddLabel = new JLabel(odd);
        oddLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        oddLabel.setForeground(TEXT_COLOR);
        oddLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonContainer.add(typeLabel);
        buttonContainer.add(Box.createVerticalStrut(4));
        buttonContainer.add(oddLabel);

        // Effet hover amélioré
        buttonContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buttonContainer.setBackground(new Color(243, 244, 246));
                buttonContainer.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(PRIMARY_COLOR, 12),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                oddLabel.setForeground(PRIMARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buttonContainer.setBackground(BACKGROUND_COLOR);
                buttonContainer.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(BORDER_COLOR, 12),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));
                oddLabel.setForeground(TEXT_COLOR);
            }
        });

        buttonContainer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                handleBetClick(label, odd);
            }
        });

        panel.add(buttonContainer);
    }

    private void handleBetClick(String type, String oddString) {
        try {
            // Remplacer la virgule par un point pour le parsing
            oddString = oddString.replace(",", ".");
            double odd = Double.parseDouble(oddString);
            
            // Demander le montant du pari
            String montantStr = JOptionPane.showInputDialog(this,
                "Entrez le montant du pari :",
                "Montant du pari",
                JOptionPane.QUESTION_MESSAGE);
                
            if (montantStr != null && !montantStr.trim().isEmpty()) {
                // Convertir le montant en remplaçant aussi la virgule par un point
                montantStr = montantStr.replace(",", ".");
                double montant = Double.parseDouble(montantStr);
                verifierEtPlacerPari(type, montant, odd);
            }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                "Montant invalide. Veuillez entrer un nombre valide.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Une erreur est survenue : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verifierEtPlacerPari(String type, double montant, double cote) {
        System.out.println("Vérification du pari - Type: " + type + ", Montant: " + montant + ", Cote: " + cote);
        
        // Vérifier que le type de pari est valide
        if (!type.equals("1") && !type.equals("X") && !type.equals("2") && !type.equals("N")) { // Ajouter "X" et "N" comme types valides
                    JOptionPane.showMessageDialog(this,
                "Type de pari invalide: " + type,
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
        // Vérifier que le montant est positif et ne dépasse pas le capital disponible
        if (montant <= 0) {
                    JOptionPane.showMessageDialog(this,
                "Le montant du pari doit être positif",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

        if (montant > utilisateur.getCapital()) {
                    JOptionPane.showMessageDialog(this,
                "Montant insuffisant dans votre compte",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
        // Si toutes les vérifications sont passées, placer le pari
        placerPari(type, montant, cote);
    }

    private void placerPari(String type, double montant, double cote) {
        try {
            // Convertir le type si nécessaire (X ou N en DRAW)
            String typeFormatted = type.equals("X") || type.equals("N") ? "DRAW" : type;
            
            // Récupérer l'ID du match depuis l'API
            int matchId = getMatchIdFromTeams(currentHomeTeam, currentAwayTeam);
            
            // Utiliser UtilisateurDAO pour placer le pari
                UtilisateurDAO utilisateurDAO = new UtilisateurDAO(connexion);
            boolean pariPlace = utilisateurDAO.placerPari(
                utilisateur.getId(),
                montant,
                typeFormatted,
                String.valueOf(cote),
                matchId,
                currentHomeTeam,
                currentAwayTeam
            );

            if (pariPlace) {
                // Mettre à jour l'objet utilisateur local avec le nouveau capital
                double nouveauCapital = utilisateur.getCapital() - montant;
                utilisateur.setCapital(nouveauCapital);

                // Afficher un message de confirmation
                    JOptionPane.showMessageDialog(this,
                    "Pari placé avec succès !\nMontant: " + montant + "€\nGain potentiel: " + String.format("%.2f", montant * cote) + "€",
                    "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                // Mettre à jour l'affichage du capital
                    mettreAJourAffichageCapital();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Impossible de placer le pari. Vérifiez votre capital disponible.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement du pari dans la base de données: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du placement du pari: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode utilitaire pour obtenir l'ID du match
    private int getMatchIdFromTeams(String homeTeam, String awayTeam) {
        // Créer un ID unique basé sur les noms des équipes
        String matchString = homeTeam + awayTeam;
        return Math.abs(matchString.hashCode());
    }

    private void mettreAJourAffichageCapital() {
        // Mettre à jour l'affichage du capital dans l'interface
        // Par exemple, dans un JLabel dans le header
        capitalLabel.setText(String.format("Capital : %.2f€", utilisateur.getCapital()));
    }

    private void deconnexion() {
        dispose();
        new LoginFenetre(this.connexion).setVisible(true);
    }

    // Méthodes de navigation
    private void ouvrirParisEnDirect() {
        ParisEnDirectFenetre parisEnDirect = new ParisEnDirectFenetre(this, utilisateur, connexion);
        parisEnDirect.setVisible(true);
    }

    private void ouvrirMesParis() {
        MesParisFenetre mesParis = new MesParisFenetre(this, utilisateur, connexion);
        mesParis.setVisible(true);
    }

    private void ouvrirCompte() {
        CompteFenetre compteFenetre = new CompteFenetre(this, utilisateur, connexion);
        compteFenetre.setVisible(true);
        // Rafraîchir l'affichage du capital après la fermeture de la fenêtre
        mettreAJourAffichageCapital();
    }

    private void ouvrirParametres() {
        ParametresFenetre parametresFenetre = new ParametresFenetre(this, utilisateur, connexion);
        parametresFenetre.setVisible(true);
    }

    private JPanel creerPanneauParis() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER_COLOR, CARD_RADIUS),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("Mes Paris");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);

        return panel;
    }

    // Nouvelle méthode pour ouvrir la fenêtre de détails
    private void ouvrirDetailsMatch(String homeTeam, String homeLogo, String awayTeam, String awayLogo, String date) {
        try {
            // Créer un ID de match à partir des noms d'équipes
        int matchId = getMatchIdFromTeams(homeTeam, awayTeam);
        
            // Créer la fenêtre de dialogue personnalisée
        JDialog dialog = new JDialog(this, "Détails du Match", true);
            dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

            // Panneau principal avec padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(BACKGROUND_COLOR);

            // En-tête du match
            JPanel matchHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            matchHeader.setBackground(BACKGROUND_COLOR);

            // Ajouter les logos et noms des équipes
            JPanel homeTeamPanel = creerPanneauEquipe(homeTeam, homeLogo);
        JLabel vsLabel = new JLabel("VS");
            vsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            JPanel awayTeamPanel = creerPanneauEquipe(awayTeam, awayLogo);

            matchHeader.add(homeTeamPanel);
            matchHeader.add(vsLabel);
            matchHeader.add(awayTeamPanel);

            // Ajouter la date
        JLabel dateLabel = new JLabel(date);
            dateLabel.setFont(REGULAR_FONT);
            dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Panneau des paris
            JPanel betsPanel = new JPanel();
            betsPanel.setLayout(new BoxLayout(betsPanel, BoxLayout.Y_AXIS));
            betsPanel.setBackground(BACKGROUND_COLOR);
            betsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                "Paris disponibles"
            ));

            // Récupérer et afficher les cotes
            try {
                JSONArray oddsArray = ApiFootball.getMatchOdds(homeTeam, awayTeam);
                if (oddsArray != null && oddsArray.length() >= 3) {
                    // Panneau pour les boutons de paris
                    JPanel oddsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
                    oddsPanel.setBackground(BACKGROUND_COLOR);

                    // Extraire les cotes du JSONArray
                    double homeOdd = oddsArray.getJSONObject(0).getDouble("price");
                    double drawOdd = oddsArray.getJSONObject(1).getDouble("price");
                    double awayOdd = oddsArray.getJSONObject(2).getDouble("price");

                    // Créer les boutons de paris avec les cotes
                    ajouterBoutonPari(oddsPanel, "1", String.format("%.2f", homeOdd), homeTeam + " gagne");
                    ajouterBoutonPari(oddsPanel, "N", String.format("%.2f", drawOdd), "Match nul");
                    ajouterBoutonPari(oddsPanel, "2", String.format("%.2f", awayOdd), awayTeam + " gagne");

                    betsPanel.add(oddsPanel);
                }
                } catch (Exception e) {
                    e.printStackTrace();
                JLabel errorLabel = new JLabel("Impossible de charger les cotes");
                errorLabel.setForeground(Color.RED);
                betsPanel.add(errorLabel);
            }

            // Assembler tous les éléments
            mainPanel.add(matchHeader);
            mainPanel.add(Box.createVerticalStrut(10));
            mainPanel.add(dateLabel);
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(betsPanel);

            dialog.add(mainPanel);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture des détails du match : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
        }
        
    private JPanel creerPanneauEquipe(String teamName, String logoUrl) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);

        try {
            // Logo de l'équipe
            ImageIcon originalIcon = new ImageIcon(new URL(logoUrl));
            Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(logoLabel);

            // Nom de l'équipe
            JLabel nameLabel = new JLabel(teamName);
            nameLabel.setFont(REGULAR_FONT);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(nameLabel);
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(new JLabel(teamName));
        }

        return panel;
    }

    private void ajouterBoutonPari(JPanel panel, String type, String cote, String description) {
        JButton button = new JButton("<html><center>" + description + "<br>Cote: " + cote + "</center></html>");
        button.setBackground(BACKGROUND_COLOR);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        button.setFocusPainted(false);
        
        button.addActionListener(e -> {
            String montantStr = JOptionPane.showInputDialog(this,
                "Entrez le montant du pari :",
                "Placer un pari",
                JOptionPane.QUESTION_MESSAGE);
            
            if (montantStr != null && !montantStr.trim().isEmpty()) {
                try {
                    double montant = Double.parseDouble(montantStr.replace(",", "."));
                    verifierEtPlacerPari(type, montant, Double.parseDouble(cote));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Montant invalide",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        panel.add(button);
    }

    private void mettreAJourMatchs() {
        try {
            String championnatSelectionne = (String) filtreChampionnat.getSelectedItem();
            JSONArray matches = ApiFootball.getUpcomingMatches();
            JPanel matchesContainer = new JPanel();
            matchesContainer.setLayout(new BoxLayout(matchesContainer, BoxLayout.Y_AXIS));
            matchesContainer.setBackground(BACKGROUND_COLOR);

            for (int i = 0; i < matches.length(); i++) {
                JSONObject match = matches.getJSONObject(i);
                String competition = match.getJSONObject("competition").getString("name");
                
                // Filtrer les matchs selon le championnat sélectionné
                if (championnatSelectionne.equals("Tous les championnats") || 
                    championnatSelectionne.equals(competition)) {
                    
                    JPanel matchCard = creerCarteMatch(match);
                    matchesContainer.add(matchCard);
                    matchesContainer.add(Box.createVerticalStrut(10));
                }
            }

            // Mettre à jour l'affichage
            JPanel matchesPanel = (JPanel) contentPanel.getComponent(1);
            JScrollPane scrollPane = (JScrollPane) matchesPanel.getComponent(1);
            scrollPane.setViewportView(matchesContainer);
            scrollPane.revalidate();
            scrollPane.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la mise à jour des matchs : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 
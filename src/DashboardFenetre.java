import javax.swing.*;
import java.awt.*;

public class DashboardFenetre extends JFrame {
    private Utilisateur utilisateur;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);    // Indigo
    private static final Color SECONDARY_COLOR = new Color(16, 185, 129); // Vert
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color HOVER_COLOR = new Color(238, 240, 255);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(31, 41, 55);
    private static final int LOGO_SIZE = 50;  // Logo un peu plus grand
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color ACCENT_COLOR = new Color(99, 102, 241);    // Indigo plus vif

    public DashboardFenetre(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initialiserComposants();
        setTitle("BetBlitz - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initialiserComposants() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Sidebar avec navigation
        JPanel sidebarPanel = createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Contenu principal (par défaut: Accueil)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Afficher la page d'accueil par défaut
        showAccueil();

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        // Logo et titre
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setBackground(Color.WHITE);
        
        // Logo avec dimensions originales
        ImageIcon originalIcon = new ImageIcon("src/img/logo.png");
        Image originalImage = originalIcon.getImage();
        double ratio = (double) originalIcon.getIconHeight() / originalIcon.getIconWidth();
        int logoHeight = (int) (LOGO_SIZE * ratio);
        Image scaledLogo = originalImage.getScaledInstance(LOGO_SIZE, logoHeight, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        
        leftPanel.add(logoLabel);

        // Panel utilisateur avec style moderne
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setBackground(Color.WHITE);
        
        // Bouton de solde avec style amélioré
        JButton balanceButton = createStyledButton("1000 €", SECONDARY_COLOR);
        balanceButton.setPreferredSize(new Dimension(120, 35));
        
        // Avatar utilisateur avec style amélioré
        JPanel avatarPanel = createAvatarPanel(utilisateur.getUsername());
        
        // Label utilisateur
        JLabel userLabel = new JLabel(utilisateur.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(TEXT_COLOR);
        
        userPanel.add(balanceButton);
        userPanel.add(userLabel);
        userPanel.add(avatarPanel);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));

        // Ajouter un peu d'espace en haut
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        String[] menuItems = {
            "Accueil", "Paris en Direct", "Mes Paris", "Compte", "Paramètres"
        };

        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            sidebarPanel.add(menuButton);
            sidebarPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        return sidebarPanel;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT_COLOR);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        // Ajouter les actions
        button.addActionListener(e -> {
            switch(text) {
                case "Accueil": showAccueil(); break;
                case "Paris en Direct": showParisEnDirect(); break;
                case "Mes Paris": showMesParis(); break;
                case "Compte": showCompte(); break;
                case "Paramètres": showParametres(); break;
            }
        });

        // Effet de survol amélioré
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(HOVER_COLOR);
                button.setForeground(PRIMARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(TEXT_COLOR);
            }
        });

        return button;
    }

    private JPanel createAvatarPanel(String username) {
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Cercle de fond
                g2.setColor(PRIMARY_COLOR);
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Initiales
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String initials = String.valueOf(username.charAt(0)).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(35, 35);
            }
        };
        avatarPanel.setOpaque(false);
        return avatarPanel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    // Méthodes pour afficher les différentes sections
    private void showAccueil() {
        contentPanel.removeAll();
        contentPanel.add(createAccueilPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showParisEnDirect() {
        contentPanel.removeAll();
        contentPanel.add(createParisEnDirectPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMesParis() {
        contentPanel.removeAll();
        contentPanel.add(createMesParisPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCompte() {
        contentPanel.removeAll();
        contentPanel.add(createComptePanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showParametres() {
        contentPanel.removeAll();
        contentPanel.add(createParametresPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Panels pour chaque section
    private JPanel createAccueilPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Widgets d'accueil
        panel.add(createMatchWidget("Matchs à venir"), gbc);
        return panel;
    }

    private JPanel createParisEnDirectPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(new JLabel("Paris en Direct - En développement"));
        return panel;
    }

    private JPanel createMesParisPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(new JLabel("Mes Paris - En développement"));
        return panel;
    }

    private JPanel createComptePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(new JLabel("Compte - En développement"));
        return panel;
    }

    private JPanel createParametresPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(new JLabel("Paramètres - En développement"));
        return panel;
    }

    private JPanel createMatchWidget(String title) {
        JPanel widget = new JPanel();
        widget.setBackground(Color.WHITE);
        widget.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        widget.add(new JLabel(title));
        return widget;
    }
} 
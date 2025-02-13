import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.border.EmptyBorder;
import org.json.JSONArray;
import org.json.JSONObject;

public class ParisEnDirectFenetre extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_COLOR = new Color(31, 41, 55);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SCORE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    
    private final Utilisateur utilisateur;
    private final Connection connexion;
    private final Timer refreshTimer;
    private JPanel matchesPanel;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public ParisEnDirectFenetre(JFrame parent, Utilisateur utilisateur, Connection connexion) {
        super(parent, "Paris en Direct", false); // false pour non-modal
        this.utilisateur = utilisateur;
        this.connexion = connexion;

        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initializeUI();

        // Timer pour rafraîchir les scores toutes les 30 secondes
        refreshTimer = new Timer(30000, e -> refreshMatchesData());
        refreshTimer.start();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // En-tête
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel principal avec scroll pour les matchs
        matchesPanel = new JPanel();
        matchesPanel.setLayout(new BoxLayout(matchesPanel, BoxLayout.Y_AXIS));
        matchesPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(matchesPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Charger les matchs initiaux
        refreshMatchesData();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Matchs en Direct");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);

        // Bouton de rafraîchissement manuel
        JButton refreshButton = createStyledButton("Rafraîchir", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> refreshMatchesData());

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(refreshButton, BorderLayout.EAST);

        return panel;
    }

    private void refreshMatchesData() {
        matchesPanel.removeAll();
        
        try {
            JSONArray liveMatches = ApiFootball.getLiveMatches();
            
            if (liveMatches == null || liveMatches.length() == 0) {
                showNoMatchesMessage();
            } else {
                for (int i = 0; i < liveMatches.length(); i++) {
                    JSONObject match = liveMatches.getJSONObject(i);
                    addMatchPanel(match);
                }
            }
            
            matchesPanel.revalidate();
            matchesPanel.repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage();
        }
    }

    private void addMatchPanel(JSONObject match) {
        JPanel matchPanel = new JPanel();
        matchPanel.setLayout(new BorderLayout());
        matchPanel.setBackground(BACKGROUND_COLOR);
        matchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Informations du match
        String homeTeam = match.getJSONObject("homeTeam").getString("name");
        String awayTeam = match.getJSONObject("awayTeam").getString("name");
        int homeScore = match.getJSONObject("score").getJSONObject("fullTime").getInt("home");
        int awayScore = match.getJSONObject("score").getJSONObject("fullTime").getInt("away");
        String minute = match.getString("minute");

        // Panel central avec les scores
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        scorePanel.setBackground(BACKGROUND_COLOR);

        JLabel scoreLabel = new JLabel(String.format("%s %d - %d %s", homeTeam, homeScore, awayScore, awayTeam));
        scoreLabel.setFont(SCORE_FONT);
        scoreLabel.setForeground(TEXT_COLOR);

        JLabel minuteLabel = new JLabel(minute + "'");
        minuteLabel.setFont(REGULAR_FONT);
        minuteLabel.setForeground(PRIMARY_COLOR);

        scorePanel.add(scoreLabel);
        scorePanel.add(Box.createHorizontalStrut(20));
        scorePanel.add(minuteLabel);

        // Boutons de paris
        JPanel betsPanel = createBetsPanel(match);

        matchPanel.add(scorePanel, BorderLayout.CENTER);
        matchPanel.add(betsPanel, BorderLayout.SOUTH);

        // Ajouter un espace entre les matchs
        matchesPanel.add(Box.createVerticalStrut(10));
        matchesPanel.add(matchPanel);
    }

    private JPanel createBetsPanel(JSONObject match) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel.setBackground(BACKGROUND_COLOR);

        // Récupérer les cotes en direct
        double[] odds = {2.10, 3.50, 3.20}; // À remplacer par les vraies cotes de l'API

        String[] types = {"1", "N", "2"};
        String[] labels = {"Victoire domicile", "Match nul", "Victoire extérieur"};

        for (int i = 0; i < types.length; i++) {
            final String type = types[i];
            final double odd = odds[i];
            
            JButton betButton = createBetButton(labels[i], String.format("%.2f", odd));
            betButton.addActionListener(e -> placerPariEnDirect(match, type, odd));
            panel.add(betButton);
        }

        return panel;
    }

    private JButton createBetButton(String label, String odd) {
        JButton button = new JButton(String.format("<html><center>%s<br>%s</center></html>", label, odd));
        button.setFont(REGULAR_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(BACKGROUND_COLOR);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(243, 244, 246));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BACKGROUND_COLOR);
            }
        });

        return button;
    }

    private void placerPariEnDirect(JSONObject match, String type, double cote) {
        String montantStr = JOptionPane.showInputDialog(this,
            "Entrez le montant du pari :",
            "Placer un pari en direct",
            JOptionPane.QUESTION_MESSAGE);
            
        if (montantStr != null && !montantStr.trim().isEmpty()) {
            try {
                double montant = Double.parseDouble(montantStr.replace(",", "."));
                if (montant <= 0) throw new NumberFormatException();

                // TODO: Implémenter la logique de pari en direct
                JOptionPane.showMessageDialog(this,
                    "Fonctionnalité de paris en direct à venir",
                    "En développement",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez entrer un montant valide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color),
            new EmptyBorder(8, 20, 8, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void showNoMatchesMessage() {
        JLabel messageLabel = new JLabel("Aucun match en direct pour le moment");
        messageLabel.setFont(REGULAR_FONT);
        messageLabel.setForeground(TEXT_COLOR);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        matchesPanel.add(messageLabel);
    }

    private void showErrorMessage() {
        JLabel errorLabel = new JLabel("Erreur lors du chargement des matchs en direct");
        errorLabel.setFont(REGULAR_FONT);
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        matchesPanel.add(errorLabel);
    }

    @Override
    public void dispose() {
        refreshTimer.stop();
        super.dispose();
    }
} 
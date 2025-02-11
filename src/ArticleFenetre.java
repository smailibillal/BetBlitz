import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.plaf.ColorUIResource;

public class ArticleFenetre extends JFrame {
    private static final long serialVersionUID = 1L;
    private ArticleDAO articleDAO;
    private Utilisateur utilisateurConnecte;
    private JTable table;
    private JTextField designationField;
    private JTextField prixField;
    private JTextField quantiteField;
    private JButton ajouterButton;
    private JButton supprimerButton;
    private JButton actualiserButton;

    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);    // Indigo
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);   // Emerald
    private static final Color DANGER_COLOR = new Color(239, 68, 68);     // Red
    private static final Color TEXT_COLOR = new Color(31, 41, 55);        // Gray 800
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public ArticleFenetre(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        setupLookAndFeel();
        initialiserConnexion();
        initialiserComposants();
        setTitle("Gestion des Articles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15);
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", REGULAR_FONT);
            UIManager.put("Label.font", REGULAR_FONT);
            UIManager.put("TextField.font", REGULAR_FONT);
            UIManager.put("Table.font", REGULAR_FONT);
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialiserConnexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connexion = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/stocks_db?serverTimezone=UTC",
                "root",
                ""
            );
            articleDAO = new ArticleDAO(connexion);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur de connexion à la base de données: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            // En cas d'erreur de connexion, on ferme la fenêtre
            dispose();
            // Et on réaffiche la fenêtre de connexion
            new LoginFenetre(null).setVisible(true);
        }
    }

    private void initialiserComposants() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // En-tête avec informations utilisateur
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel central avec formulaire et boutons
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(BACKGROUND_COLOR);

        // Formulaire
        JPanel formPanel = createFormPanel();
        centerPanel.add(formPanel, BorderLayout.NORTH);

        // Table des articles
        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        // Ajout des listeners pour les boutons
        ajouterButton.addActionListener(e -> ajouterArticle());
        supprimerButton.addActionListener(e -> supprimerArticle());
        actualiserButton.addActionListener(e -> rafraichirTable());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
        
        // Rafraîchir la table au démarrage
        rafraichirTable();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Gestion des Articles");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);

        JLabel userLabel = new JLabel("Connecté en tant que " + utilisateurConnecte.getUsername());
        userLabel.setFont(REGULAR_FONT);
        userLabel.setForeground(PRIMARY_COLOR);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(createRoundedBorder("Nouvel Article"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Champs de saisie stylisés
        designationField = createStyledTextField(20);
        prixField = createStyledTextField(10);
        quantiteField = createStyledTextField(10);

        // Layout des champs
        addFormField(formPanel, "Désignation", designationField, gbc, 0);
        addFormField(formPanel, "Prix HT (€)", prixField, gbc, 1);
        addFormField(formPanel, "Quantité", quantiteField, gbc, 2);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        ajouterButton = createStyledButton("Ajouter", SUCCESS_COLOR);
        supprimerButton = createStyledButton("Supprimer", DANGER_COLOR);
        actualiserButton = createStyledButton("Actualiser", PRIMARY_COLOR);

        buttonPanel.add(ajouterButton);
        buttonPanel.add(supprimerButton);
        buttonPanel.add(actualiserButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(createRoundedBorder("Liste des Articles"));

        // Table stylisée
        table = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (row % 2 == 0) {
                    comp.setBackground(new Color(249, 250, 251));
                } else {
                    comp.setBackground(Color.WHITE);
                }
                if (isCellSelected(row, column)) {
                    comp.setBackground(new Color(239, 246, 255));
                }
                return comp;
            }
        };
        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // En-tête de table stylisé
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(REGULAR_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(REGULAR_FONT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    private Border createRoundedBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lblField = new JLabel(label);
        lblField.setFont(REGULAR_FONT);
        lblField.setForeground(TEXT_COLOR);
        panel.add(lblField, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.weightx = 0.0;
    }

    private void rafraichirTable() {
        try {
            DefaultTableModel model = articleDAO.getTableModel();
            table.setModel(model);
            
            // Configurer les largeurs des colonnes
            table.getColumnModel().getColumn(0).setPreferredWidth(80);   // Référence
            table.getColumnModel().getColumn(1).setPreferredWidth(300);  // Désignation
            table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Prix HT
            table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Quantité
            table.getColumnModel().getColumn(4).setPreferredWidth(150);  // Créé par
            
            // Centrer certaines colonnes
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);  // Référence
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);  // Prix
            table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);  // Quantité
            
            // Formater la colonne prix avec 2 décimales
            table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                    if (value instanceof Double) {
                        value = String.format("%.2f €", (Double) value);
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des articles: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterArticle() {
        try {
            String designation = designationField.getText().trim();
            if (designation.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "La désignation ne peut pas être vide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            double prix;
            int quantite;
            try {
                prix = Double.parseDouble(prixField.getText().replace(",", "."));
                quantite = Integer.parseInt(quantiteField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez entrer des valeurs numériques valides",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (prix < 0 || quantite < 0) {
                JOptionPane.showMessageDialog(this,
                    "Les valeurs ne peuvent pas être négatives",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Article article = new Article(designation, prix, quantite);
            article.setCreatedBy(utilisateurConnecte.getId());

            if (articleDAO.ajouter(article)) {
                JOptionPane.showMessageDialog(this,
                    "Article ajouté avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                viderChamps();
                rafraichirTable();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ajout: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerArticle() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un article à supprimer",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int reference = (int) table.getValueAt(selectedRow, 0);
        int confirmation = JOptionPane.showConfirmDialog(this,
            "Êtes-vous sûr de vouloir supprimer cet article ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                if (articleDAO.supprimer(reference)) {
                    rafraichirTable();
                    JOptionPane.showMessageDialog(this,
                        "Article supprimé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la suppression: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viderChamps() {
        designationField.setText("");
        prixField.setText("");
        quantiteField.setText("");
        designationField.requestFocus();
    }
}

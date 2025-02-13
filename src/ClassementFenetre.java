import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class ClassementFenetre extends JDialog {
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color SECONDARY_COLOR = new Color(16, 185, 129);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font TABLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private Connection connexion;
    private DecimalFormat df = new DecimalFormat("#0.00");

    public ClassementFenetre(JFrame parent, Connection connexion) {
        super(parent, "Classement des Parieurs", true);
        this.connexion = connexion;
        
        setSize(800, 600);
        setLocationRelativeTo(parent);
        initialiserComposants();
    }

    private void initialiserComposants() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // En-tête avec style moderne
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Classement des parieurs");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Les meilleurs parieurs de la plateforme");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(new Color(107, 114, 128));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table de classement avec nouveau design
        String[] columns = {
            "Position", "Parieur", "Taux de Réussite", "Paris Gagnés", 
            "Total Paris", "Gains Totaux"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            UtilisateurDAO dao = new UtilisateurDAO(connexion);
            List<ClassementUtilisateur> classement = dao.getClassement();
            
            int position = 1;
            for (ClassementUtilisateur user : classement) {
                String positionStr = position == 1 ? "1" : 
                                   position == 2 ? "2" : 
                                   position == 3 ? "3" : 
                                   String.valueOf(position);
                model.addRow(new Object[]{
                    positionStr,
                    user.getUsername(),
                    df.format(user.getTauxReussite()) + "%",
                    user.getParisGagnes(),
                    user.getTotalParis(),
                    df.format(user.getGainTotal()) + "€"
                });
                position++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la récupération du classement",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }

        JTable table = new JTable(model);
        table.setFont(TABLE_FONT);
        table.setRowHeight(50);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(237, 233, 254));
        table.setSelectionForeground(PRIMARY_COLOR);

        // Personnalisation de l'en-tête
        table.getTableHeader().setFont(TABLE_HEADER_FONT);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(PRIMARY_COLOR);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        table.getTableHeader().setReorderingAllowed(false);

        // Personnalisation des colonnes
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        for (int i = 2; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Alternance des couleurs des lignes
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                }
                
                // Style spécial pour les 3 premiers
                if (row < 3) {
                    setFont(TABLE_FONT.deriveFont(Font.BOLD));
                    if (!isSelected) {
                        setForeground(PRIMARY_COLOR);
                    }
                } else {
                    setFont(TABLE_FONT);
                    if (!isSelected) {
                        setForeground(Color.BLACK);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Panneau pour la table avec ombre
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        tablePanel.add(scrollPane);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }
} 
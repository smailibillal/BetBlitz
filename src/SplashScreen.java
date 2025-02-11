import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SplashScreen extends JWindow {
    private static final int WIDTH = 500;
    private static final int HEIGHT = 350;
    private JProgressBar progressBar;
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color PROGRESS_BG = new Color(233, 236, 239);
    private JLabel loadingLabel;

    public SplashScreen() {
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        
        // Rendre la fenêtre arrondie
        setShape(new RoundRectangle2D.Double(0, 0, WIDTH, HEIGHT, 20, 20));
        
        // Panel principal avec effet d'ombre
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond blanc avec coins arrondis
                g2.setColor(BACKGROUND_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 30, 20, 30);

        // Logo avec effet de brillance
        ImageIcon originalIcon = new ImageIcon("src/img/logo.png");
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(180, 180, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(resizedImage));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(logoLabel, gbc);

        // Label "Chargement..."
        loadingLabel = new JLabel("Chargement...");
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loadingLabel.setForeground(PRIMARY_COLOR);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(10, 30, 5, 30);
        panel.add(loadingLabel, gbc);

        // Barre de progression personnalisée
        progressBar = new JProgressBar() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond de la barre
                g2.setColor(PROGRESS_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Progression
                int width = (int) (getWidth() * (getValue() / 100.0));
                g2.setColor(PRIMARY_COLOR);
                g2.fillRoundRect(0, 0, width, getHeight(), 10, 10);

                g2.dispose();
            }
        };
        progressBar.setPreferredSize(new Dimension(400, 8));
        progressBar.setBorderPainted(false);
        progressBar.setBackground(PROGRESS_BG);
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setOpaque(false);
        
        gbc.insets = new Insets(5, 30, 30, 30);
        panel.add(progressBar, gbc);

        add(panel);

        // Effet d'ombre autour de la fenêtre
        getRootPane().putClientProperty("Window.shadow", Boolean.TRUE);
    }

    public void startProgress(LoginFenetre loginFenetre) {
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            private final String[] loadingTexts = {
                "Chargement...",
                "Préparation des données...",
                "Initialisation...",
                "Presque terminé..."
            };

            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(30);
                    publish(i);
                    
                    if (i % 25 == 0) {
                        final int textIndex = i / 25;
                        SwingUtilities.invokeLater(() -> 
                            loadingLabel.setText(loadingTexts[Math.min(textIndex, loadingTexts.length - 1)])
                        );
                    }
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                // Transition vers la page de connexion
                loginFenetre.setLocation(getLocation());
                loginFenetre.setVisible(true);
                dispose();
            }
        };
        worker.execute();
    }
} 
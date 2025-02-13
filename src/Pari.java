public class Pari {
    private int id;
    private String homeTeam;
    private String awayTeam;
    private String type;
    private double cote;
    private double montant;
    private String date;
    private String statut;
    private boolean dansCorbeille;

    public Pari(int id, String homeTeam, String awayTeam, String type, 
                double cote, double montant, String date, String statut, boolean dansCorbeille) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.type = type;
        this.cote = cote;
        this.montant = montant;
        this.date = date;
        this.statut = statut;
        this.dansCorbeille = dansCorbeille;
    }

    // Getters
    public int getId() { return id; }
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public String getType() { return type; }
    public double getCote() { return cote; }
    public double getMontant() { return montant; }
    public String getDate() { return date; }
    public String getStatut() { return statut; }
    public boolean isDansCorbeille() { return dansCorbeille; }
} 
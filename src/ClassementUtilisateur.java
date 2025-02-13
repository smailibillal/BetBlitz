public class ClassementUtilisateur {
    private String username;
    private int totalParis;
    private int parisGagnes;
    private double tauxReussite;
    private double gainTotal;
    private double capitalActuel;

    public ClassementUtilisateur(String username, int totalParis, int parisGagnes, 
                               double gainTotal, double capitalActuel) {
        this.username = username;
        this.totalParis = totalParis;
        this.parisGagnes = parisGagnes;
        this.tauxReussite = totalParis > 0 ? (parisGagnes * 100.0) / totalParis : 0;
        this.gainTotal = gainTotal;
        this.capitalActuel = capitalActuel;
    }

    // Getters
    public String getUsername() { return username; }
    public int getTotalParis() { return totalParis; }
    public int getParisGagnes() { return parisGagnes; }
    public double getTauxReussite() { return tauxReussite; }
    public double getGainTotal() { return gainTotal; }
    public double getCapitalActuel() { return capitalActuel; }
} 
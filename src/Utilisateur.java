public class Utilisateur {
    private int id;
    private String username;
    private String password;
    private String email;
    private Integer age;
    private String pays;
    private String dateCreation;
    private double capital;

    public Utilisateur(int id, String username, String password, String email, String dateCreation, double capital) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateCreation = dateCreation;
        this.capital = capital;
    }

    // Constructeur sans id pour la création
    public Utilisateur(String username, String password, String email, double capital) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.capital = capital;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDateCreation() { return dateCreation; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public String getPays() {
        return pays;
    }
    public void setPays(String pays) {
        this.pays = pays;
    }
    public double getCapital() {
        return capital;
    }
    public void setCapital(double capital) {
        this.capital = capital;
    }
}

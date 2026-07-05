package reframe.model.beans;

public class Utente {
    
    /* ATTRIBUTI */
    private String username;
    private String email;
    
    // Sicurezza: il campo è destinato a memorizzare esclusivamente il digest crittografico della password mai la stringa in chiaro
    private String password;
    private String telefono;
    private String nome;
    private String cognome;
    private String bio;
    
    // Implementazione RBAC (Role-Based Access Control): definisce il livello di privilegio dell'account 
    // per l'autorizzazione alle sezioni riservate (es. 0 = Cliente, 1 = Admin, 2 = Superadmin)
    private int isAdmin;

    /* COSTRUTTORI */
    public Utente() {
    }
    
    public Utente(String username, String email, String password, String telefono, String nome, String cognome, String bio) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.nome = nome;
        this.cognome = cognome;
        this.bio = bio;
    }

    /* GETTER E SETTER */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    /* OVERRIDE METODI */
    @Override
    public String toString() {
        return "Utente{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", telefono='" + telefono + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }
}
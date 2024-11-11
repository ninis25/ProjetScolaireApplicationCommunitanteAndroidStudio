import java.net.InetAddress;
import java.util.ArrayList;

public class Utilisateur {
    private final String login;
    private final String pwd;
    private boolean connecte;
    private final InetAddress ipAddress;
    private final int port;
    private String email;
    private boolean connected = false;
    private final ArrayList<Utilisateur> listeAmis;
    private final ArrayList<Utilisateur> listeBloques;
    private final ArrayList<Message> listeMessages;
    private final ArrayList<Utilisateur> listeAttente;

    public Utilisateur(String login, String pwd,String email, InetAddress ip, int port) {
        this.login = login;
        this.pwd = pwd;
        this.email = email;
        this.ipAddress = ip;
        this.port = port;
        this.listeAmis = new ArrayList<>();
        this.listeBloques = new ArrayList<>();
        this.listeMessages = new ArrayList<>();
        this.listeAttente = new ArrayList<>();
    }

    public String getLogin() {
        return login;
    }

    public String getPwd() {
        return pwd;
    }
    public String getEmail() {
        return email;
    }

    public ArrayList<Utilisateur> getAmis() {
        if (listeAmis.isEmpty()) {
            System.out.println("Vous n'avez pas d'amis.");
        }
        return listeAmis;
    }
    public void envoyerDemandeAmi(Utilisateur ami) {
        // Vérifier si l'ami n'est pas déjà dans la liste d'amis, dans la liste d'attente et n'est pas bloqué
        if (!listeAmis.contains(ami) && !listeAttente.contains(ami) && !listeBloques.contains(ami)) {
            listeAttente.add(ami); // Ajouter à la liste d'attente
            System.out.println("Demande d'ami envoyée à " + ami.getEmail());
        } else {
            System.out.println("Impossible d'envoyer une demande d'ami à cet utilisateur.");
        }
    }

    public ArrayList<Utilisateur> getBloques() {
        return listeBloques;
    }

    public void setBloques(Utilisateur u, boolean statut) {
        if (statut) {
            if (listeAmis.contains(u) && !listeBloques.contains(u)) {
                listeAmis.remove(u);
                listeBloques.add(u);
            }
        } else {
            if (listeBloques.contains(u)) {
                listeBloques.remove(u);
            }
        }
    }

    public ArrayList<Utilisateur> getAttente() {
        return listeAttente;
    }

    public void setAttente(Utilisateur u) {
        if (!listeAttente.contains(u) && !listeBloques.contains(u) && !listeAmis.contains(u) && listeAmis.size() < 10 && listeAttente.size() < 10) {
            listeAttente.add(u);
        }
    }

    public void setConnexion(boolean state) {
        connected = state;
    }

    public boolean getConnexion() {
        return connected;
    }

    public ArrayList<Message> getMessageList() {
        return listeMessages;
    }

    public void setMessage(Message message) {
        listeMessages.add(message);
    }

    public boolean isFriendWith(Utilisateur other) {
        return listeAmis.contains(other);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean estConnecte() {
        return connecte;
    }

    public void connecte() {
        connecte = true;
    }
    public InetAddress getIpAddress() {
        return ipAddress;
    }
    public int getPort() {
        return port;
    }
    public void addFriend(Utilisateur u) {
        listeAmis.add(u);
    }
    public void sendMessageToFriend(Message message) {
        for (Utilisateur ami : listeAmis) {
            ami.setMessage(message);
        }
    }

    public void deconnecte() {
        connecte = false;
    }
    public String toString(){
return "Utilisateur : "+login+" "+pwd+" "+email+" "+ipAddress+" "+port;
    }
}
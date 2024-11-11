import java.net.InetAddress;
import java.util.ArrayList;

public class UserManager {
    private final ArrayList<Utilisateur> listOfUsers;

    public UserManager() {
        listOfUsers = new ArrayList<>();
    }

    public void addUser(Utilisateur utilisateur) {
        listOfUsers.add(utilisateur);
    }

    public Utilisateur getUserByEmail(String email) {
        for (Utilisateur utilisateur : listOfUsers) {
            if (utilisateur.getEmail().equals(email)) {
                return utilisateur;
            }
        }
        return null;
    }

    public boolean userExists(String email) {
        return getUserByEmail(email) != null;
    }

    public Utilisateur getConnectedUser(InetAddress clientAdress, int clientport) {
        for (Utilisateur utilisateur : listOfUsers) {
            if (utilisateur.getIpAddress().equals(clientAdress) && utilisateur.getPort() == clientport) {
                return utilisateur;
            }
        }
        return null;
    }

    public ArrayList<Utilisateur> getAllUsers() {
        return new ArrayList<>(listOfUsers);
    }

}
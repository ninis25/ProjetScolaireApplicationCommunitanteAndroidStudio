import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    Utilisateur expediteur;
    String sujet;
    Utilisateur destinataire;
    boolean envoye, recu;

    Message(Utilisateur expediteur, String sujet, Utilisateur destinataire) {
        this.expediteur = expediteur;
        this.sujet = sujet;
        this.destinataire = destinataire;
        this.envoye = false;
        this.recu = false;
    }

    void recu() {
        recu = true;
    }

    public String Ecrire_Message(String sujet, String contenu) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = dtf.format(now);

        // Vous pouvez ajuster le format du message en fonction de vos besoins
        return "message@" + formattedDate + "@" + expediteur.getEmail() + "@" + destinataire.getEmail() + "@" + sujet + "@" + contenu;
    }
}
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer implements Runnable {
	public final static int PORT = 6012;
	private final static int BUFFER = 1024;
	private final static int MAX_USERS = 100;
	private final ArrayList<String> existing_clients = new ArrayList<>();
	private final ArrayList<Integer> client_ports = new ArrayList<>();
	private final ArrayList<InetAddress> client_addresses = new ArrayList<>();


	private final DatagramSocket socket;
	private final UserManager userManager;

	public ChatServer() throws IOException {
		socket = new DatagramSocket(PORT);
		System.out.println("Le serveur est hébergé sur le port : " + PORT);
		userManager = new UserManager();
	}

	public Utilisateur returnUser(String mail) throws UnknownHostException {
		for (Utilisateur utilisateur : userManager.getAllUsers()) {
			if (utilisateur.getEmail().equals(mail)) {
				return utilisateur;
			}
		}
		// Si l'utilisateur n'est pas trouvé, retourne un utilisateur introuvable
		return new Utilisateur("UtilisateurIntrouvable", "server", "@unknown",InetAddress.getByName("127.0.0.1"), 6012);

	}

	public void sendMessage(String message, InetAddress clientAddress, int clientPort) {
		try {
			byte[] data = message.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, clientAddress, clientPort);
			socket.send(packet);
		} catch (IOException e) {
			System.err.println("Erreur lors de l'envoi du message au client : " + e.getMessage());
		}
	}

	public void creation(DatagramPacket packet, String message) {
		String[] parts = message.split(" ");
		String login = parts[1];
		String password = parts[2];
		String email = parts[3];

		if (!userManager.userExists(email)) {
			// Ajouter l'utilisateur à la liste
			Utilisateur newUser = new Utilisateur(login, password, email, packet.getAddress(), packet.getPort());
			userManager.addUser(newUser);

			// Message de confirmation pour le client
			String confirmationMessage = login + " , l'inscription est réussie !";
			sendMessage("Inscription réussie", newUser.getIpAddress(), newUser.getPort());
			sendMessage(confirmationMessage, packet.getAddress(), packet.getPort());

			System.out.println("User created");
		} else {
			System.out.println("Erreur, votre identifiant est déjà utilisé");
			// Envoyer un message d'erreur au client si l'utilisateur existe déjà
			String errorMessage = "Erreur, votre identifiant est déjà utilisé";
			sendMessage(errorMessage, packet.getAddress(), packet.getPort());
		}
	}

	public void connexion(DatagramPacket packet, String message) {
		String[] parts = message.split(" ");
		boolean userExists = false;

		for (Utilisateur utilisateur : userManager.getAllUsers()) {
			if (utilisateur.getLogin().equals(parts[1])) {
				System.out.println(utilisateur.getLogin());
				userExists = true;
				if (utilisateur.getPwd().equals(parts[2]) && !utilisateur.estConnecte()) {
					utilisateur.connecte();
					System.out.println("Connexion réussie : " + parts[1]);
					sendMessage("Connexion réussie", utilisateur.getIpAddress(), utilisateur.getPort());
					return;
				} else {
					System.out.println("Connection refused");
					sendMessage("Connexion refusée", packet.getAddress(), packet.getPort());
				}
			}
		}
		if (!userExists) {
			System.out.println("Utilisateur non trouvé");
			sendMessage("Utilisateur non trouvé", packet.getAddress(), packet.getPort());
		}
	}

	public void lecture(DatagramPacket packet, String message) throws UnknownHostException {
		String[] parts = message.split(" ");

		Utilisateur user1 = returnUser(parts[1]);
		Utilisateur user2 = returnUser(parts[2]);

		if (user1 != null && user2 != null) {
			// Vérifiez les différentes contraintes pour l'envoi d'un message
			if (user1.isFriendWith(user2)) {
				// Récupérez le contenu du message
				StringBuilder content = new StringBuilder();
				for (int i = 4; i < parts.length; i++) {
					content.append(parts[i]).append(" ");
				}

				// Envoyez le message au destinataire
				sendMessage(content.toString(), user2.getIpAddress(), user2.getPort());
				String confirmationMessage = "Client : votre message a bien été envoyé";
				sendMessage(confirmationMessage, user2.getIpAddress(), user2.getPort());

				// Ajoutez le message à la liste du destinataire (simulez l'ajout dans la classe Message)
				System.out.println(user2.getLogin() + " a reçu un message de " + user1.getLogin() + ": " + content.toString());
			} else {
				String errorMessage = "Le message ne peut être envoyé car " + user1.getLogin() + " ou " + user2.getLogin() + " ne sont pas amis.";
				sendMessage(errorMessage, packet.getAddress(), packet.getPort());
			}
		} else {
			String errorMessage = "Le message ne peut être envoyé car les utilisateurs renseignés ne sont pas corrects.";
			sendMessage(errorMessage, packet.getAddress(), packet.getPort());
		}
	}

	public void whoIsConnected(DatagramPacket packet){
		for (Utilisateur utilisateur : userManager.getAllUsers()) {
			if (utilisateur.estConnecte()) {
				String msg = "Utilisateur connecté : " + utilisateur.getLogin();
				sendMessage(msg, packet.getAddress(), packet.getPort());
			}
		}
		String msg = "Aucun utilisateur connecté";
		sendMessage(msg, packet.getAddress(), packet.getPort());
	}

	public void deconnexion(DatagramPacket packet,String message) {
		String[] parts = message.split(" ");
		String login = parts[1];
		for (Utilisateur utilisateur : userManager.getAllUsers()) {
			if (utilisateur.getLogin().equals(login)) {
				utilisateur.deconnecte();
				System.out.println("Utilisateur déconnecté : " + login);
			}
		}
		System.out.println("Utilisateur non trouvé pour la déconnexion : " + login);
		String mesage = "Utilisateur non trouvé pour la déconnexion : " + login;
		sendMessage(mesage, packet.getAddress(), packet.getPort());
	}

	private void demandeAmi(DatagramPacket packet, String message) throws IOException {
		String[] parts = message.split(" ");

		if (parts.length == 3) {
			String emailDemandeur = parts[1];
			Utilisateur utilisateurCourant = returnUser(emailDemandeur);

			String emailDemande = parts[2];
			Utilisateur demandeur = returnUser(emailDemande);

			if (demandeur != null) {
				// Supposons que "utilisateurCourant" soit l'utilisateur actuellement connecté
				utilisateurCourant.envoyerDemandeAmi(demandeur);


				// Envoyez une confirmation au demandeur
				String confirmationMessage = "Demande d'ami envoyée à : " + emailDemande;
				sendMessage(confirmationMessage, packet.getAddress(), packet.getPort());
			} else {
				// Envoyez un message d'erreur si l'utilisateur demandeur n'existe pas
				String errorMessage = "L'utilisateur demandeur n'existe pas.";
				sendMessage(errorMessage, packet.getAddress(), packet.getPort());
			}
		} else {
			// Envoyez un message d'erreur si le format du message est invalide
			String errorMessage = "Format de message invalide pour demande_ami.";
			sendMessage(errorMessage, packet.getAddress(), packet.getPort());
		}
	}
	private void accepteAmi(DatagramPacket packet, String message) throws IOException {
		String[] parts = message.split(" ");

		if (parts.length == 2) {
			String emailDemande = parts[1];
			Utilisateur demandeur = returnUser(emailDemande);

			InetAddress clientAddress = packet.getAddress();
			int clientPort = packet.getPort();
			Utilisateur utilisateurCourant = userManager.getConnectedUser(clientAddress, clientPort);

			if (demandeur != null && utilisateurCourant != null) {
				// Ajoutez demandeur à la liste d'amis de utilisateurCourant
				utilisateurCourant.addFriend(demandeur);

				// Ajoutez utilisateurCourant à la liste d'amis de demandeur
				demandeur.addFriend(utilisateurCourant);

				// Supprimez la demande d'ami de la liste d'attente de utilisateurCourant
				utilisateurCourant.getAttente().remove(demandeur);

				// Envoyez un message de confirmation au demandeur
				String confirmationMessage = "Demande d'ami acceptée par : " + utilisateurCourant.getEmail();
				sendMessage(confirmationMessage, clientAddress, clientPort);
			} else {
				// Envoyez un message d'erreur si l'utilisateur demandeur n'existe pas
				String errorMessage = "L'utilisateur demandeur n'existe pas.";
				sendMessage(errorMessage, clientAddress, clientPort);
			}
		} else {
			// Envoyez un message d'erreur si le format du message est invalide
			String errorMessage = "Format de message invalide pour accepte_ami.";
			sendMessage(errorMessage, packet.getAddress(), packet.getPort());
		}
	}


	public void viewUsers() {
		for (Utilisateur utilisateur : userManager.getAllUsers()) {
			// Print all users information
			System.out.println(utilisateur.toString()+"\n");
		}
	}

	public void sendUsers(DatagramPacket packet) {
		for (Utilisateur utilisateur : userManager.getAllUsers()) {
			// Print all users information
			String msg = utilisateur.getLogin();
			sendMessage(msg, utilisateur.getIpAddress(), utilisateur.getPort());
		}
	}

	public void viewFriend() {
		for (Utilisateur utilisateur : userManager.getAllUsers()) {
			System.out.println(utilisateur.getAmis());
		}
	}

	public void run() {
		byte[] buffer = new byte[BUFFER];
		while (true) {
			try {
				Arrays.fill(buffer, (byte) 0);
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);

				String message = new String(buffer, 0, packet.getLength());

				InetAddress clientAddress = packet.getAddress();
				int client_port = packet.getPort();

				String id = clientAddress.toString() + "|" + client_port;

				// Check if the maximum limit is reached
				if (client_addresses.size() >= MAX_USERS) {
					System.out.println("Maximum user limit reached. Connection from " + id + " rejected.");
					continue; // Skip processing further for this client
				}

				if (!existing_clients.contains(id)) {
					existing_clients.add(id);
					client_ports.add(client_port);
					client_addresses.add(clientAddress);
				}

                if (message.contains("creation")) {
					creation(packet, message);
				} else if (message.contains("connexion")) {
					connexion(packet, message); // Supprimer les premiers 5 caractères
					continue;
				} else if (message.contains("logout")) {
					deconnexion(packet, message);
				} else if (message.contains("view")) {
					viewUsers();
					viewFriend();
				} else if (message.contains("who")) {
					whoIsConnected(packet);
				} else if (message.contains("lecture")) {
					lecture(packet, message);
				} else if (message.contains("demande_ami")) {
					demandeAmi(packet,message);
				} else if (message.contains("accepte_ami")) {
					accepteAmi(packet,message);
				} else if (message.contains("send")){
					sendUsers(packet);
				}

				System.out.println(id + " : " + message);

				byte[] data = (id + " : " + message).getBytes();
				for (int i = 0; i < client_addresses.size(); i++) {
					InetAddress cl_address = client_addresses.get(i);
					int cl_port = client_ports.get(i);
					packet = new DatagramPacket(data, data.length, cl_address, cl_port);
					socket.send(packet);
				}

			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}


	public static void main(String args[]) throws Exception {
		ChatServer server_thread = new ChatServer();
		Utilisateur user1 = new Utilisateur("john", "password1", "john@sae", InetAddress.getByName("127.0.0.1"), 6012);
		Utilisateur user2 = new Utilisateur("alice", "password2", "alice@sae",InetAddress.getByName("127.0.0.1"), 6012);
		server_thread.userManager.addUser(user1);
		server_thread.userManager.addUser(user2);
		server_thread.run();

		Thread thread = new Thread(server_thread);
		thread.start();
	}
}


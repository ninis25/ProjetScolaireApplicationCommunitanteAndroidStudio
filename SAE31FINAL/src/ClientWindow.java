import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

public class ClientWindow extends JFrame {

	String host_name;
	JTextPane message_field;
	JTextPane room_field;

	String message = "";
	boolean message_is_ready = false;

	public ClientWindow() {

		JDialog hostNameDialog = new JDialog(this, "Entrer l'adresse du serveur: ", true);
		JTextField hostField = new JTextField("127.0.0.1");
		JButton ok = new JButton("OK");
		hostNameDialog.setLayout(new FlowLayout());
		hostNameDialog.add(hostField);
		hostNameDialog.add(ok);
		hostNameDialog.setLocationRelativeTo(null);
		hostNameDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		hostNameDialog.setSize(250, 65);
		hostNameDialog.setResizable(false);
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				host_name = hostField.getText().trim();
				hostNameDialog.dispose();
			}
		});
		hostNameDialog.setVisible(true);

		setSize(800, 600);
		setTitle("Chat client UDP");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		room_field = new JTextPane();
		message_field = new JTextPane();
		room_field.setEditable(false);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(room_field, BorderLayout.CENTER);

		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.add(message_field, BorderLayout.CENTER);

		add(panel, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);

		ScrollPane x = new ScrollPane();
		x.add(room_field);
		ScrollPane z = new ScrollPane();
		z.add(message_field);
		z.setPreferredSize(new Dimension(100, 100));
		add(x, BorderLayout.CENTER);
		add(z, BorderLayout.SOUTH);

		setVisible(true);
		message_field.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					message_field.setCaretPosition(0);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER && !message_is_ready) {
					message = message_field.getText().trim();
					message_field.setText(null);
					if (message != null && !message.isEmpty()) {
						message_is_ready = true;
					}
				}
			}
		});
	}

	public void displayMessage(String receivedMessage) {
		StyledDocument doc = room_field.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), receivedMessage + "\n", null);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	public boolean isMessageReady() {
		return message_is_ready;
	}

	public void setMessageReady(boolean messageReady) {
		this.message_is_ready = messageReady;
	}

	public String getMessage() {
		return message;
	}

	public String getHostName() {
		return host_name;
	}
}

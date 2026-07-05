package reframe.utils;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailManager {

    /* CREDENZIALI SMTP */
    
    private static final String MITTENTE = "noreply.reframe@gmail.com";
    
    // Autenticazione SMTP implementata tramite App Password generata da Google Account.
    private static final String PASSWORD = "welriypdthcjbrqs"; 

    /* CONFIGURAZIONE E INVIO */
    
    public static void inviaEmail(String destinatario, String oggetto, String corpoTesto) {
        
        // Configurazione dei parametri di connessione al server SMTP di Google con protocollo crittografico STARTTLS obbligatorio sulla porta 587.
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Istanziazione della sessione di posta iniettando l'autenticatore custom per validare le credenziali sul server remoto in fase di handshake.
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MITTENTE, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MITTENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(oggetto);
            message.setText(corpoTesto);

            Transport.send(message);
            System.out.println("Email inviata con successo a " + destinatario);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'invio dell'email: " + e.getMessage());
        }
    }
}
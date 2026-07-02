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

    // Configura questi parametri con una mail dedicata al progetto
    private static final String MITTENTE = "noreply.reframe@gmail.com";
    private static final String PASSWORD = "welriypdthcjbrqs"; 

    public static void inviaEmail(String destinatario, String oggetto, String corpoTesto) {
        
        // Proprietà per il server SMTP (Esempio per Gmail)
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Creazione della sessione con autenticazione
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MITTENTE, PASSWORD);
            }
        });

        try {
            // Composizione del messaggio
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MITTENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(oggetto);
            message.setText(corpoTesto);

            // Invio
            Transport.send(message);
            System.out.println("Email inviata con successo a " + destinatario);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante l'invio dell'email: " + e.getMessage());
        }
    }
}
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public void sendEmail(final String username, final String password) {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("pmanarionale@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("dimaqp@gmail.com"));
            message.setSubject("Aloha");
            message.setText("Dear email receiver," + "\n\n Take care!");

            Transport.send(message);

            System.out.println("Message successfully sent!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}


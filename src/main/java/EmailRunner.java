public class EmailRunner {

    public static EmailReceiver emailReceiver = new EmailReceiver();
    public static EmailSender emailSender = new EmailSender();

    public static void main(String[] args) {

        final String username = "pmanationale@gmail.com";
        final String password = "03ofumuh";

        String protocol = "imap";
        String host = "imap.gmail.com";
        String port = "993";

        emailReceiver.downloadEmails(protocol, host, port, username, password);
        emailSender.sendEmail(username, password);
    }
}


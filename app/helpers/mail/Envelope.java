package helpers.mail;

public class Envelope {
    private String emailTo;
    private String subject;
    private String message;

    public Envelope(String emailTo, String subject, String message) {
        this.emailTo = emailTo;
        this.subject = subject;
        this.message = message;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public String getSubject() {
        return subject;
    }


    public String getMessage() {
        return message;
    }
}
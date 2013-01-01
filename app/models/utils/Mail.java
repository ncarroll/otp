package models.utils;

import play.Configuration;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public class Mail {
    private static final int DELAY = 1;

    public static class Envelope {
        private String subject;
        private String message;
        private List<String> toEmails;

        public Envelope(String subject, String message, List<String> toEmails) {
            this.subject = subject;
            this.message = message;
            this.toEmails = toEmails;
        }

        public Envelope(String subject, String message, String email) {
            this.message = message;
            this.subject = subject;
            this.toEmails = new ArrayList<String>();
            this.toEmails.add(email);
        }
    }

    public static void sendMail(Envelope envelope) {
        SendMailJob sendMailJob = new SendMailJob(envelope);
        final FiniteDuration delay = Duration.create(DELAY, TimeUnit.SECONDS);
        Akka.system().scheduler().scheduleOnce(delay, sendMailJob, Akka.system().dispatcher());
    }

    static class SendMailJob implements Runnable {
        Envelope envelope;

        public SendMailJob(Envelope envelope) {
            this.envelope = envelope;
        }

        public void run() {
            MailerAPI email = play.Play.application().plugin(MailerPlugin.class).email();

            final Configuration root = Configuration.root();
            final String mailFrom = root.getString("mail.from");
            email.addFrom(mailFrom);
            email.setSubject(envelope.subject);
            for (String toEmail : envelope.toEmails) {
                email.addRecipient(toEmail);
                Logger.debug("Mail.sendMail: Mail will be sent to " + toEmail);
            }

            final String mailSign = root.getString("mail.sign");
            email.send(envelope.message + "\n\n " + mailSign,
                    envelope.message + "<br><br>--<br>" + mailSign);

            Logger.debug("Mail sent - SMTP:" + root.getString("smtp.host")
                    + ":" + root.getString("smtp.port")
                    + " SSL:" + root.getString("smtp.ssl")
                    + " user:" + root.getString("smtp.user")
                    + " password:" + root.getString("smtp.password"));
        }
    }
}

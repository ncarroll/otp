package helpers.mail;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;
import play.Configuration;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class Mail {
    private static final int DELAY = 1;

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
            email.setSubject(envelope.getSubject());
            email.addRecipient(envelope.getEmailTo());
            Logger.debug("Mail.sendMail: Mail will be sent to " + envelope.getEmailTo());

            final String mailSign = root.getString("mail.sign");
            email.send(envelope.getMessage() + "\n\n " + mailSign,
                    envelope.getMessage() + "<br><br>--<br>" + mailSign);

            Logger.debug("Mail sent - SMTP:" + root.getString("smtp.host")
                    + ":" + root.getString("smtp.port")
                    + " SSL:" + root.getString("smtp.ssl")
                    + " user:" + root.getString("smtp.user")
                    + " password:" + root.getString("smtp.password"));
        }
    }
}

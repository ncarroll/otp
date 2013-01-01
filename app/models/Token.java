package models;

import models.utils.Mail;
import play.Configuration;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.i18n.Messages;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class Token extends Model {

    private static final int EXPIRATION_DAYS = 1;

    public enum TypeToken {
        password("reset"), email("email");
        private String urlPath;

        TypeToken(String urlPath) {
            this.urlPath = urlPath;
        }
    }

    @Id
    public String token;

    @Constraints.Required
    @Formats.NonEmpty
    public Long userId;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    public TypeToken type;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    @Constraints.Required
    @Formats.NonEmpty
    public String email;

    // -- Queries
    @SuppressWarnings("unchecked")
    public static Model.Finder<String, Token> find = new Finder(String.class, Token.class);

    public static Token findByTokenAndType(String token, TypeToken type) {
        return find.where().eq("token", token).eq("type", type).findUnique();
    }

    public boolean isExpired() {
        return dateCreation != null && dateCreation.before(expirationTime());
    }

    private Date expirationTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, -EXPIRATION_DAYS);
        return cal.getTime();
    }

    private static Token getNewToken(User user, TypeToken type, String email) {
        Token token = new Token();
        token.token = UUID.randomUUID().toString();
        token.userId = user.id;
        token.type = type;
        token.email = email;
        token.save();
        return token;
    }

    public static void sendMailResetPassword(User user) throws MalformedURLException {
        sendMail(user, TypeToken.password, null);
    }

    public static void sendMailChangeMail(User user, @Nullable String email) throws MalformedURLException {
        sendMail(user, TypeToken.email, email);
    }

    private static void sendMail(User user, TypeToken type, String email) throws MalformedURLException {

        Token token = getNewToken(user, type, email);
        String externalServer = Configuration.root().getString("server.hostname");

        String subject = null;
        String message = null;
        String toMail = null;

        // Should use reverse routing here.
        String urlString = urlString = "http://" + externalServer + "/" + type.urlPath + "/" + token.token;
        URL url = new URL(urlString); // validate the URL

        switch (type) {
            case password:
                subject = Messages.get("mail.reset.ask.subject");
                message = Messages.get("mail.reset.ask.message", url.toString());
                toMail = user.email;
                break;
            case email:
                subject = Messages.get("mail.change.ask.subject");
                message = Messages.get("mail.change.ask.message", url.toString());
                toMail = token.email; // == email parameter
                break;
        }

        Logger.debug("sendMailResetLink: url = " + url);
        Mail.Envelope envelope = new Mail.Envelope(subject, message, toMail);
        Mail.sendMail(envelope);
    }

}

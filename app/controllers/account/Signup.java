package controllers.account;

import forms.account.SignupForm;
import helpers.AppException;
import helpers.Hash;
import helpers.mail.Envelope;
import helpers.mail.Mail;
import models.User;
import org.apache.commons.mail.EmailException;
import org.picketbox.core.util.Base32;
import play.Configuration;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.account.signup.confirm;
import views.html.account.signup.create;
import views.html.account.signup.created;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static play.data.Form.form;

public class Signup extends Controller {

    public Result showSignup() {
        return ok(create.render(form(SignupForm.class)));
    }

    public Result save() {
        Form<SignupForm> signupForm = form(SignupForm.class).bindFromRequest();

        if (signupForm.hasErrors()) {
            return badRequest(create.render(signupForm));
        }

        SignupForm signup = signupForm.get();
        Result resultError = checkBeforeSave(signupForm, signup.email);

        if (resultError != null) {
            return resultError;
        }

        try {
            User user = new User();
            user.email = signup.email;
            user.fullname = signup.fullname;
            user.passwordHash = Hash.createPassword(signup.inputPassword);
            user.confirmationToken = UUID.randomUUID().toString();
            user.secretKey = generateSecretKey();
            Logger.debug("Secret key: " + user.secretKey);

            user.save();
            sendMailAskForConfirmation(user);

            return ok(created.render(keyUriFormat(user)));
        } catch (EmailException e) {
            Logger.debug("SignupForm.save Cannot send email", e);
            flash("error", Messages.get("error.sending.email"));
        } catch (Exception e) {
            Logger.error("SignupForm.save error", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(create.render(signupForm));
    }

    private Result checkBeforeSave(Form<SignupForm> signupForm, String email) {
        // Check unique email
        if (User.findByEmail(email) != null) {
            flash("error", Messages.get("error.email.already.exist"));
            return badRequest(create.render(signupForm));
        }

        return null;
    }

    private void sendMailAskForConfirmation(User user) throws EmailException, MalformedURLException {
        String subject = Messages.get("mail.confirm.subject");

        String urlString = "http://" + Configuration.root().getString("server.hostname");
        urlString += "/confirm/" + user.confirmationToken;
        URL url = new URL(urlString); // validate the URL, will throw an exception if bad.
        String message = Messages.get("mail.confirm.message", url.toString());

        Envelope envelope = new Envelope(user.email, subject, message);
        Mail.sendMail(envelope);
    }

    public Result confirm(String token) {
        User user = User.findByConfirmationToken(token);
        if (user == null) {
            flash("error", Messages.get("error.unknown.email"));
            return badRequest(confirm.render());
        }

        if (user.validated) {
            flash("error", Messages.get("error.account.already.validated"));
            return badRequest(confirm.render());
        }

        try {
            if (User.confirm(user)) {
                sendMailConfirmation(user);
                flash("success", Messages.get("account.successfully.validated"));
                return ok(confirm.render());
            } else {
                Logger.debug("SignupForm.confirm cannot confirm user");
                flash("error", Messages.get("error.confirm"));
                return badRequest(confirm.render());
            }
        } catch (AppException e) {
            Logger.error("Cannot signup", e);
            flash("error", Messages.get("error.technical"));
        } catch (EmailException e) {
            Logger.debug("Cannot send email", e);
            flash("error", Messages.get("error.sending.confirm.email"));
        }
        return badRequest(confirm.render());
    }

    private void sendMailConfirmation(User user) throws EmailException {
        String subject = Messages.get("mail.welcome.subject");
        String message = Messages.get("mail.welcome.message");
        Envelope envelope = new Envelope(user.email, subject, message);
        Mail.sendMail(envelope);
    }

    private String generateSecretKey() {
        String secretKey = UUID.randomUUID().toString();
        secretKey = secretKey.replace('-', 'c');
        return secretKey.substring(0, 10);
    }

    private String keyUriFormat(User user) {
        String base32EncodedKey = Base32.encode(user.secretKey.getBytes());

        return "otpauth://totp/" + user.email + "?secret=" + base32EncodedKey;
    }
}

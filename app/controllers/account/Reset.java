package controllers.account;

import forms.account.AskForm;
import forms.account.ResetForm;
import helpers.AppException;
import helpers.mail.Envelope;
import helpers.mail.Mail;
import models.Token;
import models.User;
import org.apache.commons.mail.EmailException;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.account.reset.ask;
import views.html.account.reset.reset;
import views.html.account.reset.runAsk;

import java.net.MalformedURLException;

import static play.data.Form.form;

public class Reset extends Controller {

    public static Result ask() {
        Form<AskForm> askForm = form(AskForm.class);
        return ok(ask.render(askForm));
    }

    public static Result runAsk() {
        Form<AskForm> askForm = form(AskForm.class).bindFromRequest();

        if (askForm.hasErrors()) {
            flash("error", Messages.get("signup.valid.email"));
            return badRequest(ask.render(askForm));
        }

        final String email = askForm.get().email;
        Logger.debug("runAsk: email = " + email);
        User user = User.findByEmail(email);
        Logger.debug("runAsk: user = " + user);

        // If we do not have this email address in the list, we should not expose this to the user.
        // This exposes that the user has an account, allowing a user enumeration attack.
        // See http://www.troyhunt.com/2012/05/everything-you-ever-wanted-to-know.html for details.
        // Instead, email the person saying that the reset failed.
        if (user == null) {
            Logger.debug("No user found with email " + email);
            sendFailedPasswordResetAttempt(email);
            return ok(runAsk.render());
        }

        Logger.debug("Sending password reset link to user " + user);

        try {
            Token.sendMailResetPassword(user);
            return ok(runAsk.render());
        } catch (MalformedURLException e) {
            Logger.error("Cannot validate URL", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(ask.render(askForm));
    }

    private static void sendFailedPasswordResetAttempt(String email) {
        String subject = Messages.get("mail.reset.fail.subject");
        String message = Messages.get("mail.reset.fail.message", email);

        Envelope envelope = new Envelope(subject, message, email);
        Mail.sendMail(envelope);
    }

    public static Result reset(String token) {

        if (token == null) {
            flash("error", Messages.get("error.technical"));
            Form<AskForm> askForm = form(AskForm.class);
            return badRequest(ask.render(askForm));
        }

        Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.password);
        if (resetToken == null) {
            flash("error", Messages.get("error.technical"));
            Form<AskForm> askForm = form(AskForm.class);
            return badRequest(ask.render(askForm));
        }

        if (resetToken.isExpired()) {
            resetToken.delete();
            flash("error", Messages.get("error.expiredresetlink"));
            Form<AskForm> askForm = form(AskForm.class);
            return badRequest(ask.render(askForm));
        }

        Form<ResetForm> resetForm = form(ResetForm.class);
        return ok(reset.render(resetForm, token));
    }

    public static Result runReset(String token) {
        Form<ResetForm> resetForm = form(ResetForm.class).bindFromRequest();

        if (resetForm.hasErrors()) {
            flash("error", Messages.get("signup.valid.password"));
            return badRequest(reset.render(resetForm, token));
        }

        try {
            Token resetToken = Token.findByTokenAndType(token, Token.TypeToken.password);
            if (resetToken == null) {
                flash("error", Messages.get("error.technical"));
                return badRequest(reset.render(resetForm, token));
            }

            if (resetToken.isExpired()) {
                resetToken.delete();
                flash("error", Messages.get("error.expiredresetlink"));
                return badRequest(reset.render(resetForm, token));
            }

            // check email
            User user = User.find.byId(resetToken.userId);
            if (user == null) {
                // display no detail (email unknown for example) to
                // avoir check email by foreigner
                flash("error", Messages.get("error.technical"));
                return badRequest(reset.render(resetForm, token));
            }

            String password = resetForm.get().inputPassword;
            user.changePassword(password);

            // Send email saying that the password has just been changed.
            sendPasswordChanged(user);
            flash("success", Messages.get("resetpassword.success"));
            return ok(reset.render(resetForm, token));
        } catch (AppException e) {
            flash("error", Messages.get("error.technical"));
            return badRequest(reset.render(resetForm, token));
        } catch (EmailException e) {
            flash("error", Messages.get("error.technical"));
            return badRequest(reset.render(resetForm, token));
        }

    }

    private static void sendPasswordChanged(User user) throws EmailException {
        String subject = Messages.get("mail.reset.confirm.subject");
        String message = Messages.get("mail.reset.confirm.message");
        Envelope envelope = new Envelope(subject, message, user.email);
        Mail.sendMail(envelope);
    }
}
package forms.account;

import models.User;
import helpers.AppException;
import play.data.validation.Constraints;
import play.i18n.Messages;

import java.security.GeneralSecurityException;

public class LoginForm {

    @Constraints.Required
    public String email;

    @Constraints.Required
    public String password;

    @Constraints.Required
    public String timeBasedOTP;

    public String validate() {

        User user;

        try {
            user = User.authenticate(email, password, timeBasedOTP);
        } catch (AppException e) {
            return Messages.get("error.technical");
        } catch (GeneralSecurityException gse) {
            return Messages.get("invalid.totp");
        }
        if (user == null) {
            return Messages.get("invalid.user.or.password");
        } else if (!user.validated) {
            return Messages.get("account.not.validated.check.mail");
        }
        return null;
    }
}

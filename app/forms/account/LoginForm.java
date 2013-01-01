package forms.account;

import models.User;
import models.utils.AppException;
import play.data.validation.Constraints;
import play.i18n.Messages;

public class LoginForm {

    @Constraints.Required
    public String email;
    @Constraints.Required
    public String password;

    public String validate() {

        User user;

        try {
            user = User.authenticate(email, password);
        } catch (AppException e) {
            return Messages.get("error.technical");
        }
        if (user == null) {
            return Messages.get("invalid.user.or.password");
        } else if (!user.validated) {
            return Messages.get("account.not.validated.check.mail");
        }
        return null;
    }
}

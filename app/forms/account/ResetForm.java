package forms.account;

import play.data.validation.Constraints;

public class ResetForm {
    @Constraints.Required
    public String inputPassword;
}

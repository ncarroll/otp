package forms.account;

import play.data.validation.Constraints;

public class AskForm {

    @Constraints.Required
    public String email;

    public AskForm() {}

    public AskForm(String email) {
        this.email = email;
    }
}

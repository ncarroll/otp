package forms.account;


import play.data.validation.Constraints;

public class SignupForm {

    @Constraints.Required
    public String email;

    @Constraints.Required
    public String fullname;

    @Constraints.Required
    public String inputPassword;

    public String validate() {
        if (isBlank(email)) {
            return "Email is required";
        }

        if (isBlank(fullname)) {
            return "Full name is required";
        }

        if (isBlank(inputPassword)) {
            return "Password is required";
        }

        return null;
    }

    private boolean isBlank(String input) {
        return input == null || input.isEmpty() || input.trim().isEmpty();
    }
}

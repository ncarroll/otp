package controllers;

import forms.account.LoginForm;
import forms.account.SignupForm;
import models.User;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import views.html.login;

import static play.data.Form.form;

public class Application extends Controller {

    public static Result GO_HOME = redirect(routes.Application.index());

    public static Result GO_PRESENTATION = redirect(routes.Presentation.index());

    public Result index() {
        // Check that the email matches a confirmed user before we redirect
        String email = ctx().session().get("email");
        if (email != null) {
            User user = User.findByEmail(email);
            if (user != null && user.validated) {
                return GO_PRESENTATION;
            } else {
                Logger.debug("Clearing invalid session credentials");
                session().clear();
            }
        }

        return ok(index.render(form(SignupForm.class)));
    }

    public Result authenticate() {
        Form<LoginForm> loginForm = form(LoginForm.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session("email", loginForm.get().email);
            return GO_PRESENTATION;
        }
    }

    public Result logout() {
        session().clear();
        flash("success", Messages.get("youve.been.logged.out"));
        return GO_HOME;
    }

    public Result showLogin() {
        return ok(login.render(form(LoginForm.class)));
    }
}

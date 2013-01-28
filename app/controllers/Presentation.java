package controllers;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.presentation.index;

@Security.Authenticated(Secured.class)
public class Presentation extends Controller {

    public Result index() {
        return ok(index.render(User.findByEmail(request().username())));
    }
}

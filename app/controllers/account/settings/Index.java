package controllers.account.settings;

import controllers.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
@org.springframework.stereotype.Controller
public class Index extends Controller {

    public static Result index() {
        return Password.index();
    }
}

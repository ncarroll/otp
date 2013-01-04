import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.servlet.RequestScoped;
import play.mvc.Http;

import static com.google.inject.servlet.ServletScopes.REQUEST;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bindScope(RequestScoped.class, REQUEST);

        bind(Http.Request.class).toProvider(new Provider<Http.Request>() {
            @Override
            public Http.Request get() {
                return Http.Context.current().request();
            }
        });

        bind(Http.Response.class).toProvider(new Provider<Http.Response>() {
            @Override
            public Http.Response get() {
                return Http.Context.current().response();
            }
        });

        bind(Http.Context.class).toProvider(new Provider<Http.Context>() {
            @Override
            public Http.Context get() {
                return Http.Context.current();
            }
        });
    }
}

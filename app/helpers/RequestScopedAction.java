package helpers;

import com.google.inject.Key;
import com.google.inject.servlet.ServletScopes;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class RequestScopedAction extends Action.Simple {

    @Override
    public Result call(final Http.Context ctx) throws Throwable {

        Callable<Result> resultCallable = ServletScopes.scopeRequest(new Callable<Result>() {
            @Override
            public Result call() throws Exception {
                try {
                    return delegate.call(ctx);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }
            }
        }, new HashMap<Key<?>, Object>());

        return resultCallable.call();
    }
}

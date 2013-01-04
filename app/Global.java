import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

    private Injector injector;

    @Override
    public void onStart(Application application) {
        injector = Guice.createInjector(Stage.PRODUCTION, new AppModule());
    }

    @Override
    public <A> A getControllerInstance(Class<A> aClass) throws Exception {
        return injector.getInstance(aClass);
    }
}

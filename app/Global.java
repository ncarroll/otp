import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

    private AnnotationConfigApplicationContext applicationContext;

    @Override
    public void onStart(Application application) {
        applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AppConfig.class);
        applicationContext.scan("controllers");
        applicationContext.refresh();
    }

    @Override
    public <A> A getControllerInstance(Class<A> aClass) throws Exception {
        return applicationContext.getBean(aClass);
    }
}

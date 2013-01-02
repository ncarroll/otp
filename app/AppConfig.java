import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import play.mvc.Security;

@Configuration
public class AppConfig {

    @Bean
    public Security.AuthenticatedAction authenticatedAction() {
        return new Security.AuthenticatedAction();
    }
}

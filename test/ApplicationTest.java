import models.User;
import org.junit.Test;
import play.mvc.Content;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;


public class ApplicationTest {

    @Test
    public void renderPresentationTemplate() {
        User user = new User();
        Content html = views.html.presentation.index.render(user);
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Hoorah, you have successfully logged in!");
    }
}

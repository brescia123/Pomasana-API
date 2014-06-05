import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

public class PomasanaApplication extends ResourceConfig {

    public PomasanaApplication() {

        packages("org.glassfish.jersey.examples.linking");

        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true);

    }
}

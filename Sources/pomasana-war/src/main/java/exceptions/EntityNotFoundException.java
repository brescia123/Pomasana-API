package exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class EntityNotFoundException extends WebApplicationException {

    /**
     * Create a HTTP 404 (Not Found) exception.
     */
    public EntityNotFoundException() {
        super(Response.status(404).build());
    }

    /**
     * Create a HTTP 404 (Not Found) exception.
     * @param message the String that is the entity of the 404 response.
     */
    public EntityNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).
                entity(message).type("text/plain").build());
    }

}

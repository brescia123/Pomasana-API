package exceptions;

import javax.ws.rs.WebApplicationException;

import helper.Responses;

public class NotAuthorizedException extends WebApplicationException {

    public NotAuthorizedException() {
        super(Responses.notAuthorized());
    }
}

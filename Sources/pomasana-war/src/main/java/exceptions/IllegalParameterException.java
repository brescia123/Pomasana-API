package exceptions;

import javax.ws.rs.WebApplicationException;

import helper.Responses;

public class IllegalParameterException extends WebApplicationException {

    public IllegalParameterException(String illegalParameter) {
        super(Responses.invalidId(illegalParameter));
    }
}

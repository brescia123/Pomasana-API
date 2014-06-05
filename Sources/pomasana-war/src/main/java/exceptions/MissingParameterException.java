package exceptions;

import javax.ws.rs.WebApplicationException;

import helper.Responses;

public class MissingParameterException extends WebApplicationException {

    public MissingParameterException(String illegalParameter) {
        super(Responses.missingJsonField(illegalParameter));
    }

}

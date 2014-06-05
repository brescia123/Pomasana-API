package exceptions;

import javax.ws.rs.WebApplicationException;

import helper.Responses;

public class ReadOnlyParameterException extends WebApplicationException {

    public ReadOnlyParameterException(String readOnlyParameter) {
        super(Responses.readOnlyJsonField(readOnlyParameter));
    }

}

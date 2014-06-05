package exceptions;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import helper.Responses;

public class AsanaProblemException extends WebApplicationException {
    public AsanaProblemException(JsonElement asanaResponse) {
        super(Responses.asanaProblem(asanaResponse));
    }

}

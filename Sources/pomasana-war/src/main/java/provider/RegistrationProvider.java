package provider;


import java.net.URISyntaxException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URLEncoder;

import asana.AsanaConnectHelper;
import helper.Constants;
import helper.Responses;


@Path("/registration")
public class RegistrationProvider {


    @GET
    public Response register(@QueryParam(Constants.CLIENT_REDIRECT_URL_PARAM) String redirectUrl) {


        /*
        Check redirect url
         */

        if (redirectUrl == null) {
            return Responses.badRequest(
                    "The requrired parameter " + Constants.CLIENT_REDIRECT_URL_PARAM
                            + " is missing or not valid"
            );
        }

        try {
            String encodedRedirectUrl = URLEncoder.encode(redirectUrl, "UTF-8");
            System.out.println( "encodedRedirectUrl sent to Asana" + encodedRedirectUrl);

            return Response.temporaryRedirect(AsanaConnectHelper.buildOAuthAuthorizeRequest(encodedRedirectUrl)).build();
        } catch (URISyntaxException e) {
            return Responses.badRequest(
                    "The requrired parameter " + Constants.CLIENT_REDIRECT_URL_PARAM
                            + " is missing or not valid"
            );
        } catch (UnsupportedEncodingException uee){
            return Responses.badRequest(
                    "Url Encode Exception"
            );
        }

    }




}

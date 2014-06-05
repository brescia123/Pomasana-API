package provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

public abstract class AbstractProvider {

    @Context
    UriInfo uriInfo;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public AbstractProvider() {
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response get(@PathParam("id") String idString) {

        Long id;

        try {
            id = Long.valueOf(idString);
        } catch (NumberFormatException n) {
            return Response.status(Response.Status.BAD_REQUEST).entity(idString + " is not an ID")
                    .build();
        }


        return Response.ok().build();

    }



}

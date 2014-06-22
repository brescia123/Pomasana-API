package provider;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import asana.AsanaApiClient;
import asana.AsanaTaskJson;
import exceptions.AsanaProblemException;
import helper.Authorization;
import helper.Responses;
import model.User;

@Path("/asana")
public class AsanaTaskProvider {

    AsanaApiClient asanaApiClient;

    @Context
    UriInfo uriInfo;

    @GET
    @Path("/projects")
    @Produces("application/json")
    public Response getProjects(@Context HttpHeaders hh) {

        User authUser = Authorization.authorize(hh);

        if (authUser != null) {

            JsonElement projects = null;

            projects = AsanaApiClient.build(authUser).getProjects();

            return Responses.ok(projects);
        } else {
            return Responses.notAuthorized();
        }

    }


    @GET
    @Path("/projects/{id}")
    @Produces("application/json")
    public Response getProjectTasks(@Context HttpHeaders hh,
            @PathParam("id") String workspaceIdString) {

        try {

            Long workspaceId = Long.valueOf(workspaceIdString);

            User authUser = Authorization.authorize(hh);

            if (authUser != null) {


                ArrayList<AsanaTaskJson> asanaTaskJsons = AsanaApiClient.build(authUser).getProjectTasks(workspaceId);

                JsonArray asanaTaskJsonArray = new JsonArray();

                for(AsanaTaskJson asanaTaskJson : asanaTaskJsons){
                    asanaTaskJsonArray.add(asanaTaskJson.getJsonRepresentation(uriInfo));
                }

                return Responses.ok(asanaTaskJsonArray);

            } else {
                return Responses.notAuthorized();
            }

        } catch (NumberFormatException nfe) {
            return Responses.invalidId(workspaceIdString);
        }

    }

}

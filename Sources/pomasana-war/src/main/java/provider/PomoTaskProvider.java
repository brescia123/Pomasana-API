package provider;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import asana.AsanaApiClient;
import dao.PomoTaskDao;
import helper.Authorization;
import helper.ParameterValidator;
import helper.Responses;
import model.PomoTask;
import model.Pomodoro;
import model.User;

@Path("/pomotasks")
public class PomoTaskProvider {

    private static final String USER_ID = "userId";

    private static final String ID = "id";

    @Context
    UriInfo uriInfo;

    private final PomoTaskDao pomoTaskDao = new PomoTaskDao();

    private final JsonParser jsonParser = new JsonParser();


    public PomoTaskProvider() {
    }


    @GET
    @Path("/{id}/pomodori")
    @Produces("application/json")
    public Response get(@Context HttpHeaders hh, @PathParam("id") String idString) {

        Long id = ParameterValidator.validateId(idString);

        PomoTask pomoTask = pomoTaskDao.find(id);


        /*
        Check if pomotask exists
         */
        if (pomoTask == null) {
            return Responses.notFound(idString);
        }


        /*
        Check Auth
         */
        User authUser = Authorization.authorize(hh);

        Authorization.ownPomoTasks(authUser, pomoTask);

        JsonArray pomodoroJsonObjectsArray = new JsonArray();

        for (Pomodoro pomodoro : pomoTask.getUsedPomodori()) {
            pomodoroJsonObjectsArray.add(pomodoro.getJsonRepresentation(uriInfo));
        }

        return Responses.ok(pomodoroJsonObjectsArray);

    }



    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response getPomoTaskPomodori(@Context HttpHeaders hh, @PathParam("id") String idString) {

        Long id = ParameterValidator.validateId(idString);

        PomoTask pomoTask = pomoTaskDao.find(id);


        /*
        Check if pomotask exists
         */
        if (pomoTask == null) {
            return Responses.notFound(idString);
        }


        /*
        Check Auth
         */
        User authUser = Authorization.authorize(hh);

        Authorization.ownPomoTasks(authUser, pomoTask);

        pomoTask = AsanaApiClient.build(authUser).syncWithAsana(pomoTask);

        return Responses.ok(pomoTask.getJsonRepresentation(uriInfo));

    }


    @GET
    @Produces("application/json")
    public Response getAll(@Context HttpHeaders hh,
            @QueryParam(PomoTask.COMPLETED) Boolean completed) {


        /*
        Check Auth
         */
        User authUser = Authorization.authorize(hh);

        List<PomoTask> pomoTaskList = pomoTaskDao.findByUser(authUser);

        JsonArray pomoTasksJsonArray = new JsonArray();

        for (PomoTask pomoTask : pomoTaskList) {

            //pomoTask = AsanaApiClient.build(authUser).syncWithAsana(pomoTask);

            if (completed != null) {
                if (pomoTask.isCompleted() == completed) {
                    pomoTasksJsonArray.add(pomoTask.getJsonRepresentation(uriInfo));
                }
            } else {
                pomoTasksJsonArray.add(pomoTask.getJsonRepresentation(uriInfo));
            }
        }

        return Responses.ok(pomoTasksJsonArray);

    }




    @POST
    @Consumes({"application/json", "text/json"})
    @Produces("application/json")
    public Response post(@Context HttpHeaders hh, String body) {

        /*
        Check Auth
         */
        User authUser = Authorization.authorize(hh);

        /*
        Validate body
         */
        PomoTask newPomoTask = ParameterValidator.validateNewPomoTask(body,authUser,pomoTaskDao);


        /*
        Update asana
         */
        AsanaApiClient.build(authUser).updateTask(newPomoTask);

        newPomoTask = AsanaApiClient.build(authUser).syncWithAsana(newPomoTask);
        /*
        Create the new PomoTask
         */
        PomoTask createdPomoTask = pomoTaskDao.persist(newPomoTask);


        /*
        Create the Response
         */

        URI createdPomoTaskUri = createdPomoTask.getUri(uriInfo);

        return Responses.created(createdPomoTaskUri,
                createdPomoTask.getJsonRepresentation(uriInfo));

    }


    @PUT
    @Path("/{id}")
    @Consumes({"application/json", "text/json"})
    @Produces("application/json")
    public Response update(@Context HttpHeaders hh, @PathParam("id") String idString, String body) {


        Long id = ParameterValidator.validateId(idString);

        /*
        Load PomoTask from Db
        */
        PomoTask pomoTask = pomoTaskDao.find(id);

        /*
        Check if PomoTask exists
         */
        if (pomoTask == null) {
            return Responses.notFound(idString);
        }

         /*
        Check Auth
         */
        User authUser = Authorization.authorize(hh);

        Authorization.ownPomoTasks(authUser, pomoTask);

        /*
        Validate body
         */
        PomoTask updatedPomoTask = ParameterValidator.validateUpdatePomoTask(body,pomoTask,authUser);


        /*
        Update Asana
         */
        updatedPomoTask = AsanaApiClient.build(authUser).updateTask(updatedPomoTask);

        /*
        Update Db
         */
        updatedPomoTask = pomoTaskDao.persist(updatedPomoTask);


        /*
        Create the Response
         */
        URI updatedPomoTaskUri = updatedPomoTask.getUri(uriInfo);

        return Responses
                .created(updatedPomoTaskUri, updatedPomoTask.getJsonRepresentation(uriInfo));


    }


    /*
    By default it delete the task also from asana, otherwise it checks the parameter delete_asana
     */

    @DELETE
    @Path("/{id}")
    @Produces("application/json")
    public Response delete(@Context HttpHeaders hh, @PathParam("id") String idString, @QueryParam("delete_asana") Boolean deleteAsana) {

        Long id = ParameterValidator.validateId(idString);


        /*
        Load PomoTask from Db
        */
        PomoTask pomoTask = pomoTaskDao.find(id);

        /*
        Check if PomoTask exists
         */
        if (pomoTask == null) {
            return Responses.notFound(idString);
        }

        /*
        Check Auth
         */
        User authUser = Authorization.authorize(hh);

        Authorization.ownPomoTasks(authUser, pomoTask);

        if(deleteAsana != null && deleteAsana){
            AsanaApiClient.build(authUser).deleteTask(id);
        }

        pomoTaskDao.delete(pomoTask);

        return Responses.ok();

    }
}

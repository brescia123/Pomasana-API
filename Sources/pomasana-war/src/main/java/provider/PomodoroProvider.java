package provider;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dao.PomoTaskDao;
import dao.PomodoroDao;
import helper.Authorization;
import helper.Responses;
import model.PomoTask;
import model.Pomodoro;
import model.User;

@Path("/pomodori")
public class PomodoroProvider {


    private static final String POMO_TASK_ID = "pomoTaskId";


    @Context
    UriInfo uriInfo;


    private final PomodoroDao pomodoroDao = new PomodoroDao();

    private final PomoTaskDao pomoTaskDao = new PomoTaskDao();



    /*
    Constructor
     */

    public PomodoroProvider() {
    }



    /*
    HTTP Methods
     */

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response get(@Context HttpHeaders hh, @PathParam("id") String idString) {

        try {
            Long id = Long.valueOf(idString);

            Pomodoro pomodoro = pomodoroDao.find(id);

            /*
            Check if pomotask exists
             */
            if (pomodoro == null) {
                return Responses.notFound(idString);
            }

           /*
          Check Auth
           */
            User authUser = Authorization.authorize(hh);

            boolean owner = Authorization.ownPomodoro(authUser, pomodoro);

            if (authUser == null || !owner) {
                return Responses.notAuthorized();
            }

            return Responses.ok(pomodoro.getJsonRepresentation(uriInfo));

        } catch (NumberFormatException nfe) {
            return Responses.invalidId(idString);
        }

    }

    @GET
    @Produces("application/json")
    public Response getAll(@Context HttpHeaders hh) {

        /*
        Check Auth
         */
        User authUser = Authorization.authorize(hh);

        if (authUser == null) {
            return Responses.notAuthorized();
        }

        List<Pomodoro> pomodoroList = pomodoroDao.findByUser(authUser);

        JsonArray pomodoroJsonObjectsAttay = new JsonArray();

        for (Pomodoro pomodoro : pomodoroList) {
            pomodoroJsonObjectsAttay.add(pomodoro.getJsonRepresentation(uriInfo));
        }

        return Responses.ok(pomodoroJsonObjectsAttay);

    }

    @POST
    @Consumes({"application/json", "text/json"})
    @Produces("application/json")
    public Response post(@Context HttpHeaders hh, String body) {

        JsonParser jsonParser = new JsonParser();

        try {
            JsonElement jsonElement = jsonParser.parse(body);

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            /*
            Check mandatory fields
             */
            if (jsonObject.has(POMO_TASK_ID)) {

                Pomodoro newPomodoro = new Pomodoro();

                /*
                Check Auth
                 */
                User authUser = Authorization.authorize(hh);

                PomoTask relatedPomoTask = pomoTaskDao
                        .find(jsonObject.get(POMO_TASK_ID).getAsLong());

                boolean owner = Authorization.ownPomoTasks(authUser, relatedPomoTask);

                if (authUser == null || !owner) {
                    return Responses.notAuthorized();
                }

                newPomodoro.setPomoTask(relatedPomoTask);

                /*
                Check read-only fields
                 */

                if (jsonObject.has(Pomodoro.COMPLETED_AT)) {

                    return Responses.readOnlyJsonField(Pomodoro.COMPLETED_AT);

                }

                /*
                Check optional fields
                 */

                if (jsonObject.has(Pomodoro.EXT_INTERRUPT)) {
                    try {
                        newPomodoro
                                .setExtInterrupt(jsonObject.get(Pomodoro.EXT_INTERRUPT).getAsInt());
                    } catch (NumberFormatException nfe) {
                        return Responses.invalidJsonField(Pomodoro.EXT_INTERRUPT);
                    }
                }

                if (jsonObject.has(Pomodoro.INT_INTERRUPT)) {
                    try {
                        newPomodoro
                                .setIntInterrupt(jsonObject.get(Pomodoro.INT_INTERRUPT).getAsInt());
                    } catch (NumberFormatException nfe) {
                        return Responses.invalidJsonField(Pomodoro.INT_INTERRUPT);
                    }
                }

                if (jsonObject.has(Pomodoro.NOTES)) {
                    newPomodoro.setNotes(jsonObject.get(Pomodoro.NOTES).getAsString());
                }


                /*
                Create the new Pomodoro
                 */
                Pomodoro createdPomodoro = pomodoroDao.persist(newPomodoro);


                /*
                Create the Response
                 */

                URI createdPomodoroUri = createdPomodoro.getUri(uriInfo);

                return Responses.created(createdPomodoroUri,
                        createdPomodoro.getJsonRepresentation(uriInfo));

            } else {
                return Responses.missingJsonField(POMO_TASK_ID);
            }
        } catch (JsonParseException jpe) {
            return Responses.invalidJson();
        } catch (IllegalStateException ise) {
            return Responses.notJsonObject();
        }

    }


    @PUT
    @Path("/{id}")
    @Consumes({"application/json", "text/json"})
    @Produces("application/json")
    public Response update(@Context HttpHeaders hh, @PathParam("id") String idString, String body) {

        JsonParser jsonParser = new JsonParser();

        try {

            Long id = Long.valueOf(idString);

            JsonElement jsonElement = jsonParser.parse(body);

            JsonObject jsonObject = jsonElement.getAsJsonObject();

            /*
            Check read-only fields
             */

            if (jsonObject.has(Pomodoro.COMPLETED_AT)) {

                return Responses.readOnlyJsonField(Pomodoro.COMPLETED_AT);

            }

            /*
            Load Pomdoro from Db
             */

            Pomodoro pomodoro = pomodoroDao.find(id);



            /*
            Check if Pomodoro exists
             */
            if (pomodoro == null) {
                return Responses.notFound(idString);
            }


            /*
            Check Auth
             */
            User authUser = Authorization.authorize(hh);

            boolean owner = Authorization.ownPomodoro(authUser, pomodoro);

            if (authUser == null || !owner) {
                return Responses.notAuthorized();
            }

            /*
            Check optional fields
             */

            if (jsonObject.has(Pomodoro.EXT_INTERRUPT)) {
                try {
                    pomodoro.setExtInterrupt(jsonObject.get(Pomodoro.EXT_INTERRUPT).getAsInt());
                } catch (NumberFormatException nfe) {
                    return Responses.invalidJsonField(Pomodoro.EXT_INTERRUPT);
                }
            }

            if (jsonObject.has(Pomodoro.INT_INTERRUPT)) {
                try {
                    pomodoro.setIntInterrupt(jsonObject.get(Pomodoro.INT_INTERRUPT).getAsInt());
                } catch (NumberFormatException nfe) {
                    return Responses.invalidJsonField(Pomodoro.INT_INTERRUPT);
                }
            }

            if (jsonObject.has(Pomodoro.NOTES)) {
                pomodoro.setNotes(jsonObject.get(Pomodoro.NOTES).getAsString());
            }


            /*
            Update the Pomodoro
             */
            Pomodoro updatedPomodoro = pomodoroDao.persist(pomodoro);


            /*
            Create the Response
             */

            URI createdPomodoroUri = updatedPomodoro.getUri(uriInfo);

            return Responses
                    .created(createdPomodoroUri, updatedPomodoro.getJsonRepresentation(uriInfo));

        } catch (JsonParseException jpe) {
            return Responses.invalidJson();
        } catch (IllegalStateException ise) {
            return Responses.notJsonObject();
        } catch (NumberFormatException nfe) {
            return Responses.invalidId(idString);
        }

    }


    @DELETE
    @Path("/{id}")
    @Produces("application/json")
    public Response delete(@Context HttpHeaders hh, @PathParam("id") String idString) {

        try {

            Long id = Long.valueOf(idString);

            /*
            Load Pomodoro from Db
            */

            Pomodoro pomodoro = pomodoroDao.find(id);

            /*
            Check if Pomodoro exists
             */
            if (pomodoro == null) {
                return Responses.notFound(idString);
            }

            /*
            Check Auth
             */
            User authUser = Authorization.authorize(hh);

            boolean owner = Authorization.ownPomodoro(authUser, pomodoro);

            if (authUser == null || !owner) {
                return Responses.notAuthorized();
            }

            pomodoroDao.delete(pomodoro);

            return Responses.ok();

        } catch (NumberFormatException nfe) {
            return Responses.invalidId(idString);
        }

    }
}

package provider;


import com.google.gson.JsonArray;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import dao.PomoTaskDao;
import dao.UserDao;
import helper.Authorization;
import helper.Responses;
import model.PomoTask;
import model.User;

@Path("/users")
public class UserProvider {

    @Context
    UriInfo uriInfo;

    private final UserDao userDao = new UserDao();


    public UserProvider() {
    }


    @GET
    @Path("/{id}/pomotasks")
    @Produces("application/json")
    public Response getMePomoTasks(@Context HttpHeaders hh, @PathParam("id") String idString,
            @QueryParam(PomoTask.COMPLETED) Boolean completed) {

        User authUser = Authorization.authorize(hh);

        if ("me".equals(idString)) {
            if (authUser != null) {
                PomoTaskDao pomoTaskDao = new PomoTaskDao();

                List<PomoTask> pomoTaskList = pomoTaskDao.findByUser(authUser);

                JsonArray pomoTasksJsonArray = new JsonArray();

                for (PomoTask pomoTask : pomoTaskList) {

                    if (completed != null) {
                        if (pomoTask.isCompleted() == completed) {
                            pomoTasksJsonArray.add(pomoTask.getJsonRepresentation(uriInfo));
                        }
                    } else {
                        pomoTasksJsonArray.add(pomoTask.getJsonRepresentation(uriInfo));
                    }
                }

                return Responses.ok(pomoTasksJsonArray);
            } else {
                return Responses.notAuthorized();
            }

        } else {

            try {
                Long id = Long.valueOf(idString);

                if (authUser != null && authUser.getId().equals(id)) {
                    PomoTaskDao pomoTaskDao = new PomoTaskDao();

                    List<PomoTask> pomoTaskList = pomoTaskDao.findByUser(authUser);

                    JsonArray pomoTasksJsonArray = new JsonArray();

                    for (PomoTask pomoTask : pomoTaskList) {

                        if (completed != null) {
                            if (pomoTask.isCompleted() == completed) {
                                pomoTasksJsonArray.add(pomoTask.getJsonRepresentation(uriInfo));
                            }
                        } else {
                            pomoTasksJsonArray.add(pomoTask.getJsonRepresentation(uriInfo));
                        }
                    }

                    return Responses.ok(pomoTasksJsonArray);
                } else {
                    return Responses.notAuthorized();
                }

            } catch (NumberFormatException nfe) {
                return Responses.invalidId(idString);
            }

        }

    }


    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response get(@Context HttpHeaders hh, @PathParam("id") String idString) {

        User authUser = Authorization.authorize(hh);

        if ("me".equals(idString)) {
            if (authUser != null) {
                return Responses.ok(authUser.getJsonRepresentation(uriInfo));
            } else {
                return Responses.notAuthorized();
            }

        } else {

            try {
                Long id = Long.valueOf(idString);

                if (authUser != null && authUser.getId().equals(id)) {
                    return Responses.ok(authUser.getJsonRepresentation(uriInfo));
                } else {
                    return Responses.notAuthorized();
                }

            } catch (NumberFormatException nfe) {
                return Responses.invalidId(idString);
            }

        }
    }


    @DELETE
    @Path("/{id}")
    public Response deleteMe(@Context HttpHeaders hh, @PathParam("id") String idString) {

        User authUser = Authorization.authorize(hh);

        if ("me".equals(idString)) {
            if (authUser != null) {
                userDao.delete(authUser);
                return Responses.ok();
            } else {
                return Responses.notAuthorized();


            }

        } else {

            try {
                Long id = Long.valueOf(idString);

                if (authUser != null && authUser.getId().equals(id)) {
                    userDao.delete(authUser);
                    return Responses.ok();
                } else {
                    return Responses.notAuthorized();
                }

            } catch (NumberFormatException nfe) {
                return Responses.invalidId(idString);
            }
        }
    }

}

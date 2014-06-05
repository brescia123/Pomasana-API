package asana;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import dao.PomoTaskDao;
import exceptions.AsanaProblemException;
import helper.Constants;
import model.PomoTask;
import model.User;

public class AsanaApiClient {

    private User user;

    private WebTarget target;

    private JsonParser jsonParser;

    private Gson gson;

    private AsanaApiClient(User user) {
        this.user = user;
        Client client = ClientBuilder.newClient();
        target = client.target(Constants.ASANA_API_URL);
        jsonParser = new JsonParser();
        gson = new Gson();
    }

    public static AsanaApiClient build(User user) {
        return new AsanaApiClient(user);
    }

    /**
     * Return a list of asana projects of the user
     *
     * @return A JsonElement returned by Asana
     */
    public JsonElement getProjects() throws AsanaProblemException {

        WebTarget projectsWebTarget = target.path("projects");

        Invocation.Builder invocationBuilder = projectsWebTarget.request();

        Response asanaResponse = get(invocationBuilder);

        if (isError(asanaResponse)) {
            throw new AsanaProblemException(parseResponse(asanaResponse));
        }

        return parseResponse(asanaResponse);
    }

    /**
     * Get tasks from a specified project in Asana
     *
     * @param projectId The project id
     * @return A JsonElement returned by Asana
     * @throws AsanaProblemException If there is a problem with communicating with Asana
     */
    public ArrayList<AsanaTaskJson> getProjectTasks(Long projectId) throws AsanaProblemException {

        WebTarget projectsWebTarget = target
                .path("projects").path(projectId.toString())
                .path("tasks")
                .queryParam("opt_fields", "name,due_on,completed")
                .queryParam("completed_since", "now");

        Invocation.Builder invocationBuilder = projectsWebTarget.request();

        Response asanaResponse = get(invocationBuilder);

        if (isError(asanaResponse)) {
            throw new AsanaProblemException(parseResponse(asanaResponse));
        }

        return parseAsanaTasks(parseResponse(asanaResponse));

    }

    /**
     * Get a specified task from Asana
     *
     * @param taskId The asana task id
     * @return A JsonElement returned by Asana
     * @throws AsanaProblemException If there is a problem with communicating with Asana
     */
    public JsonElement getTask(Long taskId) throws AsanaProblemException {

        WebTarget projectsWebTarget = target
                .path("tasks")
                .path(taskId.toString());

        Invocation.Builder invocationBuilder = projectsWebTarget.request();

        Response asanaResponse = get(invocationBuilder);

        if (isError(asanaResponse)) {
            throw new AsanaProblemException(parseResponse(asanaResponse));
        }

        return parseResponse(asanaResponse);

    }


    /**
     * Update the given PomoTask with the corrisponding asana task
     *
     * @param pomoTask The PomoTask to be updated on the Asana system
     * @throws exceptions.AsanaProblemException If there is a problem with communicating with Asana
     */
    public PomoTask updateTask(PomoTask pomoTask) throws AsanaProblemException {

        WebTarget projectsWebTarget = target
                .path("tasks")
                .path(pomoTask.getId().toString());

        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest(pomoTask.getName(),
                pomoTask.isCompleted());

        Invocation.Builder invocationBuilder = projectsWebTarget.request();

        Response response = put(invocationBuilder, updateTaskRequest);

        if (isError(response)) {
            throw new AsanaProblemException(parseResponse(response));
        }

        return pomoTask;

    }


    /**
     * Sync the given Pomotask with corresponding asana task updating its name and state
     *
     * @param pomoTask The PomoTask to be synchronized on the Asana system
     * @return The synchronized PomoTask
     * @throws AsanaProblemException If there is a problem with communicating with Asana
     */
    public PomoTask syncWithAsana(PomoTask pomoTask) throws AsanaProblemException {

        Long id = pomoTask.getId();

        User user = pomoTask.getUser();

        /*
        Check the id and user
         */
        if (id == null || user == null) {
            throw new IllegalArgumentException();
        }

        /*
        Get task from asana
         */

        WebTarget projectsWebTarget = target
                .path("tasks")
                .path(pomoTask.getId().toString());

        Invocation.Builder invocationBuilder = projectsWebTarget.request();

        Response asanaResponse = get(invocationBuilder);


        /*
        Check the answer
         */
        if (isError(asanaResponse)) {
            throw new AsanaProblemException(parseResponse(asanaResponse));
        }

        JsonObject responseJsonObject = parseResponse(asanaResponse).getAsJsonObject();

        JsonObject taskJsonObject = responseJsonObject.get("data").getAsJsonObject();

        pomoTask.setCompleted(taskJsonObject.get(UpdateTaskRequest.COMPLETED).getAsBoolean());
        pomoTask.setName(taskJsonObject.get(UpdateTaskRequest.NAME).getAsString());

        PomoTaskDao pomoTaskDao = new PomoTaskDao();

        return pomoTaskDao.persist(pomoTask);
    }


    public void deleteTask(Long id) throws AsanaProblemException {

        WebTarget projectsWebTarget = target
                .path("tasks")
                .path(id.toString());

        Invocation.Builder invocationBuilder = projectsWebTarget.request();

            Response asanaResponse = delete(invocationBuilder);

        if (isError(asanaResponse)) {
            throw new AsanaProblemException(parseResponse(asanaResponse));
        }

    }


    /*
    Private methods
     */
    private Response get(Invocation.Builder invocationBuilder) {
        /*
        Check the validity of the token
         */
        if (System.currentTimeMillis() > user.getExpiresAt()) {
            AsanaConnectHelper.renewToken(user);
        }

        invocationBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());

        try {
            return invocationBuilder.get();
        } catch (ProcessingException e) {
            throw new AsanaProblemException(gson.toJsonTree(e.getMessage()));
        }
    }

    private Response delete(Invocation.Builder invocationBuilder) {
        /*
        Check the validity of the token
         */
        if (System.currentTimeMillis() > user.getExpiresAt()) {
            AsanaConnectHelper.renewToken(user);
        }

        invocationBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());

        try {
            return invocationBuilder.delete();
        } catch (ProcessingException e) {
            throw new AsanaProblemException(gson.toJsonTree(e.getMessage()));
        }
    }

    private Response put(Invocation.Builder invocationBuilder,
            UpdateTaskRequest updateTaskRequest) {
        /*
        Check the validity of the token
         */
        if (System.currentTimeMillis() > user.getExpiresAt()) {
            AsanaConnectHelper.renewToken(user);
        }

        try {
            invocationBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());

            return invocationBuilder.put(
                    Entity.entity(gson.toJson(updateTaskRequest.getJson()),
                            MediaType.APPLICATION_JSON)
            );
        } catch (Exception e) {
            throw new AsanaProblemException(gson.toJsonTree(e.getMessage()));
        }

    }

    private ArrayList<AsanaTaskJson> parseAsanaTasks(JsonElement jsonElement){

        JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();

        ArrayList<AsanaTaskJson> asanaTaskJsons = new ArrayList<AsanaTaskJson>();

        for(JsonElement jsonElement1 : data.getAsJsonArray()){
            JsonObject jsonObject = jsonElement1.getAsJsonObject();
            AsanaTaskJson asanaTaskJson = new AsanaTaskJson();
            asanaTaskJson.setId(jsonObject.get("id").getAsLong());
            asanaTaskJson.setName(jsonObject.get("name").getAsString());
            asanaTaskJson.setCompleted(jsonObject.get("completed").getAsBoolean());
            if(!jsonObject.get("due_on").isJsonNull()) {
                asanaTaskJson.setDueOn(DateTimeFormat.forPattern("yyyy-MM-dd")
                        .parseDateTime(jsonObject.get("due_on").getAsString()));
            }
            asanaTaskJsons.add(asanaTaskJson);
        }

        return asanaTaskJsons;

    }

    private JsonElement parseResponse(Response response) {
        String asanaResponse = response.readEntity(String.class);

        return jsonParser.parse(asanaResponse);
    }


    private boolean isError(Response response) {
        Integer status = response.getStatus();

        if (status.toString().startsWith("4") ||
                status.toString().startsWith("5")) {
            return true;
        }

        return false;
    }

}

package helper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URI;

import javax.ws.rs.core.Response;

public class Responses {

    private static Gson gson = new Gson();

    public static Response ok() {
        return Response.ok().build();
    }



    /*
    ERRORS
     */

    public static Response invalidId(String idString) {
        return Response.status(Response.Status.BAD_REQUEST).entity(
                buildJsonError(idString + " is not an ID"))
                .build();
    }

    public static Response invalidParameter(String idString) {
        return Response.status(Response.Status.BAD_REQUEST).entity(
                buildJsonError(idString + " is not valid"))
                .build();
    }

    public static Response invalidJson() {
        return Response.status(Response.Status.BAD_REQUEST).entity(buildJsonError("Invalid Json")).build();
    }

    public static Response notJsonObject() {
        return Response.status(Response.Status.BAD_REQUEST).entity(
                buildJsonError("Not a Json Object")).build();
    }

    public static Response invalidJsonField(String fieldName) {
        return Response.status(Response.Status.BAD_REQUEST).entity(
                buildJsonError("Invalid Json Field: " + fieldName))
                .build();
    }

    public static Response missingJsonField(String missingField) {
        return Response.status(Response.Status.BAD_REQUEST).entity(
                buildJsonError("Missing Field: " + missingField))
                .build();
    }

    public static Response readOnlyJsonField(String field) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(buildJsonError("You cannot override " + field + " field"))
                .build();
    }


    public static Response notFound(String idString) {
        return Response.status(Response.Status.NOT_FOUND).entity(buildJsonError(
                "ID: " + idString + " NOT FOUND")).build();
    }

    public static Response wrongRoute(String rightRoute) {
        return Response.status(Response.Status.BAD_REQUEST).entity(buildJsonError(
                "Not allowed. Use: " + rightRoute)).build();
    }


    public static Response badRequest(String message){
        return Response.status(Response.Status.BAD_REQUEST).entity(buildJsonError(message)).build();
    }

    public static Response asanaProblem(JsonElement error){
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(buildJsonAsanaError(error)).build();
    }

    public static Response notAuthorized(){
        return Response.status(Response.Status.UNAUTHORIZED).entity(buildJsonError("Not Authorized")).build();
    }



    /*
    OK RESPONSES
     */

    public static Response ok(JsonElement jsonElement) {
        return Response.ok().entity(buildJsonData(jsonElement)).build();
    }


    public static Response created(URI resourceURI, JsonElement resourceRepresentation) {
        return Response.created(resourceURI).entity(buildJsonData(resourceRepresentation)).build();

    }


    private static String buildJsonError(String message){
        JsonObject jsonError = new JsonObject();
        JsonObject jsonMessage = new JsonObject();

        jsonMessage.addProperty("msg", message);
        jsonError.add("error", jsonMessage);
        return gson.toJson(jsonError);
    }

    private static String buildJsonAsanaError(JsonElement jsonElement){
        JsonObject jsonError = new JsonObject();
        JsonObject jsonMessage = new JsonObject();

        jsonMessage.addProperty("msg", "Asana has some problems");
        jsonMessage.add("asanaResponse", jsonElement);
        jsonError.add("error", jsonMessage);
        return gson.toJson(jsonError);
    }

    private static String buildJsonData(JsonElement message){
        JsonObject jsonData = new JsonObject();

        jsonData.add("data", message);
        return gson.toJson(jsonData);
    }


}

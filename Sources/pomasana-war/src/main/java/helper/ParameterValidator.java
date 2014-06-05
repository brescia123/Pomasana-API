package helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;

import javax.ws.rs.WebApplicationException;

import dao.PomoTaskDao;
import exceptions.IllegalParameterException;
import exceptions.MissingParameterException;
import exceptions.ReadOnlyParameterException;
import model.PomoTask;
import model.User;

public class ParameterValidator {


    private static final String USER_ID = "userId";

    private static final String ID = "id";

    private final static JsonParser jsonParser = new JsonParser();


    public static Long validateId(String idString) {
        try {
            return Long.valueOf(idString);
        } catch (NumberFormatException e) {
            throw new IllegalParameterException(idString);
        }
    }

    public static PomoTask validateNewPomoTask(String body, User authUser, PomoTaskDao pomoTaskDao)
            throws WebApplicationException {

        JsonObject jsonObject = validateJson(body);

        /*
        Check mandatory fields ([asana]id)
         */
        if (!jsonObject.has(ID)) {
            throw new MissingParameterException(ID);
        }

        PomoTask newPomoTask = new PomoTask();


        /*
        Check if id is correct
         */

        Long id = validateId(jsonObject.get(ID).getAsString());

        if (pomoTaskDao.existsById(id)) {
            throw new WebApplicationException(Responses.wrongRoute("PUT ../pomotask/{id}"));
        }

        newPomoTask.setId(id);

        newPomoTask.setUser(authUser);

        /*
        Check read-only fields
         */

        if (jsonObject.has(PomoTask.COMPLETED_AT)) {
            throw new ReadOnlyParameterException(PomoTask.COMPLETED_AT);
        }
        if (jsonObject.has(PomoTask.CREATED_AT)) {
            throw new ReadOnlyParameterException(PomoTask.CREATED_AT);
        }
        if (jsonObject.has(PomoTask.MODIFIED_AT)) {
            throw new ReadOnlyParameterException(PomoTask.MODIFIED_AT);
        }

        /*
        Check optional fields
         */

        if (jsonObject.has(PomoTask.COMPLETED)) {
            try {
                newPomoTask.setCompleted(jsonObject.get(PomoTask.COMPLETED).getAsBoolean());
            } catch (ClassCastException cce) {
                throw new IllegalParameterException(PomoTask.COMPLETED);
            }
        }

        if (jsonObject.has(PomoTask.ESTIMATED_POMODORI)) {
            try {
                ArrayList<Integer> estimatedPomodori = new ArrayList<Integer>();
                estimatedPomodori
                        .add(jsonObject.get(PomoTask.ESTIMATED_POMODORI).getAsInt());
                newPomoTask.setEstimatedPomodori(estimatedPomodori);
            } catch (NumberFormatException nfe) {
                throw new IllegalParameterException(PomoTask.ESTIMATED_POMODORI);
            }
        }

        if (jsonObject.has(PomoTask.NAME)) {
            try {
                newPomoTask.setName(jsonObject.get(PomoTask.NAME).getAsString());
            } catch (NumberFormatException nfe) {
                throw new IllegalParameterException(PomoTask.NAME);
            }
        }

        return newPomoTask;

    }

    public static PomoTask validateUpdatePomoTask(String body, PomoTask pomoTask, User authUser)
            throws WebApplicationException {

        JsonObject jsonObject = validateJson(body);


            /*
            Check read-only fields
             */

        if (jsonObject.has(PomoTask.COMPLETED_AT)) {
            throw new ReadOnlyParameterException(PomoTask.COMPLETED_AT);
        }
        if (jsonObject.has(PomoTask.CREATED_AT)) {
            throw new ReadOnlyParameterException(PomoTask.CREATED_AT);
        }
        if (jsonObject.has(PomoTask.MODIFIED_AT)) {
            throw new ReadOnlyParameterException(PomoTask.MODIFIED_AT);
        }
        if (jsonObject.has(PomoTask.USED_POMODORI)) {
            throw new ReadOnlyParameterException(PomoTask.USED_POMODORI);
        }
        if (jsonObject.has(USER_ID)) {
            throw new ReadOnlyParameterException(USER_ID);
        }



        /*
        Check optional fields
         */
        if (jsonObject.has(PomoTask.COMPLETED)) {
            try {
                pomoTask.setCompleted(jsonObject.get(PomoTask.COMPLETED).getAsBoolean());
            } catch (ClassCastException cce) {
                throw new IllegalParameterException(PomoTask.COMPLETED);
            }
        }

        if (jsonObject.has(PomoTask.ESTIMATED_POMODORI)) {
            try {
                ArrayList<Integer> estimatedPomodori = new ArrayList<Integer>(
                        pomoTask.getEstimatedPomodori());
                estimatedPomodori.add(jsonObject.get(PomoTask.ESTIMATED_POMODORI).getAsInt());
                pomoTask.setEstimatedPomodori(estimatedPomodori);
            } catch (NumberFormatException nfe) {
                throw new IllegalParameterException(PomoTask.ESTIMATED_POMODORI);
            }
        }

        if (jsonObject.has(PomoTask.NAME)) {
            try {
                pomoTask.setName(jsonObject.get(PomoTask.NAME).getAsString());
            } catch (NumberFormatException nfe) {
                throw new IllegalParameterException(PomoTask.NAME);
            }
        }

        return pomoTask;
    }


    private static JsonObject validateJson(String body) {
        try {
            JsonElement jsonElement = jsonParser.parse(body);

            return jsonElement.getAsJsonObject();

        } catch (JsonSyntaxException jse) {
            throw new WebApplicationException(Responses.invalidJson());
        } catch (IllegalStateException ise) {
            throw new WebApplicationException(Responses.notJsonObject());
        }
    }
}

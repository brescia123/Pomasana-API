package asana;


import com.google.gson.JsonObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class UpdateTaskRequest {

    public static final String NAME = "name";

    public static final String COMPLETED = "completed";

    private String name;

    private Boolean completed;

    public UpdateTaskRequest(String name, Boolean completed) {
        this.name = name;
        this.completed = completed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public JsonObject getJson() {

        JsonObject requestJsonObject = new JsonObject();

        JsonObject data = new JsonObject();


        if (name != null) {
            data.addProperty("name", name);
        }
        if (completed != null) {
            data.addProperty("completed", completed);
        }


        requestJsonObject.add("data", data);


        return requestJsonObject;
    }

}


package asana;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import javax.annotation.Generated;
import javax.ws.rs.core.UriInfo;

import dao.PomoTaskDao;
import model.PomoTask;

@Generated("org.jsonschema2pojo")
public class AsanaTaskJson {

    private static final PomoTaskDao pomoTaskDao = new PomoTaskDao();

    @Expose
    private Long id;

    @Expose
    private String name;

    @Expose
    private Boolean completed;

    @Expose
    @SerializedName("due_on")
    private DateTime dueOn;

    private PomoTask associatedPomoTask;

    public AsanaTaskJson(Long id) {
        this.id = id;
    }

    public AsanaTaskJson() {
    }

    public DateTime getDueOn() {
        return dueOn;
    }

    public void setDueOn(DateTime dueOn) {
        this.dueOn = dueOn;
    }

    public PomoTask getAssociatedPomoTask() {
        return associatedPomoTask;
    }

    public void setAssociatedPomoTask(PomoTask associatedPomoTask) {
        this.associatedPomoTask = associatedPomoTask;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }


    public JsonObject getJsonRepresentation(UriInfo uriInfo) {
        JsonObject asanaTaskJsonObject = new JsonObject();

        asanaTaskJsonObject.addProperty("id", id);

        asanaTaskJsonObject.addProperty(PomoTask.NAME, name);
        asanaTaskJsonObject.addProperty(PomoTask.COMPLETED, completed);
        if (dueOn != null) {
            asanaTaskJsonObject
                    .addProperty("due_on", dueOn.toString());
        }

        PomoTask pomoTask = pomoTaskDao.find(id);

        if (pomoTask != null){
            asanaTaskJsonObject.addProperty("related_pomotask",
                    pomoTask.getUri(uriInfo).toASCIIString());
        }

        return asanaTaskJsonObject;

    }

}

package model;

import com.google.gson.JsonObject;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import org.joda.time.DateTime;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

@Entity
public class Pomodoro {

    public static final String POMODORI = "pomodori";

    public static final String POMOTASK = "pomoTask";

    public static final String COMPLETED_AT = "completedAt";

    public static final String INT_INTERRUPT = "intInterrupt";

    public static final String EXT_INTERRUPT = "extInterrupt";

    public static final String NOTES = "notes";


    @Id
    private Long id;

    @Load
    @Index
    private Ref<PomoTask> pomoTask;

    @Index
    private Long completedAt;

    private Integer intInterrupt = 0;

    private Integer extInterrupt = 0;

    private String notes = "";

    public Pomodoro() {
    }




    public Long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Long completedAt) {
        this.completedAt = completedAt;
    }

    public DateTime getFriendlyCompletedAt() {
        if (completedAt == null) {
            return null;
        }
        return new DateTime(completedAt);
    }

    public Integer getIntInterrupt() {
        return intInterrupt;
    }

    public void setIntInterrupt(Integer intInterrupt) {
        this.intInterrupt = intInterrupt;
    }

    public Integer getExtInterrupt() {
        return extInterrupt;
    }

    public void setExtInterrupt(Integer extInterrupt) {
        this.extInterrupt = extInterrupt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public PomoTask getPomoTask() {
        return pomoTask.get();
    }

    public void setPomoTask(PomoTask pomoTask) {
        this.pomoTask = Ref.create(pomoTask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pomodoro)) {
            return false;
        }

        Pomodoro pomodoro = (Pomodoro) o;

        if (!id.equals(pomodoro.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public JsonObject getJsonRepresentation(UriInfo uriInfo) {
        JsonObject pomodoroJsonObject = new JsonObject();

        pomodoroJsonObject.addProperty("id", id);
        pomodoroJsonObject.addProperty("self_uri", getUri(uriInfo).toASCIIString());

        pomodoroJsonObject.add(PomoTask.USER,
                getPomoTask().getUser().getSimpleJsonRepresentation(uriInfo));
        pomodoroJsonObject.add(Pomodoro.POMOTASK,
                getPomoTask().getSimpleJsonRepresentation(uriInfo));
        pomodoroJsonObject.addProperty(Pomodoro.NOTES, notes);
        pomodoroJsonObject.addProperty(Pomodoro.INT_INTERRUPT, intInterrupt);
        pomodoroJsonObject.addProperty(Pomodoro.EXT_INTERRUPT, extInterrupt);
        if (completedAt != null) {
            pomodoroJsonObject
                    .addProperty(Pomodoro.COMPLETED_AT, getFriendlyCompletedAt().toString());
        }

        return pomodoroJsonObject;
    }

    public JsonObject getSimpleJsonRepresentation(UriInfo uriInfo) {

        JsonObject pomodoroSimpleJsonObject = new JsonObject();

        pomodoroSimpleJsonObject.addProperty("id", id);

        pomodoroSimpleJsonObject.addProperty("uri", getUri(uriInfo).toASCIIString());

        return pomodoroSimpleJsonObject;
    }

    public URI getUri(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(POMODORI)
                .path(id.toString())
                .build();
    }
}

package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import org.joda.time.DateTime;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriInfo;

@Entity
public class PomoTask {

    public static final String POMOTASKS = "pomotasks";

    public static final String USER = "user";

    public static final String COMPLETED = "completed";

    public static final String NAME = "name";

    public static final String USED_POMODORI = "usedPomodori";

    public static final String ESTIMATED_POMODORI = "estimatedPomodori";

    public static final String CREATED_AT = "createdAt";

    public static final String MODIFIED_AT = "modifiedAt";

    public static final String COMPLETED_AT = "completedAt";

    @Id
    private Long id;

    @Index
    private Ref<User> user;

    @Index
    private boolean completed = false;

    @Index
    private String name;

    @Load
    private List<Ref<Pomodoro>> usedPomodori = new ArrayList<Ref<Pomodoro>>();

    private List<Integer> estimatedPomodori = new ArrayList<Integer>();

    @Index
    private Long createdAt;

    @Index
    private Long modifiedAt;

    @Index
    private Long completedAt;



    public PomoTask() {

    }

    public List<Integer> getEstimatedPomodori() {
        return estimatedPomodori;
    }

    public void setEstimatedPomodori(List<Integer> estimatedPomodori) {
        this.estimatedPomodori = estimatedPomodori;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Long modifiedAt) {
        this.modifiedAt = modifiedAt;
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

    public DateTime getFriendlyModifiedAt() {
        if (modifiedAt == null) {
            return null;
        }
        return new DateTime(modifiedAt);
    }

    public DateTime getFriendlyCreatedAt() {
        if (createdAt == null) {
            return null;
        }
        return new DateTime(createdAt);
    }


    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        if (!this.completed && completed) {
            setCompletedAt(System.currentTimeMillis());
        }
        if (this.completed && !completed) {
            setCompletedAt(null);
        }
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Pomodoro> getUsedPomodori() {
        List<Pomodoro> pomodori = new ArrayList<Pomodoro>();
        for (Ref<Pomodoro> pomodoroRef : this.usedPomodori) {
            pomodori.add(pomodoroRef.get());
        }
        return pomodori;
    }

    public void setUsedPomodori(List<Pomodoro> usedPomodori) {
        List<Ref<Pomodoro>> pomodoroRefs = new ArrayList<Ref<Pomodoro>>();
        for (Pomodoro pomodoro : usedPomodori) {
            pomodoroRefs.add(Ref.create(pomodoro));
        }
        this.usedPomodori = pomodoroRefs;
    }

    public User getUser() {
        return user.get();
    }

    public void setUser(User user) {
        this.user = Ref.create(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PomoTask)) {
            return false;
        }

        PomoTask pomoTask = (PomoTask) o;

        if (!id.equals(pomoTask.id)) {
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

        pomodoroJsonObject.addProperty(COMPLETED, completed);

        pomodoroJsonObject.addProperty(NAME, name);

        pomodoroJsonObject.addProperty(ESTIMATED_POMODORI, estimatedPomodori.toString());
        if (createdAt != null) {
            pomodoroJsonObject.addProperty(CREATED_AT, getFriendlyCreatedAt().toString());
        }
        if (modifiedAt != null) {
            pomodoroJsonObject.addProperty(MODIFIED_AT, getFriendlyModifiedAt().toString());
        }
        if (completedAt != null) {
            pomodoroJsonObject.addProperty(COMPLETED_AT, getFriendlyCompletedAt().toString());
        }

        pomodoroJsonObject.add(USER, getUser().getSimpleJsonRepresentation(uriInfo));

        JsonArray usedPomodoriJsonArray = new JsonArray();
        for (Pomodoro pomodoro : getUsedPomodori()) {
            usedPomodoriJsonArray.add(pomodoro.getSimpleJsonRepresentation(uriInfo));
        }

        pomodoroJsonObject.add(USED_POMODORI, usedPomodoriJsonArray);

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
                .path(POMOTASKS)
                .path(id.toString())
                .build();
    }

}

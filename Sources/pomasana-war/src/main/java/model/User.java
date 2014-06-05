package model;

import com.google.gson.JsonObject;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


import java.net.URI;

import javax.ws.rs.core.UriInfo;

@Entity
public class User  {

    public static final String USERS = "users";

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHOTO_URL = "photoUrl";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String POMASANA_TOKEN = "pomasanaToken";
    public static final String EXPIRES_AT = "expiresAt";


    //It is the same of Asana
    @Id
    private Long id;

    private String name;

    private String email;

    private String photoUrl;

    private String accessToken;

    private String refreshToken;

    private Long expiresAt;

    @Index
    private String pomasanaToken;

    public User() {

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getBearerAccessToken(){
        return "Bearer " + accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getPomasanaToken() {
        return pomasanaToken;
    }

    public void setPomasanaToken(String pomasanaToken) {
        this.pomasanaToken = pomasanaToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        User user = (User) o;

        if (!id.equals(user.id)) {
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
        pomodoroJsonObject.addProperty(NAME, name);
        pomodoroJsonObject.addProperty(EMAIL, email);
        pomodoroJsonObject.addProperty(PHOTO_URL, photoUrl);

        return pomodoroJsonObject;
    }

    public JsonObject getSimpleJsonRepresentation(UriInfo uriInfo){
        JsonObject pomodoroJsonObject = new JsonObject();

        pomodoroJsonObject.addProperty("id", id);
        pomodoroJsonObject.addProperty(NAME, name);
        pomodoroJsonObject.addProperty("uri", getUri(uriInfo).toASCIIString());

        return pomodoroJsonObject;
    }



        public URI getUri(UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder()
                .path(USERS)
                .path(id.toString())
                .build();
    }

}

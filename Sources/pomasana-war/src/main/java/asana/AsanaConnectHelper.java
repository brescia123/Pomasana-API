package asana;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import dao.UserDao;
import helper.Constants;
import helper.TokenGenerator;
import model.User;

public class AsanaConnectHelper {


    private static final UserDao userDao = new UserDao();


    public static User getToken(String code) {

        try {
            URL url = new URL(AsanaConnectHelper.buildExchangeTokenRequest(code));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            return createOrUpdateUser(result.toString());

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;

        }

    }

    public static User renewToken(User user) {

        try {
            URL url = new URL(AsanaConnectHelper.buildExchangeRefreshTokenRequest(
                    user.getRefreshToken()));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            return createOrUpdateUser(result.toString());

        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

    }

    public static String buildExchangeTokenRequest(String code) {

        String finalUrl = "";

        finalUrl = finalUrl.concat(Constants.TOKEN_EXCHANGE_URL);
        finalUrl = finalUrl.concat("?");
        finalUrl = finalUrl.concat("grant_type").concat("=").concat("authorization_code");
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("client_id").concat("=").concat(Constants.CLIENT_ID);
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("client_secret").concat("=").concat(Constants.CLIENT_SECRET);
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("redirect_uri").concat("=").concat(Constants.REDIRECT_URL);
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("code").concat("=").concat(code);

        return finalUrl;
    }

    public static String buildExchangeRefreshTokenRequest(String refreshToken) {

        String finalUrl = "";

        finalUrl = finalUrl.concat(Constants.TOKEN_EXCHANGE_URL);
        finalUrl = finalUrl.concat("?");
        finalUrl = finalUrl.concat("grant_type").concat("=").concat("refresh_token");
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("client_id").concat("=").concat(Constants.CLIENT_ID);
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("client_secret").concat("=").concat(Constants.CLIENT_SECRET);
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("redirect_uri").concat("=").concat(Constants.REDIRECT_URL);
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("refresh_token").concat("=").concat(refreshToken);

        return finalUrl;
    }

    public static URI buildOAuthAuthorizeRequest(String clientRedirectUrl) throws
            URISyntaxException {

        String finalUrl = "";

        finalUrl = finalUrl.concat(Constants.OAUTH_AUTHORIZATION_URL);
        finalUrl = finalUrl.concat("?");
        finalUrl = finalUrl.concat("client_id").concat("=").concat(Constants.CLIENT_ID);
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("redirect_uri").concat("=").concat(Constants.REDIRECT_URL);
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("response_type").concat("=").concat("code");
        finalUrl = finalUrl.concat("&");
        finalUrl = finalUrl.concat("state").concat("=").concat(clientRedirectUrl);

        return new URI(finalUrl);

    }


    public static User createOrUpdateUser(String jsonTokenString)
            throws IOException {

        UserDao userDao = new UserDao();

        JsonParser jsonParser = new JsonParser();

        try {
            JsonElement jsonElement = jsonParser.parse(jsonTokenString.toString());


        /*
        Parse the asana response
         */
            if (jsonElement instanceof JsonObject) {

                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if (jsonObject.has("error")) {
                    return null;
                }

                String userIdString = jsonObject.get("data").getAsJsonObject().get("id")
                        .getAsString();

                Long userId = Long.valueOf(userIdString);

                User user = userDao.find(userId);


            /*
            User exists -> update fields
             */
                if (user != null) {

                    user.setEmail(
                            jsonObject.get("data").getAsJsonObject().get("email").getAsString());
                    user.setName(
                            jsonObject.get("data").getAsJsonObject().get("name").getAsString());

                    user.setAccessToken(jsonObject.get("access_token").getAsString());
                    JsonElement refreshToken = jsonObject.get("refresh_token");
                    if (refreshToken != null) {
                        user.setRefreshToken(refreshToken.getAsString());
                    }
                    user.setExpiresAt(
                            System.currentTimeMillis() + (jsonObject.get("expires_in").getAsLong()
                                    * 1000)
                    );

                    userDao.persist(user);

                }


            /*
            User doesn't exist -> create a new one
             */

                else {

                    user = new User();

                    user.setId(userId);
                    user.setEmail(
                            jsonObject.get("data").getAsJsonObject().get("email").getAsString());
                    user.setName(
                            jsonObject.get("data").getAsJsonObject().get("name").getAsString());

                    user.setAccessToken(jsonObject.get("access_token").getAsString());
                    user.setRefreshToken(jsonObject.get("refresh_token").getAsString());
                    user.setExpiresAt(
                            System.currentTimeMillis() + (jsonObject.get("expires_in").getAsLong()
                                    * 1000)
                    );

                    user.setPomasanaToken(TokenGenerator.generate());

                    userDao.persist(user);

                }

                return user;

            } else {
                return null;
            }
        } catch (JsonSyntaxException jse) {
            return null;
        }
    }

}

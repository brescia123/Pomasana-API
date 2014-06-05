package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

import asana.AsanaConnectHelper;
import model.User;

public class AsanaLoginServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {


        /*
        Coming from Asana Redirect
         */
        String code = request.getParameter("code");
        String clientRedirectUrl = request.getParameter("state");

        if (code != null && clientRedirectUrl != null) {

            User user = AsanaConnectHelper.getToken(code);

            /*
            Redirect to the client with the token
             */
            if (user != null) {
                System.out.println( "clientRedirectUrl received" + clientRedirectUrl);
                response.sendRedirect(
                        clientRedirectUrl + "?access_token=" + user.getPomasanaToken());

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }else
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

    }




}

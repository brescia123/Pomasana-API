package servlet;

import com.google.gson.JsonElement;

import org.joda.time.DateTime;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import asana.AsanaApiClient;
import asana.UpdateTaskRequest;
import dao.PomoTaskDao;
import dao.PomodoroDao;
import dao.UserDao;
import model.User;

public class FillDbServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        UserDao userDao = new UserDao();
        PomoTaskDao pomoTaskDao = new PomoTaskDao();
        PomodoroDao pomodoroDao = new PomodoroDao();

        //CAMILLA
//
//        User camilla = new User();
//        camilla.setId(12107752679514L);
//        camilla.setName("Camilla Veronica Tafuro");
//        camilla.setEmail("camilla.tafuro@gmail.com");
//        camilla.setAccessToken("sdvsef8sd8v9sdv");
//        camilla.setPhotoUrl("photoUrl");
//        userDao.persist(camilla);
//
//        //PomoTask 12028104848570
//
//        //if pomoTask doesn't exist create it
//        if (!pomoTaskDao.existsById(12028104848570L)) {
//
//            PomoTask pomoTask = new PomoTask();
//            pomoTask.setId(12028104848570L);
//            pomoTask.setUser(camilla);
//            ArrayList<Integer> estimatedPomodori = new ArrayList<Integer>();
//            estimatedPomodori.add(3);
//            estimatedPomodori.add(4);
//            pomoTask.setEstimatedPomodori(estimatedPomodori);
//            pomoTaskDao.persist(pomoTask);
//
//
//
//            Pomodoro pomodoro2 = new Pomodoro();
//            pomodoro2.setNotes("Prova");
//            pomodoro2.setExtInterrupt(1);
//            pomodoro2.setIntInterrupt(1);
//            pomodoro2.setPomoTask(pomoTask);
//            ArrayList<Pomodoro> pomodori = new ArrayList<Pomodoro>();
//            pomodori.add(pomodoro2);
//            pomodoroDao.persistAll(pomodori);
//
//
//        }
//
//
//        //PomoTask 12249671470508
//
//        //if pomoTask doesn't exist create it
//        if (!pomoTaskDao.existsById(12249671470508L)) {
//
//            PomoTask pomoTask = new PomoTask();
//            pomoTask.setId(12249671470508L);
//            pomoTask.setUser(camilla);
//            ArrayList<Integer> estimatedPomodori = new ArrayList<Integer>();
//            estimatedPomodori.add(1);
//            estimatedPomodori.add(3);
//            pomoTask.setEstimatedPomodori(estimatedPomodori);
//            pomoTaskDao.persist(pomoTask);
//
//                    /*
//        Add 2 pomodori
//         */
//
//            Pomodoro pomodoro2 = new Pomodoro();
//            pomodoro2.setNotes("Prova");
//            pomodoro2.setExtInterrupt(1);
//            pomodoro2.setIntInterrupt(1);
//            pomodoro2.setPomoTask(pomoTask);
//
//            Pomodoro pomodoro1 = new Pomodoro();
//            pomodoro1.setNotes("telefonata");
//            pomodoro1.setExtInterrupt(1);
//            pomodoro1.setIntInterrupt(0);
//            pomodoro1.setPomoTask(pomoTask);
//
//            ArrayList<Pomodoro> pomodori = new ArrayList<Pomodoro>();
//            pomodori.add(pomodoro2);
//            pomodori.add(pomodoro1);
//
//            pomodoroDao.persistAll(pomodori);
//
//
//
//        }
//
//
//        User user = userDao.find(12107752679514L);
//        user.setPomasanaToken("2");
//        userDao.persist(user);
//
//
//

//        User user = userDao.find(11253820692740L);
//
//        AsanaApiClient asanaApiClient = new AsanaApiClient(user);
//
//        JsonElement jsonElement = asanaApiClient.getTask(12455047206526L);

        System.out.print(System.currentTimeMillis());
    }
}

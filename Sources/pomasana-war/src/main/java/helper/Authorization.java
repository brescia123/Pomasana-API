package helper;

import javax.ws.rs.core.HttpHeaders;

import dao.PomoTaskDao;
import dao.PomodoroDao;
import dao.UserDao;
import exceptions.NotAuthorizedException;
import model.PomoTask;
import model.Pomodoro;
import model.User;

public class Authorization {

    private static UserDao userDao = new UserDao();

    private static PomoTaskDao pomoTaskDao = new PomoTaskDao();

    private static PomodoroDao pomodoroDao = new PomodoroDao();

    public static User authorize(HttpHeaders httpHeaders) {

        if (httpHeaders != null
                && httpHeaders.getRequestHeader(HttpHeaders.AUTHORIZATION) != null
                && httpHeaders.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0) != null) {

            String token = httpHeaders.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);

            return userDao.getByProperty(User.POMASANA_TOKEN, token);
        } else {
            throw new NotAuthorizedException();
        }

    }

    public static boolean ownPomoTasks(User user, PomoTask pomoTask){

        if(user == null || pomoTask ==  null){
            throw new NotAuthorizedException();
        }

        if(pomoTaskDao.findByUser(user).contains(pomoTask)){
            return true;
        }else{
            throw new NotAuthorizedException();
        }

    }
    
    public static boolean ownPomodoro(User user, Pomodoro pomodoro){

        if(user == null || pomodoro ==  null){
            throw new NotAuthorizedException();
        }

        if(pomodoroDao.findByUser(user).contains(pomodoro)){
            return true;
        }else {
            throw new NotAuthorizedException();
        }

    }

}

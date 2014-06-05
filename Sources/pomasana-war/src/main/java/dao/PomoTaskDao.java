package dao;

import com.google.gson.JsonElement;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import asana.AsanaApiClient;
import asana.UpdateTaskRequest;
import model.PomoTask;
import model.Pomodoro;
import model.User;

public class PomoTaskDao extends AbstractDao<PomoTask> {

    private static final PomodoroDao pomodoroDao = new PomodoroDao();

    public PomoTaskDao() {
        super(PomoTask.class);
    }


    @Override
    public PomoTask persist(PomoTask pomoTask) {

        /*
        Check the mandatory field:
            - id ( == asanaId)
            - user
        Otherwise return null
         */

        if (pomoTask.getId() == null || pomoTask.getUser() == null) {
            return null;
        }

        /*
        Creating new PomoTask
         */

        if (!existsById(pomoTask.getId())) {

            pomoTask.setCreatedAt(System.currentTimeMillis());

            pomoTask.setModifiedAt(System.currentTimeMillis());

        }

        /*
        Updating existing PomoTask
         */

        else {

            pomoTask.setModifiedAt(System.currentTimeMillis());

        }

        return super.persist(pomoTask);
    }

    @Override
    public List<PomoTask> persistAll(Iterable<PomoTask> entities) {

        ArrayList<PomoTask> pomoTasks = new ArrayList<PomoTask>();

        for (PomoTask pomoTask : entities) {

            pomoTasks.add(persist(pomoTask));
        }

        return pomoTasks;
    }

    @Override
    public void delete(PomoTask pomoTask) {

        ArrayList<Pomodoro> pomodori =  new ArrayList<Pomodoro>(pomoTask.getUsedPomodori());
        pomodoroDao.deleteAll(pomodori);

        super.delete(pomoTask);
    }

    @Override
    public void deleteAll(Iterable<PomoTask> entities) {
        for(PomoTask pomoTask : entities){
            delete(pomoTask);
        }
    }

    public List<PomoTask> findByUser(User user){

        return listByProperty(PomoTask.USER, user);

    }





}

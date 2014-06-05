package dao;

import java.util.ArrayList;
import java.util.List;

import model.PomoTask;
import model.Pomodoro;
import model.User;

public class PomodoroDao extends AbstractDao<Pomodoro> {


    private static final PomoTaskDao pomoTaskDao = new PomoTaskDao();

    public PomodoroDao() {
        super(Pomodoro.class);
    }


    @Override
    public Pomodoro persist(Pomodoro pomodoro) {


        /*
        Check the existence of the given PomoTask
         */
        if (!pomoTaskDao.existsById(pomodoro.getPomoTask().getId())) {
            return null;
        }

        /*
        Creating th Pomodoro (the pomodoro cannot have an Id)
         */

        if (pomodoro.getId() == null) {

            pomodoro.setCompletedAt(System.currentTimeMillis());
            /*
            Persist the Pomodoro (in this way the datastore creates a Key for it)
             */
            Pomodoro savedPomodoro = super.persist(pomodoro);

             /*
             Add the pomodoro to its PomoTask and persist the PomoTask if it is not yet added
             */
            PomoTask pomoTask = savedPomodoro.getPomoTask();
            List<Pomodoro> pomodoroList = pomoTask.getUsedPomodori();
            if (!pomodoroList.contains(savedPomodoro)) {
                pomodoroList.add(savedPomodoro);
                pomoTask.setUsedPomodori(pomodoroList);
                pomoTaskDao.persist(pomoTask);
            }

            return savedPomodoro;

        }

        /*
        Updating the Pomodoro (The Pomdoro has an id)
         */

        else {

            return super.persist(pomodoro);

        }

    }

    @Override
    public List<Pomodoro> persistAll(Iterable<Pomodoro> entities) {

        for (Pomodoro pomodoro : entities) {
            /*
            Check the existence of the given PomoTask
             */
            if (!pomoTaskDao.existsById(pomodoro.getPomoTask().getId())) {
                return null;
            }
        }

        for (Pomodoro pomodoro : entities) {
            pomodoro.setCompletedAt(System.currentTimeMillis());
        }


        /*
        Persist all the new Pomodori (in this way the datastore creates Keys for them)
        */
        List<Pomodoro> savedPomodori = super.persistAll(entities);

        /*
        Add every new pomodoro to its PomoTask and persist the PomoTask if it is not yet added
         */
        for (Pomodoro savedPomodoro : savedPomodori) {

            PomoTask pomoTask = savedPomodoro.getPomoTask();
            List<Pomodoro> pomodoroList = pomoTask.getUsedPomodori();

            if (!pomodoroList.contains(savedPomodoro)) {
                pomodoroList.add(savedPomodoro);
                pomoTask.setUsedPomodori(pomodoroList);
                pomoTaskDao.persist(pomoTask);
            }
        }

        return savedPomodori;
    }

    @Override
    public void delete(Pomodoro pomodoro) {

        /*
        Delete the Pomodoro from the associated PomoTask
         */
        if (pomodoro.getPomoTask() != null && pomoTaskDao
                .existsById(pomodoro.getPomoTask().getId())) {

            PomoTask associatedPomoTask = pomodoro.getPomoTask();

            List<Pomodoro> pomodoroList = associatedPomoTask.getUsedPomodori();

            if (pomodoroList.contains(pomodoro)) {
                pomodoroList.remove(pomodoro);
                associatedPomoTask.setUsedPomodori(pomodoroList);
                pomoTaskDao.persist(associatedPomoTask);
            }
        }

        super.delete(pomodoro);
    }

    @Override
    public void deleteAll(Iterable<Pomodoro> entities) {


        /*
        Delete every Pomodoro from the associated PomoTask
        */
        for (Pomodoro pomodoro : entities) {

            if (pomodoro.getPomoTask() != null && pomoTaskDao
                    .existsById(pomodoro.getPomoTask().getId())) {

                PomoTask associatedPomoTask = pomodoro.getPomoTask();

                List<Pomodoro> pomodoroList = associatedPomoTask.getUsedPomodori();

                if (pomodoroList.contains(pomodoro)) {
                    pomodoroList.remove(pomodoro);
                    associatedPomoTask.setUsedPomodori(pomodoroList);
                    pomoTaskDao.persist(associatedPomoTask);
                }
            }

        }

        super.deleteAll(entities);
    }

    public List<Pomodoro> findByUser(User user) {

        List<Pomodoro> pomodoroList = findAll();
        List<Pomodoro> userPomodoroList = new ArrayList<Pomodoro>();


        for (Pomodoro pomodoro : pomodoroList){
            if(pomodoro.getPomoTask().getUser().getId().equals(user.getId())){
                userPomodoroList.add(pomodoro);
            }
        }

        return userPomodoroList;

    }
}

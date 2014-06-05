package dao;

import model.PomoTask;
import model.User;

public class UserDao extends AbstractDao<User> {

    public UserDao() {
        super(User.class);
    }

    @Override
    public void delete(User user) {

        PomoTaskDao pomoTaskDao = new PomoTaskDao();

        if (find(user.getId()) != null) {

            pomoTaskDao.deleteAll(pomoTaskDao.listByProperty(PomoTask.USER, user));
        }

        super.delete(user);
    }
}

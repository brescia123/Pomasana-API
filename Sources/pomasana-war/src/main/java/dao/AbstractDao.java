package dao;


import com.googlecode.objectify.Key;

import java.util.ArrayList;
import java.util.List;

import static helper.OfyService.ofy;


public abstract class AbstractDao<T> {

    private final Class<T> clazz;

    public AbstractDao(Class<T> clazz) {
        this.clazz = clazz;
    }


    public T find(Long id) {
        return ofy().load().type(clazz).id(id).now();
    }


    public T find(Key<T> key) {
        return ofy().load().key(key).now();
    }


    public List<T> findAll() {
        return ofy().load().type(clazz).list();
    }

    public List<T> findAll(List<Key<T>> keys) {
        return new ArrayList<T>(ofy().load().keys(keys).values());
    }


    public T persist(T t) {
        ofy().save().entity(t).now();
        return t;
    }

    public List<T> persistAll(Iterable<T> entities) {
        return new ArrayList<T>(ofy().save().entities(entities).now().values());
    }

    public void delete(T t) {
        ofy().delete().entity(t).now();
    }

//    public void deleteById(Long id){
//        ofy().delete().key(Key.create(clazz,id)).now();
//    }

    public void deleteAll(Iterable<T> entities) {
        ofy().delete().entities(entities);
    }

    public T getByProperty(String propName, Object propValue) {
        return ofy().load().type(clazz).filter(propName, propValue).first().now();
    }

    public List<T> listByProperty(String propName, Object propValue) {
        return ofy().load().type(clazz).filter(propName, propValue).list();
    }

    public boolean existsById(Long id){
        if(ofy().load().type(clazz).id(id).now() != null){
            return true;
        }

        return false;
    }
}

package com.nulab.data.util;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by mayan on 11/18/2016.
 */

public class GenericDao<T extends Serializable, PK extends Serializable>
        implements Serializable {

    @Resource(name = "sessionFactory")
    private SessionFactory sessionFactory;

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(GenericDao.class));

    public <T> T save(final T o){
        return (T) sessionFactory.getCurrentSession().save(o);
    }

    public void delete(final Object object){
        sessionFactory.getCurrentSession().delete(object);
    }

    public <T> T get(final Class<T> type, final Long id){
        return (T) sessionFactory.getCurrentSession().get(type, id);
    }

    public <T> T merge(final T o)   {
        return (T) sessionFactory.getCurrentSession().merge(o);
    }

    public <T> void saveOrUpdate(final T o){
        sessionFactory.getCurrentSession().saveOrUpdate(o);
    }

    public <T> List<T> getAll(final Class<T> type) {
        final Session session = sessionFactory.getCurrentSession();
        final Criteria crit = session.createCriteria(type);
        return crit.list();
    }

    public <T> T getBy(final Class<T> type, final Long id){
        return (T) sessionFactory.getCurrentSession().get(type, id);
    }

    public List<T> findByParameter(String hql, Map<String, ?> paramMap) throws RuntimeException {
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        for (Map.Entry<String, ?> param : paramMap.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
        return query.list();
    }


}

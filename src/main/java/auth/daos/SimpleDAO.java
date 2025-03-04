package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class SimpleDAO<E> implements DAO<E>{

    public abstract Class<E> getEntityClass();

    private Session getSession() {
        return ConfigureSessionHibernate.getSession();
    }

    @Override
    public void create(E entity) {
        Session session = getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public E get(Integer id) {
        Session session = getSession();
        E entity = session.get(getEntityClass(), id);
        session.close();
        return entity;
    }

    @Override
    public void update(E entity) {
        Session session = getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public E delete(Integer id) {
        Session session = getSession();
        Transaction transaction = null;
        E entity = null;

        try {
            transaction = session.beginTransaction();
            entity = session.get(getEntityClass(), id);
            if (entity != null) {
                session.delete(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return entity;
    }
}

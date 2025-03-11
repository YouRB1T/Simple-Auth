package auth.dao;

import auth.configurations.ConfigureSessionHibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleDAO<E> implements DAO<E> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDAO.class);

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
            logger.debug("Attempting to save entity of type: {}", entity.getClass().getSimpleName());
            session.save(entity);
            transaction.commit();
            logger.info("Entity of type {} saved successfully.", entity.getClass().getSimpleName());
        } catch (Exception e) {
            if (transaction != null) {
                logger.error("Transaction rolled back due to error while saving entity of type {}: {}",
                        entity.getClass().getSimpleName(), e.getMessage(), e);
                transaction.rollback();
            } else {
                logger.error("Error during creation of entity of type {}: {}",
                        entity.getClass().getSimpleName(), e.getMessage(), e);
            }
        } finally {
            session.close();
            logger.debug("Session closed after create operation for entity of type: {}",
                    entity.getClass().getSimpleName());
        }
    }

    @Override
    public E get(Integer id) {
        Session session = getSession();
        try {
            logger.debug("Attempting to fetch entity of type {} with ID: {}",
                    getEntityClass().getSimpleName(), id);
            E entity = session.get(getEntityClass(), id);
            if (entity != null) {
                logger.info("Entity of type {} fetched successfully: ID = {}",
                        entity.getClass().getSimpleName(), id);
            } else {
                logger.warn("No entity of type {} found with ID: {}",
                        getEntityClass().getSimpleName(), id);
            }
            return entity;
        } catch (Exception e) {
            logger.error("Error fetching entity of type {} with ID {}: {}",
                    getEntityClass().getSimpleName(), id, e.getMessage(), e);
            throw e;
        } finally {
            session.close();
            logger.debug("Session closed after get operation for entity of type: {}",
                    getEntityClass().getSimpleName());
        }
    }

    @Override
    public void update(E entity) {
        Session session = getSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            logger.debug("Attempting to update entity of type: {}", entity.getClass().getSimpleName());
            session.update(entity);
            transaction.commit();
            logger.info("Entity of type {} updated successfully.", entity.getClass().getSimpleName());
        } catch (Exception e) {
            if (transaction != null) {
                logger.error("Transaction rolled back due to error while updating entity of type {}: {}",
                        entity.getClass().getSimpleName(), e.getMessage(), e);
                transaction.rollback();
            } else {
                logger.error("Error during update of entity of type {}: {}",
                        entity.getClass().getSimpleName(), e.getMessage(), e);
            }
        } finally {
            session.close();
            logger.debug("Session closed after update operation for entity of type: {}",
                    entity.getClass().getSimpleName());
        }
    }

    @Override
    public E delete(Integer id) {
        Session session = getSession();
        Transaction transaction = null;
        E entity = null;

        try {
            transaction = session.beginTransaction();
            logger.debug("Attempting to delete entity of type {} with ID: {}",
                    getEntityClass().getSimpleName(), id);
            entity = session.get(getEntityClass(), id);
            if (entity != null) {
                session.delete(entity);
                transaction.commit();
                logger.info("Entity of type {} deleted successfully: ID = {}",
                        entity.getClass().getSimpleName(), id);
            } else {
                logger.warn("No entity of type {} found with ID: {}",
                        getEntityClass().getSimpleName(), id);
            }
        } catch (Exception e) {
            if (transaction != null) {
                logger.error("Transaction rolled back due to error while deleting entity of type {}: {}",
                        getEntityClass().getSimpleName(), e.getMessage(), e);
                transaction.rollback();
            } else {
                logger.error("Error during deletion of entity of type {}: {}",
                        getEntityClass().getSimpleName(), e.getMessage(), e);
            }
        } finally {
            session.close();
            logger.debug("Session closed after delete operation for entity of type: {}",
                    getEntityClass().getSimpleName());
        }
        return entity;
    }
}
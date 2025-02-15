package ru.vsu.cs.daos;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.vsu.cs.configurations.ConfigureSessionHibernate;
import ru.vsu.cs.entities.Role;
import ru.vsu.cs.entities.User;

import java.util.Optional;

public class UserDAO extends SimpleDAO<User>{
    public void createUser(User user) {
        create(user);
    }

    public User getUserById(Integer id) {
        return get(id);
    }

    public void updateUser(User user) {
        update(user);
    }

    public User deleteUser(Integer id) {
        return delete(id);
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    /**
     * Добавление роли пользователю
     *
     * @param userId  ID пользователя
     * @param roleId  ID роли
     */
    public void addRoleToUser(Integer userId, Integer roleId) {
        Session session = ConfigureSessionHibernate.getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User user = session.get(User.class, userId);
            Role role = session.get(Role.class, roleId);
            if (user != null && role != null) {
                user.getRoles().add(role);
                session.update(user);
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
    }

    /**
     * Удаление роли у пользователя
     *
     * @param userId  ID пользователя
     * @param roleId  ID роли
     */
    public void removeRoleFromUser(Integer userId, Integer roleId) {
        Session session = ConfigureSessionHibernate.getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User user = session.get(User.class, userId);
            Role role = session.get(Role.class, roleId);
            if (user != null && role != null) {
                user.getRoles().remove(role);
                session.update(user);
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
    }

    /**
     * Поиск пользователя по логину или email
     *
     * @param usernameOrEmail Логин или email пользователя
     * @return Опциональный объект пользователя
     */
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            Query<User> query = session.createQuery(
                    "FROM User WHERE username = :usernameOrEmail OR email = :usernameOrEmail", User.class);
            query.setParameter("usernameOrEmail", usernameOrEmail);
            return query.uniqueResultOptional();
        } finally {
            session.close();
        }
    }
}

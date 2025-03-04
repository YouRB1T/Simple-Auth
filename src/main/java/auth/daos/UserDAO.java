package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import auth.entities.Role;
import auth.entities.User;
import auth.entities.UserAuth;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
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

    public Optional<User> findByUsernameOrEmail(String email, String number) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            Query<User> queryEmail = session.createQuery(
                    "FROM User WHERE email = :email", User.class);
            queryEmail.setParameter("email", email);
            Optional<User> userByEmail = queryEmail.uniqueResultOptional();

            Query<User> queryNumber = session.createQuery(
                    "FROM User WHERE phoneNumber = :number", User.class);
            queryNumber.setParameter("number", number);
            Optional<User> userByNumber = queryNumber.uniqueResultOptional();

            if (userByEmail.isPresent() && userByNumber.isPresent()) {
                if (!userByEmail.get().getIdUser().equals(userByNumber.get().getIdUser())) {
                    throw new IllegalStateException("Email и номер принадлежат разным пользователям");
                }
                return userByEmail;
            } else if (userByEmail.isPresent()) {
                return userByEmail;
            } else if (userByNumber.isPresent()) {
                return userByNumber;
            } else {
                return Optional.empty();
            }
        } finally {
            session.close();
        }
    }

    public List<UserAuth> getAllUserAuthMethods(Long userId) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            Query<UserAuth> query = session.createQuery(
                    "FROM UserAuth WHERE user.idUser = :userId", UserAuth.class);
            query.setParameter("userId", userId);
            return query.list();
        } finally {
            session.close();
        }
    }
}

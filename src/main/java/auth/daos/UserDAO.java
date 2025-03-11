package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import auth.entities.Role;
import auth.entities.User;
import auth.entities.UserOAuth;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDAO extends SimpleDAO<User>{
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

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
            logger.debug("Attempting to add role with ID {} to user with ID {}", roleId, userId);

            User user = session.get(User.class, userId);
            Role role = session.get(Role.class, roleId);

            if (user != null && role != null) {
                user.getRoles().add(role);
                session.update(user);
                transaction.commit();
                logger.info("Role {} added successfully to user {}", role.getName(), user.getUsername());
            } else {
                logger.warn("User or Role not found. User ID: {}, Role ID: {}", userId, roleId);
            }
        } catch (Exception e) {
            if (transaction != null) {
                logger.error("Transaction rolled back due to error while adding role to user: {}", e.getMessage(), e);
                transaction.rollback();
            } else {
                logger.error("Error adding role to user: {}", e.getMessage(), e);
            }
        } finally {
            session.close();
            logger.debug("Session closed after addRoleToUser operation for user ID: {}", userId);
        }
    }

    public void removeRoleFromUser(Integer userId, Integer roleId) {
        Session session = ConfigureSessionHibernate.getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            logger.debug("Attempting to remove role with ID {} from user with ID {}", roleId, userId);

            User user = session.get(User.class, userId);
            Role role = session.get(Role.class, roleId);

            if (user != null && role != null) {
                user.getRoles().remove(role);
                session.update(user);
                transaction.commit();
                logger.info("Role {} removed successfully from user {}", role.getName(), user.getUsername());
            } else {
                logger.warn("User or Role not found. User ID: {}, Role ID: {}", userId, roleId);
            }
        } catch (Exception e) {
            if (transaction != null) {
                logger.error("Transaction rolled back due to error while removing role from user: {}", e.getMessage(), e);
                transaction.rollback();
            } else {
                logger.error("Error removing role from user: {}", e.getMessage(), e);
            }
        } finally {
            session.close();
            logger.debug("Session closed after removeRoleFromUser operation for user ID: {}", userId);
        }
    }

    public Optional<User> findByUsernameOrEmail(String email, String number) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            logger.debug("Attempting to find user by email: {} or number: {}", email, number);

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
                    logger.error("Email and number belong to different users. Email: {}, Number: {}", email, number);
                    throw new IllegalStateException("Email и номер принадлежат разным пользователям");
                }
                logger.info("User found by email: {}", userByEmail.get().getUsername());
                return userByEmail;
            } else if (userByEmail.isPresent()) {
                logger.info("User found by email: {}", userByEmail.get().getUsername());
                return userByEmail;
            } else if (userByNumber.isPresent()) {
                logger.info("User found by number: {}", userByNumber.get().getUsername());
                return userByNumber;
            } else {
                logger.warn("No user found with email: {} or number: {}", email, number);
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error finding user by email {} or number {}: {}", email, number, e.getMessage(), e);
            throw e;
        } finally {
            session.close();
            logger.debug("Session closed after findByUsernameOrEmail operation for email: {} and number: {}", email, number);
        }
    }
}

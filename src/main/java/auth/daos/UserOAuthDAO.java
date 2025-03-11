package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import auth.entities.AuthProvider;
import auth.entities.UserOAuth;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserOAuthDAO extends SimpleDAO<UserOAuth>{

    private static final Logger logger = LoggerFactory.getLogger(AuthProviderDAO.class);

    public void createUserAuth(UserOAuth userAuth) {
        create(userAuth);
    }

    public UserOAuth getUserAuthById(Integer id) {
        return get(id);
    }

    public void updateUserAuth(UserOAuth userAuth) {
        update(userAuth);
    }

    public UserOAuth deleteUserAuth(Integer id) {
        return delete(id);
    }
    @Override
    public Class<UserOAuth> getEntityClass() {
        return UserOAuth.class;
    }

    public Optional<AuthProvider> findByName(String name) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            logger.debug("Attempting to find AuthProvider by name: {}", name);

            Query<AuthProvider> query = session.createQuery(
                    "FROM AuthProvider WHERE name = :name", AuthProvider.class);
            query.setParameter("name", name);

            Optional<AuthProvider> result = query.uniqueResultOptional();

            if (result.isPresent()) {
                logger.info("AuthProvider found: {}", result.get());
            } else {
                logger.warn("No AuthProvider found with name: {}", name);
            }

            return result;
        } catch (Exception e) {
            logger.error("Error finding AuthProvider by name {}: {}", name, e.getMessage(), e);
            throw e;
        } finally {
            session.close();
            logger.debug("Session closed after findByName operation for name: {}", name);
        }
    }

    public List<AuthProvider> getAllProviders() {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            logger.debug("Attempting to fetch all AuthProviders");

            Query<AuthProvider> query = session.createQuery(
                    "FROM AuthProvider", AuthProvider.class);
            List<AuthProvider> providers = query.list();

            logger.info("Fetched {} AuthProviders", providers.size());

            return providers;
        } catch (Exception e) {
            logger.error("Error fetching all AuthProviders: {}", e.getMessage(), e);
            throw e;
        } finally {
            session.close();
            logger.debug("Session closed after getAllProviders operation");
        }
    }
}

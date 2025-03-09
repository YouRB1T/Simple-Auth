package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import auth.entities.AuthProvider;
import auth.entities.UserOAuth;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserOAuthDAO extends SimpleDAO<UserOAuth>{
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

    public Optional<UserOAuth> findUserAuthByUserIdAndProviderId(Long userId, Integer providerId) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            Query<UserOAuth> query = session.createQuery(
                    "FROM UserAuth WHERE user.idUser = :userId AND provider.id = :providerId", UserOAuth.class);
            query.setParameter("userId", userId);
            query.setParameter("providerId", providerId);
            return query.uniqueResultOptional();
        } finally {
            session.close();
        }
    }

    public List<UserOAuth> getAllUserAuthMethods(Long userId) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            Query<UserOAuth> query = session.createQuery(
                    "FROM UserAuth WHERE user.idUser = :userId", UserOAuth.class);
            query.setParameter("userId", userId);
            return query.list();
        } finally {
            session.close();
        }
    }
}

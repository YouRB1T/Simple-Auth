package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import auth.entities.AuthProvider;
import auth.entities.UserAuth;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class UserAuthDAO extends SimpleDAO<UserAuth>{
    public void createUserAuth(UserAuth userAuth) {
        create(userAuth);
    }

    public UserAuth getUserAuthById(Integer id) {
        return get(id);
    }

    public void updateUserAuth(UserAuth userAuth) {
        update(userAuth);
    }

    public UserAuth deleteUserAuth(Integer id) {
        return delete(id);
    }
    @Override
    public Class<UserAuth> getEntityClass() {
        return UserAuth.class;
    }

    public Optional<UserAuth> findUserAuthByUserIdAndProviderId(Long userId, Integer providerId) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            Query<UserAuth> query = session.createQuery(
                    "FROM UserAuth WHERE user.idUser = :userId AND provider.id = :providerId", UserAuth.class);
            query.setParameter("userId", userId);
            query.setParameter("providerId", providerId);
            return query.uniqueResultOptional();
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

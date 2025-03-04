package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import auth.entities.AuthProvider;
import auth.entities.Role;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class AuthProviderDAO extends SimpleDAO<AuthProvider>{
    public void createAuthProvider(AuthProvider authProvider) {
        create(authProvider);
    }

    public AuthProvider getAuthProviderById(Integer id) {
        return get(id);
    }

    public void updateAuthProvider(AuthProvider authProvider) {
        update(authProvider);
    }

    public AuthProvider deleteAuthProvider(Integer id) {
        return delete(id);
    }

    @Override
    public Class<AuthProvider> getEntityClass() {
        return AuthProvider.class;
    }

    public Optional<AuthProvider> findByName(String name) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            Query<AuthProvider> query = session.createQuery(
                    "FROM AuthProvider WHERE name = :name", AuthProvider.class);
            query.setParameter("name", name);
            return query.uniqueResultOptional();
        } finally {
            session.close();
        }
    }

    public List<AuthProvider> getAllProviders() {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            Query<AuthProvider> query = session.createQuery(
                    "FROM AuthProvider", AuthProvider.class);
            return query.list();
        } finally {
            session.close();
        }
    }
}

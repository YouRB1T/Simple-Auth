package auth.configurations;

import auth.entities.AuthProvider;
import auth.entities.Role;
import auth.entities.User;
import auth.entities.UserOAuth;
import jakarta.persistence.Entity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.PostgreSQLDialect;
import org.springframework.context.annotation.Configuration;

import static org.hibernate.cfg.JdbcSettings.*;
import static org.hibernate.cfg.SchemaToolingSettings.HBM2DDL_AUTO;

@Configuration
public class ConfigureSessionHibernate {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "org.postgresql.Driver")
                .applySetting("hibernate.connection.url", "jdbc:postgresql://localhost:5432/login_system")
                .applySetting(USER, "new_user")
                .applySetting(PASS, "123")
                .applySetting(DIALECT, PostgreSQLDialect.class)
                .applySetting(HBM2DDL_AUTO, "update")
                .applySetting(SHOW_SQL, "true")
                .build();

        return new MetadataSources(registry)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(AuthProvider.class)
                .addAnnotatedClass(Role.class)
                .addAnnotatedClass(UserOAuth.class)
                .buildMetadata()
                .getSessionFactoryBuilder()
                .build();
    }

    public static Session getSession() {
        return sessionFactory.openSession();
    }
}

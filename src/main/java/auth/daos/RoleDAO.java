package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import auth.entities.Role;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleDAO extends SimpleDAO<Role>{
    private static final Logger logger = LoggerFactory.getLogger(RoleDAO.class);

    public void createRole(Role role) {
       create(role);
    }

    public Role getRoleById(Integer id) {
        return get(id);
    }

    public void updateRole(Role role) {
        update(role);
    }

    public Role deleteRole(Integer id) {
        return delete(id);
    }

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }

    public List<Role> getAllRoles() {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            logger.debug("Attempting to fetch all roles");

            List<Role> roles = session.createQuery("FROM Role", Role.class).list();

            logger.info("Fetched {} roles", roles.size());

            return roles;
        } catch (Exception e) {
            logger.error("Error fetching all roles: {}", e.getMessage(), e);
            throw e;
        } finally {
            session.close();
            logger.debug("Session closed after getAllRoles operation");
        }
    }

    public Optional<Role> findByName(String name) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            logger.debug("Attempting to find role by name: {}", name);

            Role role = session.createQuery("FROM Role WHERE name = :name", Role.class)
                    .setParameter("name", name)
                    .uniqueResult();

            if (role != null) {
                logger.info("Role found: {}", role.getName());
            } else {
                logger.warn("No role found with name: {}", name);
            }

            return Optional.ofNullable(role);
        } catch (Exception e) {
            logger.error("Error finding role by name {}: {}", name, e.getMessage(), e);
            throw e;
        } finally {
            session.close();
            logger.debug("Session closed after findByName operation for name: {}", name);
        }
    }
}

package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import auth.entities.Role;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleDAO extends SimpleDAO<Role>{

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
            return session.createQuery("FROM Role", Role.class).list();
        } finally {
            session.close();
        }
    }

    public Optional<Role> findByName(String name) {
        Session session = ConfigureSessionHibernate.getSession();
        try {
            Role role = session.createQuery("FROM Role WHERE name = :name", Role.class)
                    .setParameter("name", name)
                    .uniqueResult();
            return Optional.ofNullable(role);
        } finally {
            session.close();
        }
    }
}

package auth.daos;

import auth.configurations.ConfigureSessionHibernate;
import auth.entities.Role;
import org.hibernate.Session;

import java.util.List;

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
}

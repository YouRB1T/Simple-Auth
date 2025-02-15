package ru.vsu.cs.daos;

import org.hibernate.Session;
import ru.vsu.cs.configurations.ConfigureSessionHibernate;
import ru.vsu.cs.entities.Role;

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

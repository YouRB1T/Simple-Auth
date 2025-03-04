package auth.daos;

public interface DAO<E> {
    void create(E entity);
    E get(Integer id);
    void update(E entity);
    E delete(Integer id);
}

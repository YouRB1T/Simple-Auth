package ru.vsu.cs.daos;

import jakarta.persistence.criteria.CriteriaBuilder;

public interface DAO<E> {
    void create(E entity);
    E get(Integer id);
    void update(E entity);
    E delete(Integer id);
}

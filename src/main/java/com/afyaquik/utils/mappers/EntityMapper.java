package com.afyaquik.utils.mappers;

public interface EntityMapper<E, D> {
    D toDto(E entity);
}

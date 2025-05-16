package com.afyaquik.core.mappers;

public interface EntityMapper<E, D> {
    D toDto(E entity);
}

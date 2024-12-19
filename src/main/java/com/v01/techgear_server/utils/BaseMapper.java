package com.v01.techgear_server.utils;

import java.util.*;

public interface BaseMapper<E, D> {
    D toDTO(E entity);

    E toEntity(D dto);

    // Bulk mapping methods
    List<D> toDTOList(List<E> entityList);

    List<E> toEntityList(List<D> dtoList);

}

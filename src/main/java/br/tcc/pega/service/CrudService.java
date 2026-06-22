package br.tcc.pega.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CrudService<T, D> {
    D create(D dto);
    D update(UUID id, D dto);
    void delete(UUID id);
    D findById(UUID id);
    Page<D> findAll(Pageable pageable);
}

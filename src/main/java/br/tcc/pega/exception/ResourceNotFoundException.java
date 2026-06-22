package br.tcc.pega.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entity, UUID id) {
        super(entity + " não encontrado(a) com id: " + id);
    }
}

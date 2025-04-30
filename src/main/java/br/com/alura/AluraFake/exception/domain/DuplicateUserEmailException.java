package br.com.alura.AluraFake.exception.domain;

import br.com.alura.AluraFake.exception.DomainException;

public class DuplicateUserEmailException extends DomainException {
    public DuplicateUserEmailException(String field) {
        super(field, "Email de usuário já cadastrado no sistema");
    }
}

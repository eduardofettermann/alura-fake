package br.com.alura.AluraFake.exception.domain;

import br.com.alura.AluraFake.exception.DomainException;

public class DuplicateTaskStatementInException extends DomainException {
    public DuplicateTaskStatementInException(String field, String message) {
        super(field, message);
    }
}

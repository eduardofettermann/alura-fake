package br.com.alura.AluraFake.exception.domain;

import br.com.alura.AluraFake.exception.DomainException;

public class OutOfSequenceTaskOrderException extends DomainException {
    public OutOfSequenceTaskOrderException(String field, String message) {
        super(field, message);
    }
}

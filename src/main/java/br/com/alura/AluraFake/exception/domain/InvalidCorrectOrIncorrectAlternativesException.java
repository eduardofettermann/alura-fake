package br.com.alura.AluraFake.exception.domain;

import br.com.alura.AluraFake.exception.DomainException;

public class InvalidCorrectOrIncorrectAlternativesException extends DomainException {
    public InvalidCorrectOrIncorrectAlternativesException(String field, String message) {
        super(field, message);
    }
}

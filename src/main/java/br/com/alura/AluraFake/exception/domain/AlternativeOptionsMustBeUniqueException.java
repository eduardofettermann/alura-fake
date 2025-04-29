package br.com.alura.AluraFake.exception.domain;

import br.com.alura.AluraFake.exception.DomainException;

public class AlternativeOptionsMustBeUniqueException extends DomainException {
    public AlternativeOptionsMustBeUniqueException(String field) {
        super(field, "As alternativas não podem ser iguais entre si");
    }
}

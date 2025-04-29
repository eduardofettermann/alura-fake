package br.com.alura.AluraFake.exception.domain;

import br.com.alura.AluraFake.exception.DomainException;

public class MissingRequiredTaskTypesException extends DomainException {
    public MissingRequiredTaskTypesException(String field) {
        super(field, "O curso deve conter ao menos uma atividade de cada tipo.");
    }
}

package br.com.alura.AluraFake.exception.domain;

import br.com.alura.AluraFake.exception.DomainException;

public class AlternativeOptionEqualsTaskStatementException extends DomainException {
    public AlternativeOptionEqualsTaskStatementException(String field) {
        super(field, "As alternativas não podem ser iguais ao enunciado da atividade.");
    }
}

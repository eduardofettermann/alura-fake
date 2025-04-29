package br.com.alura.AluraFake.exception.domain;

import br.com.alura.AluraFake.exception.DomainException;

public class CourseIsNotBuildingException extends DomainException {
    public CourseIsNotBuildingException(String field, String message) {
        super(field, message);
    }
}

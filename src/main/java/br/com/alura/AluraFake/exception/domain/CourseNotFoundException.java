package br.com.alura.AluraFake.exception.domain;

import br.com.alura.AluraFake.exception.DomainException;

public class CourseNotFoundException extends DomainException {
    public CourseNotFoundException(String field, String message) {
        super(field, message);
    }
}

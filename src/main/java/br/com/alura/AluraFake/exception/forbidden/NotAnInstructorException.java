package br.com.alura.AluraFake.exception.forbidden;

import br.com.alura.AluraFake.exception.ForbiddenException;

public class NotAnInstructorException extends ForbiddenException {
    public NotAnInstructorException(String field) {
        super(field, "Usuário não é um instrutor");
    }
}

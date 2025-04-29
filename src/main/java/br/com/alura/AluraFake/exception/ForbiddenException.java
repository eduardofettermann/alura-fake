package br.com.alura.AluraFake.exception;

public class ForbiddenException extends RuntimeException {
    private final String field;

    public ForbiddenException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}

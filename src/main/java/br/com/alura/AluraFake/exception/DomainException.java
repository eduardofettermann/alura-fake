package br.com.alura.AluraFake.exception;

public class DomainException extends RuntimeException {
    private final String field;

    public DomainException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
package br.com.alura.AluraFake.exception;

public class EmailAlreadyRegisteredException extends Exception {
    private static final String FIELD = "email";

    public EmailAlreadyRegisteredException() {
        super("Email já registrado");
    }

    public String getField() {
        return FIELD;
    }
}

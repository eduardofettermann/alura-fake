package br.com.alura.AluraFake.exception;

public class EmailOrPasswordInvalidException extends Exception {
    private static final String FIELD = "email or password";

    public EmailOrPasswordInvalidException() {
        super("Email ou senha inválidos");
    }

    public String getField() {
        return FIELD;
    }
}

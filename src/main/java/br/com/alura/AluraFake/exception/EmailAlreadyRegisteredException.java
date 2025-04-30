package br.com.alura.AluraFake.exception;

public class EmailAlreadyRegisteredException extends Throwable {
    private String field = "email";

    public EmailAlreadyRegisteredException() {
        super("Email já registrado");
    }
}

package br.com.alura.AluraFake.task;

public class NewOpenTextTaskDTO extends NewTaskDTO{

    @Override
    public Type getType() {
        return Type.OPEN_TEXT;
    }
}

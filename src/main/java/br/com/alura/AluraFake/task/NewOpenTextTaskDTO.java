package br.com.alura.AluraFake.task;

public class NewOpenTextTaskDTO extends NewTaskDTO{

    @Override
    public Type getType() {
        return Type.OPEN_TEXT;
    }

    @Override
    public void setType(Type type) { /* O tipo é sempre OPEN_TEXT */ }
}

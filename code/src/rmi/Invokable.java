package rmi;

public interface Invokable {
    public Object invoke(Invocation invocation) throws Exception;
}

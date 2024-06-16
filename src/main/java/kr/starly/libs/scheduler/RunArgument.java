package kr.starly.libs.scheduler;

public interface RunArgument<T> {
    void run(T argument);
}
package kr.starly.libs.scheduler;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncExecutor {

    private static ExecutorService service = null;

    private static ExecutorService getService() {
        if (service == null)
            service = Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    (
                            new BasicThreadFactory.Builder())
                            .namingPattern("STAsyncThread-%d")
                            .daemon(true)
                            .priority(5).uncaughtExceptionHandler((ignored, ex) -> ex.printStackTrace()).build());

        return service;
    }

    public static void run(Runnable runnable) {
        getService().execute(new ExceptionHandler(runnable));
    }

    public static <T> T submit(Callable<T> callable) {
        try {
            return getService().submit(callable).get();
        } catch (Exception e) {
            return null;
        }
    }

    public static void shutdown() {
        if (service != null && service.isTerminated()) {
            service.shutdown();
            service = null;
        }
    }

    @AllArgsConstructor
    private static class ExceptionHandler implements Runnable {

        private final Runnable handler;

        @Override
        public void run() {
            try {
                handler.run();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
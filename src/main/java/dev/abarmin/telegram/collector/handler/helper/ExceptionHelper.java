package dev.abarmin.telegram.collector.handler.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionHelper {

    public static <T> T wrap(SupplierWithException<T> supplier) {
        try {
            return supplier.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void wrap(RunnableWithException runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface SupplierWithException<T> {
        T run() throws Exception;
    }

    public interface RunnableWithException {
        void run() throws Exception;
    }

}

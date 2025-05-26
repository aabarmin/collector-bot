package dev.abarmin.telegram.collector;

import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicInteger;

@UtilityClass
public class SequenceIdGenerator {

    private final AtomicInteger sequenceId = new AtomicInteger(0);

    public static long nextChatId() {
        return sequenceId.incrementAndGet();
    }

    public static int nextUpdateId() {
        return sequenceId.incrementAndGet();
    }

    public static long nextUserId() {
        return sequenceId.incrementAndGet();
    }
}

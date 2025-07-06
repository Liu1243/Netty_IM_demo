package cn.liu.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Title: 1
 * @Author liu
 * @Package cn.liu.protocol
 * @Date 2025/7/6 15:56
 * @description:
 */
public class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.incrementAndGet();
    }
}

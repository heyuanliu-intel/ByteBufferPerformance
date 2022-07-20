package com.test.bytebuffer;

import java.nio.ByteBuffer;

public class PoolBasedAllocator implements ByteBufferAllocator {
    private ThreadLocal<ByteBufferPool> threadLocal = ThreadLocal.withInitial(() -> new ByteBufferPool());

    public void allocate(int capacity) {
        ByteBufferPool pool = threadLocal.get();
        final ByteBuffer buf = pool.take(capacity);
        try {
            // do nothing and then release
        } finally {
            pool.give(buf);
        }
    }
}

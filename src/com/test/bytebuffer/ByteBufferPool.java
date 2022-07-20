package com.test.bytebuffer;

import java.nio.ByteBuffer;
import java.util.*;

public class ByteBufferPool implements AutoCloseable {
    private final NavigableMap<Integer, Deque<ByteBuffer>> buffers = new TreeMap<>();

    protected ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }

    public ByteBuffer take(int capacity) {
        Optional<ByteBuffer> maybeBuffer = buffers.tailMap(capacity, true)
                .values()
                .stream()
                .map(Deque::poll)
                .filter(Objects::nonNull)
                .findAny();

        return maybeBuffer.map((ByteBuffer buffer) -> {
            buffer.clear().limit(capacity);
            return buffer;
        }).orElseGet(() -> allocate(capacity));
    }

    public void give(ByteBuffer buffer) {
        buffers.computeIfAbsent(buffer.capacity(), capacity -> new ArrayDeque<>(10)).offer(buffer);
    }

    @Override
    public void close() {
        buffers.clear();
    }
}
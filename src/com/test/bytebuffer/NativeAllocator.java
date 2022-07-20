package com.test.bytebuffer;

import java.nio.ByteBuffer;

public class NativeAllocator implements ByteBufferAllocator {
    public void allocate(int capacity) {
        final ByteBuffer buf = ByteBuffer.allocateDirect(capacity);
        try {
            // do nothing and then release
        } finally {
            buf.clear();
            DirectByteBufferDeallocator.free(buf);
        }
    }
}

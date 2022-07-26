package com.test.bytebuffer;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.PrivilegedAction;

public final class DirectByteBufferDeallocator {
    private static final boolean SUPPORTED;
    private static final Method cleaner;
    private static final Method cleanerClean;
    private static final Unsafe UNSAFE;

    static {
        String versionString = System.getProperty("java.specification.version");
        if (versionString.startsWith("1.")) {
            versionString = versionString.substring(2);
        }
        int version = Integer.parseInt(versionString);

        Method tmpCleaner = null;
        Method tmpCleanerClean = null;
        boolean supported;
        Unsafe tmpUnsafe = null;
        if (version < 9) {
            try {
                tmpCleaner = Class.forName("java.nio.DirectByteBuffer").getMethod("cleaner");
                tmpCleaner.setAccessible(true);
                tmpCleanerClean = Class.forName("sun.misc.Cleaner").getMethod("clean");
                tmpCleanerClean.setAccessible(true);
                supported = true;
            } catch (Throwable t) {
                supported = false;
            }
        } else {
            tmpUnsafe = getUnsafe();
            try {
                tmpCleanerClean = tmpUnsafe.getClass().getDeclaredMethod("invokeCleaner", ByteBuffer.class);
                tmpCleanerClean.setAccessible(true);
                supported = true;
            } catch (Throwable t) {
                supported = false;
            }
        }
        SUPPORTED = supported;
        cleaner = tmpCleaner;
        cleanerClean = tmpCleanerClean;
        UNSAFE = tmpUnsafe;

    }

    private DirectByteBufferDeallocator() {
        // Utility Class
    }

    /**
     * Attempts to deallocate the underlying direct memory.
     * This is a no-op for buffers where {@link ByteBuffer#isDirect()} returns false.
     *
     * @param buffer to deallocate
     */
    public static void free(ByteBuffer buffer) {
        if (SUPPORTED && buffer != null && buffer.isDirect()) {
            try {
                if (UNSAFE != null) {
                    //use the JDK9 method
                    cleanerClean.invoke(UNSAFE, buffer);
                } else {
                    Object cleaner = DirectByteBufferDeallocator.cleaner.invoke(buffer);
                    cleanerClean.invoke(cleaner);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static Unsafe getUnsafe() {
        if (System.getSecurityManager() != null) {
            return new PrivilegedAction<Unsafe>() {
                public Unsafe run() {
                    return getUnsafe0();
                }
            }.run();
        }
        return getUnsafe0();
    }

    private static Unsafe getUnsafe0() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Throwable t) {
            throw new RuntimeException("JDK did not allow accessing unsafe", t);
        }
    }
}
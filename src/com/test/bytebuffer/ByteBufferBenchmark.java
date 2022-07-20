package com.test.bytebuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ByteBufferBenchmark {
    private static final Random random = new Random();
    private static final int THREADS = 10;
    private static final int LOOP = 1000 * 1000 * 10;

    public static void main(String[] args) throws Exception {
        benchmark(0);
        benchmark(64 * 1024);
        System.out.println("##########################");
        System.out.println("running in muti-threads");
        benchmark_threads(0);
        benchmark_threads(64 * 1024);
    }

    public static void benchmark(int capacity) {
        long native_time = run(new NativeAllocator(), capacity);
        long pool_time = run(new PoolBasedAllocator(), capacity);
        System.out.println("native time:" + native_time);
        System.out.println("pool time:" + pool_time);
        System.out.println("performance improvement:" + native_time / pool_time);
    }

    public static void benchmark_threads(int capacity) throws Exception {
        long native_time = run_threads(new NativeAllocator(), capacity);
        long pool_time = run_threads(new PoolBasedAllocator(), capacity);
        System.out.println("native time:" + native_time);
        System.out.println("pool time:" + pool_time);
        System.out.println("performance improvement:" + native_time / pool_time);
    }

    public static long run(ByteBufferAllocator allocator, int capacity) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < LOOP; i++) {
            int size = capacity == 0 ? randomInt(8, 64 * 1024) : capacity;
            allocator.allocate(size);
        }
        long end = System.currentTimeMillis();
        return end - start;
    }

    public static long run_threads(ByteBufferAllocator allocator, int capacity) throws Exception {
        List<Thread> threads = new ArrayList<>(THREADS);
        List<Long> times = new ArrayList<>(THREADS);

        for (int i = 0; i < THREADS; i++) {
            Thread t = new Thread(() -> {
                long start = System.currentTimeMillis();
                for (int j = 0; j < LOOP; j++) {
                    int size = capacity == 0 ? randomInt(8, 64 * 1024) : capacity;
                    allocator.allocate(size);
                }
                long end = System.currentTimeMillis();
                times.add(end - start);
            });
            threads.add(t);
        }

        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).start();
        }

        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).join();
        }

        int total = 0;
        for (int i = 0; i < times.size(); i++) {
            total += times.get(i);
        }
        return total;
    }

    public static int randomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }
}

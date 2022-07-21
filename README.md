# ByteBufferPerformance

This project will evaluate the performance improvement for Java ByteBuffer allocation and de-allocation between native allocation and pool based allocation.

It also evaluates the performance improvement using single thread and multi-threads.

For single thread, pool-based vs native, it can get 4x improvement for random capacity and 13x improvement for fixed capacity.

For multi-threads, pool-based vs native, it can get 13x improvement for random capacity and 79x improvement for fixed capacity.

Below is the testing results.

In single thread:

|            Attempt            | #native | #pool based | improvement |
| :---------------------------: | :-----: | :---------: | :---------: |
| random capacity(milliseconds) |  7107   |    1714     |     4X      |
| fixed capacity(milliseconds)  |  14114  |    1082     |     13X     |

In multi-threads:

|            Attempt            | #native | #pool based | improvement |
| :---------------------------: | :-----: | :---------: | :---------: |
| random capacity(milliseconds) | 969873  |    73861    |     13X     |
| fixed capacity(milliseconds)  | 1007342 |    12719    |     79X     |

## Performance improvement about native vs pool based for the integration with Airlift.

Please refer to this project: https://github.com/heyuanliu-intel/AirliftPerformance

During the performance benchmarking of the integration wildfly-openssl with Airlift, we found the bottleneck is the ByteBuffer allocation and de-allocation.

The system CPU utilization is about 20%. Most of the threads are blocked. Please refer to the thread dump file: https://raw.githubusercontent.com/heyuanliu-intel/AirliftPerformance/main/threaddump/thread1.txt

From this thread dump file, we can see that most of the threads are blocked by the ByteBuffer allocation and de-allocation. So change the code from native allocation/de-allocation to pool based and blow is the benchmarking results.

For the native way, below is the benchmarking result.

<pre>
System CPU utilization is about 20%.

./run_wrk.sh 
Running 2m test @ https://localhost:9300/v1/service
  128 threads and 2000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     3.03ms   33.08ms   2.00s    99.79%
    Req/Sec     1.46k   817.09     5.63k    60.69%
  11666325 requests in 2.00m, 1.71GB read
  Socket errors: connect 0, read 0, write 0, timeout 3936
Requests/sec:  97139.29
Transfer/sec:     14.54MB
</pre>

For the pool based way, below is the benchmarking result.

<pre>
System CPU utilization is about 80%.

./run_wrk.sh 
Running 2m test @ https://localhost:9300/v1/service
  128 threads and 2000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    46.23ms   66.43ms 558.99ms   82.74%
    Req/Sec     3.30k     1.76k   19.07k    70.27%
  50158401 requests in 2.00m, 7.33GB read
Requests/sec: 417646.32
Transfer/sec:     62.53MB
</pre>

So based on the result, we can get the 4X performance improvement between native and pool based methods.

|   Attempt    | #native  | #pool based | improvement |
| :----------: | :------: | :---------: | :---------: |
| Requests/sec | 97139.29 |  417646.32  |     4X      |

# ByteBufferPerformance

This project will evaluate the performance improvement for Java ByteBuffer allocation and de-allocation between native allocation and pool based allocation.

It also evaluates the performance improvement using single thread and multi-threads.

For single thread, pool-based vs native, it can get 4x improvement for random capacity and 13x improvement for fixed capacity.

For multi-threads, pool-based vs native, it can get 13x improvement for random capacity and 79x improvement for fixed capacity.

Below is the testing results.

In single thread:

|            Attempt            | #native | #pool based | improvement |
|:-----------------------------:|:-------:|:-----------:|:-----------:|
| random capacity(milliseconds) |  7107   |    1714     |     4X      |
| fixed capacity(milliseconds)  |  14114  |    1082     |     13X     |

In multi-threads:

|            Attempt            | #native | #pool based | improvement |
|:-----------------------------:|:-------:|:-----------:|:-----------:|
| random capacity(milliseconds) | 969873  |    73861    |     13X     |
| fixed capacity(milliseconds)  | 1007342 |    12719    |     79X     |

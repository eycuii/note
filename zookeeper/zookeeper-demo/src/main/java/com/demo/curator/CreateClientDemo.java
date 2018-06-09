package com.demo.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CreateClientDemo {

    public static CuratorFramework createSimple(String connectionString) {

        // 重试策略。每一次重试之间停顿的时间按指数增加。
        // these are reasonable arguments for the ExponentialBackoffRetry. The first
        // retry will wait 1 second - the second will wait up to 2 seconds - the
        // third will wait up to 4 seconds.
        ExponentialBackoffRetry retryPolicy =
                new ExponentialBackoffRetry(1000, 3);

        return CuratorFrameworkFactory.newClient(connectionString, retryPolicy);
    }

    public static CuratorFramework  createWithOptions(
            String connectionString, RetryPolicy retryPolicy,
            int connectionTimeoutMs, int sessionTimeoutMs) {

        return CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                // etc. etc.
                .build();
    }
}

package com.demo.curator;

import com.demo.ZookeeperConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DistributedLockDemo2 {

    static final String countPath = "/count";

    static int count;

    public static void main(String[] args) {
        try {
            concurrencyTestWithDistributedLock(10);
//            concurrencyTestWithDistributedLock(100);
//            concurrencyTestWithDistributedLock(200);
//            concurrencyTestWithDistributedLock(300);
//            concurrencyTestWithDistributedLock(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void concurrencyTestWithDistributedLock(int threadCount) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countdown = new CountDownLatch(threadCount);

        initCount();
        long start = System.currentTimeMillis();

        for(int i=0; i<threadCount; i++) {
            executorService.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    CuratorFramework client =
                            CreateClientDemo.createSimple(ZookeeperConfig.CONNECTION_STRING);
                    client.start();
                    InterProcessMutex lock = new InterProcessMutex(client, countPath);
                    try {
                        lock.acquire();
                        increaseCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if(lock.isAcquiredInThisProcess()) {
                                lock.release();
                            } else {
                                System.out.println(Thread.currentThread().getName()
                                        + "======= fail");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            client.close();
                            countdown.countDown();
                        }
                    }
                }
            },"t" + i));
        }
        countdown.await();
        long end=System.currentTimeMillis();
        executorService.shutdown();
        System.out.println("count:" + count +
                " threadCount:" + threadCount
                + " costTime:" + (end-start)
                + " avg: "+ threadCount*1000/(end-start));
    }

    public static void initCount() throws Exception {
        count = 0;
    }

    public static void increaseCount() throws Exception {
        count++;
        System.out.println("new count::" + count);
    }
}

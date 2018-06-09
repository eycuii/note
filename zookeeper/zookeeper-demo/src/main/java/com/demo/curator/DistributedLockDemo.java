package com.demo.curator;

import com.demo.ZookeeperConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DistributedLockDemo {

    static final CuratorFramework client =
            CreateClientDemo.createSimple(ZookeeperConfig.CONNECTION_STRING);

    static final String countPath = "/count1";

    public static void main(String[] args) {
        client.start();
        try {
            createCount();
//            concurrencyTest();
            concurrencyTestWithDistributedLock();
            printCount();
            deleteCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    public static void concurrencyTest() throws Exception {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countdown = new CountDownLatch(threadCount);

        for(int i=0; i<threadCount; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep((long) (1000*Math.random()));
                        increaseCount();
                        countdown.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        countdown.await();
        executorService.shutdown();
    }

    public static void concurrencyTestWithDistributedLock() throws Exception {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countdown = new CountDownLatch(threadCount);

        for(int i=0; i<threadCount; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    InterProcessMutex lock = new InterProcessMutex(client, countPath);
                    try {
                        lock.acquire(); // 加锁
                        increaseCount();
                        countdown.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            lock.release(); // 释放锁
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        countdown.await();
        executorService.shutdown();
    }

    public static void createCount() throws Exception {
        CrudDemo.create(client, countPath, String.valueOf(0).getBytes());
    }

    public static void deleteCount() throws Exception {
        CrudDemo.delete(client, countPath);
    }

    public static void printCount() throws Exception {
        System.out.println("print count::" +
                new String(CrudDemo.getData(client, countPath)));
    }

    public static void increaseCount() throws Exception {
        int count = convertToInt(CrudDemo.getData(client, countPath));
        count++;
        System.out.println("new count::" + count);
        CrudDemo.setData(client, countPath, convertToByteArray(count));
    }

    public static int convertToInt(byte[] data) {
        return Integer.parseInt(new String(data));
    }

    public static byte[] convertToByteArray(int data) {
        return String.valueOf(data).getBytes();
    }
}

package com.demo.curator;

import com.demo.ZookeeperConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.Arrays;

public class CrudDemo {

    static final CuratorFramework client =
            CreateClientDemo.createSimple(ZookeeperConfig.CONNECTION_STRING);

    public static void create(CuratorFramework client, String path, byte[] payload) throws Exception {
        client.create().forPath(path, payload);
    }

    /**
     * 创建临时节点
     */
    public static void createEphemeral(CuratorFramework client, String path, byte[] payload) throws Exception {
        client.create().withMode(CreateMode.EPHEMERAL).forPath(path, payload);
    }

    public static byte[] getData(CuratorFramework client, String path) throws Exception {
        return client.getData().forPath(path);
    }

    public static void setData(CuratorFramework client, String path, byte[] payload) throws Exception {
        client.setData().forPath(path, payload);
    }

    public static void delete(CuratorFramework client, String path) throws Exception {
        client.delete().forPath(path);
    }

    /**
     * 递归删除
     */
    public static void guaranteedDelete(CuratorFramework client, String path) throws Exception {
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
    }

    public static void main(String[] args) {
        client.start();
        try {
            int i = 0;
            create(client,"/hello", String.valueOf(i).getBytes());
//            create(client, "/hello/hi", "hi~".getBytes());
//            setData(client, "/hello", "world!".getBytes());
//            guaranteedDelete(client, "/hello");
            String value = new String(getData(client,"/hello"));
            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }
}

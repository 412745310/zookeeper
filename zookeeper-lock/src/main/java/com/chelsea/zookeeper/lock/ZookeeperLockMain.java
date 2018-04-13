package com.chelsea.zookeeper.lock;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * Zookeeper锁资源竞争示例
 * 
 * @author shevchenko
 *
 */
public class ZookeeperLockMain {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 1; i++) {
            ThreadClass threadClass = new ThreadClass();
            executor.execute(threadClass);
        }
    }

    static class ThreadClass implements Runnable {

        private String zookeeperIp = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
        private String rootPath = "/lock";

        public void run() {
            try {
                ZooKeeper zk = new ZooKeeper(zookeeperIp, 10000, new Watcher() {
                    public void process(WatchedEvent event) {}
                });
                String node =
                        zk.create(rootPath + "/lock_", null, ZooDefs.Ids.OPEN_ACL_UNSAFE,
                                CreateMode.EPHEMERAL_SEQUENTIAL);
                getAndReleaseLock(zk, node);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings("static-access")
        public void getAndReleaseLock(final ZooKeeper zk, final String node) throws KeeperException,
                InterruptedException {
            List<String> nodeList = zk.getChildren(rootPath, false);
            if (nodeList.isEmpty()) {
                return;
            }
            Collections.sort(nodeList);
            String minNode = rootPath + "/" + nodeList.get(0);
            if (node.equals(minNode)) {
                // 匹配到最小的节点，处理业务，删除临时节点
                System.out.println("线程" + Thread.currentThread().getName() + "获得锁" + minNode);
                Thread.currentThread().sleep(3000);
                System.out.println("线程" + Thread.currentThread().getName() + "释放锁" + minNode);
                zk.delete(node, -1);
            } else {
                // 未匹配到当前节点，设置最小节点的监听事件
                Watcher watcher = new Watcher() {
                    public void process(WatchedEvent event) {
                        if (event.getType() == EventType.NodeChildrenChanged
                                || event.getType() == EventType.NodeDeleted) {
                            try {
                                getAndReleaseLock(zk, node);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                zk.register(watcher);
                zk.getChildren(rootPath, true);
            }
        }
    }
}

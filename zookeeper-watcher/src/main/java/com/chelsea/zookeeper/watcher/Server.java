package com.chelsea.zookeeper.watcher;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 服务端注册类
 * 
 * @author shevchenko
 *
 */
public class Server {
    private static String connectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    private static int sessionTimeout = 2000;
    private ZooKeeper zk = null;
    private String hostName;
    private String groupName = "/servers";

    public Server(String hostName) throws Exception {
        this.hostName = hostName;
        this.zk = new ZooKeeper(connectString, sessionTimeout, null);
    }

    // 将自己的信息注册到zk集群
    public void registToZK() throws Exception {
        // 判断父目录是否存在，不存在则创建
        Stat groupStat = zk.exists(groupName, false);
        if (groupStat == null) {
            zk.create(groupName, "distributed server list".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        String registAddr =
                zk.create(groupName + "/server", hostName.getBytes(), Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL_SEQUENTIAL);

        System.out.println("Server is starting, reg addr：" + registAddr);
    }

    // 下线
    public void offline() throws Exception {
        zk.close();
    }
}

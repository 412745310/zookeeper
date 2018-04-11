package com.chelsea.zookeeper.watcher;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * 客户端监听类
 * 
 * @author shevchenko
 *
 */
public class Client {

    private static String connectString = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    private static int sessionTimeout = 2000;
    private ZooKeeper zk = null;
    private String groupName = "/servers";

    // 开始监听服务器列表变化
    public void startListenServerListChange() throws Exception {
        this.zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 重新注册监听
                try {
                    getServerList();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void getServerList() throws Exception {
        List<String> children = zk.getChildren(groupName, true);
        List<String> servers = new ArrayList<String>();
        for (String child : children) {
            byte[] data = zk.getData(groupName + "/" + child, null, null);
            servers.add(new String(data));
        }
        System.out.println("--------------当前服务器列表--------------");
        for (String server : servers) {
            System.out.println(server);
        }
        System.out.println("\n");
    }

}

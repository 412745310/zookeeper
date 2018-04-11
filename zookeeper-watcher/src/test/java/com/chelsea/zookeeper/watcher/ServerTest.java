package com.chelsea.zookeeper.watcher;

import org.junit.Test;

/**
 * 服务端测试类
 * 
 * @author shevchenko
 *
 */
public class ServerTest {

    @Test
    public void test() throws Exception {
        Server server1 = new Server("server01");
        Server server2 = new Server("server02");
        server1.registToZK();
        server2.registToZK();
        Thread.sleep(5000);
        System.out.println("server01下线...");
        server1.offline();
        Thread.sleep(5000);
        System.out.println("server02下线...");
        server2.offline();
    }

}

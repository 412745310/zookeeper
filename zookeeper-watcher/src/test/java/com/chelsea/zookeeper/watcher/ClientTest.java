package com.chelsea.zookeeper.watcher;

import org.junit.Test;

/**
 * 客户端测试类
 * 
 * @author shevchenko
 *
 */
public class ClientTest {

    @Test
    public void test() throws Exception {
        Client client = new Client();
        client.startListenServerListChange();
        while (true) {
        }
    }

}

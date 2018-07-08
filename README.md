# proxy2808

## example

        String username = "xxxx";
        String password = "xxxx";
        Client client = new Client(username, password);
        Result<List<Proxy>> proxies = client.getProxy(1, 10L); // 获取1个代理，过期时间为10秒
        client.releaseProxies(proxies.getData());  // 释放代理
        proxies = client.listProxy(); // 列出所有已经获取到的代理
        client.releaseAll(); // 释放所有已经获取到的代理

package com.raito.rpc.demo.method;

import com.raito.rpc.common.util.JsonUtils;
import com.raito.rpc.server.annotation.RpcProxy;
import com.raito.rpc.server.annotation.RpcProxyMethod;

/**
 * @author cn
 * @since 2025/6/30 15:44
 * @version 1.0
 */
@RpcProxy(server = "method")
public class MethodServer {
    @RpcProxyMethod
    public String methodList(MethodList list) throws InterruptedException {
        int random = (int)(Math.random() * 10) + 1;
        Thread.sleep(random * 1000L);
        list.setRandom(random * 1000);
        return JsonUtils.toJson(list);
    }
}

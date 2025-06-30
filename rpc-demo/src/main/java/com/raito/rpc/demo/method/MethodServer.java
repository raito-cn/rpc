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
    public String methodList(MethodList list) {
        return JsonUtils.toJson(list);
    }
}

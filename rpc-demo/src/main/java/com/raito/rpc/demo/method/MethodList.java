package com.raito.rpc.demo.method;

import com.raito.rpc.server.annotation.RpcProxy;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author raito
 * @since 2025/6/29
 */
@Component
@Data
@RpcProxy
public class MethodList {
    @Value("${name:method_list}")
    private String name;

    @Value("${methodName:raito}")
    private String methodName;
}

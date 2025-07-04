package com.raito.rpc.client.strategy;

import com.raito.rpc.client.annotation.RpcRemote;
import com.raito.rpc.common.strategy.ScanStrategy;
import org.springframework.util.StringUtils;

/**
 * @author cn
 * @since 2025/7/4 17:11
 * @version 1.0
 */
public class RpcClientProxyStrategy extends ScanStrategy {
    @Override
    public void process(Class<?> clazz) {
        RpcRemote annotation = clazz.getAnnotation(RpcRemote.class);
        if (annotation == null) return;

        String server = annotation.server();
        String contextId = StringUtils.hasText(annotation.contextId()) ? annotation.contextId() : (clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1));
    }

    @Override
    public Runnable after() {
        return null;
    }
}

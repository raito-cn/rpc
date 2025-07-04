package com.raito.rpc.server.strategy;

import com.raito.rpc.common.event.ApplicationStartedListener;
import com.raito.rpc.common.strategy.ApplicationStartedStrategy;
import com.raito.rpc.server.helper.NettyStartHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;

/**
 * @author cn
 * @since 2025/7/4 15:20
 * @version 1.0
 */
@Slf4j
public class StartNettyServer extends ApplicationStartedStrategy {
    private NettyStartHelper nettyStartHelper;

    @Override
    public void run(ApplicationStartedListener listener, ApplicationStartedEvent event) {
        try {
            nettyStartHelper.getManager().start();
        } catch (Exception e) {
            log.error("启动 Netty 服务失败", e);
        }
    }

    @Override
    public int order() {
        return 1;
    }

    @Autowired
    public void setNettyStartHelper(NettyStartHelper nettyStartHelper) {
        this.nettyStartHelper = nettyStartHelper;
    }
}

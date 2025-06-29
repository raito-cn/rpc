package com.raito.rpc.server.helper;

import com.raito.rpc.server.manager.NettyServerManager;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author raito
 * @since 2025/6/29
 */
@Data
@AllArgsConstructor
public class NettyStartHelper {
    private NettyServerManager manager;
}

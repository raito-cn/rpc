package com.raito.rpc.server.helper;

import lombok.Data;

/**
 * @author cn
 * @since 2025/7/2 16:47
 * @version 1.0
 */
@Data
public class SeedMessageHelper {
    // 0 等待发送
    // 1 已超时 不用发送
    private short seed = 0;

    public synchronized short getSeed() {
        return seed;
    }

    public synchronized void setSeed(short seed) {
        this.seed = seed;
    }
}

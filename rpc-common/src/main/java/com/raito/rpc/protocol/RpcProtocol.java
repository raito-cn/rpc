package com.raito.rpc.protocol;

import lombok.Builder;
import lombok.Data;

/**
 * @author cn
 * @since 2025/6/25 15:53
 * @version 1.0
 */
@Data
@Builder
public class RpcProtocol {
    /**
     * 魔数 2
     */
    private short magic;

    /**
     * 版本 1
     */
    private byte version;

    /**
     * 0 = request, 1 = response
     * 1
     */
    private byte mesType;
    /**
     * 序列化方式标识
     * 1
     */
    private byte codecType;
    /**
     * 压缩方式
     * !
     */
    private byte compressType;
    /**
     * request id
     * 8
     */
    private long requestId;

    /**
     * 数据长度
     * 4
     */
    private int payloadLength;

    /**
     *
     */
    private byte[] payload;
}

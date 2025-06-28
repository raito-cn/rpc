package com.raito.rpc.common.codec;

import com.raito.rpc.common.constant.RpcConstant;
import lombok.Builder;
import lombok.Data;

/**
 * @author cn
 * @since 2025/6/25 14:58
 * @version 1.0
 */
@Data
@Builder
public class RpcEncoderWrapper {
    /**
     * 魔数 2
     */
    @Builder.Default
    private short magic = RpcConstant.MAGIC;

    /**
     * 版本 1
     */
    @Builder.Default
    private byte version = 1;

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
     * 消息体
     */
    private Object body;

    @Override
    public String toString() {
        return "RpcEncoderWrapper{" +
                "magic=" + magic +
                ", version=" + version +
                ", mesType=" + mesType +
                ", codecType=" + codecType +
                ", compressType=" + compressType +
                ", requestId=" + requestId +
                ", body=" + body +
                '}';
    }
}

package com.raito.rpc.client.annotation;

import java.lang.annotation.*;

/**
 * @author raito
 * @since 2025/6/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
public @interface RpcRemote {
}

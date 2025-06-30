package com.raito.rpc.server.annotation;

import java.lang.annotation.*;

/**
 * @author cn
 * @since 2025/6/30 11:19
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Inherited
public @interface RpcProxyMethod {
    String methodName() default "";

    String server() default "";
}

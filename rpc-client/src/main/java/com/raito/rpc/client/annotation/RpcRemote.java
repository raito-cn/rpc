package com.raito.rpc.client.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author raito
 * @since 2025/6/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Inherited
@Component
public @interface RpcRemote {
    String server();

    String contextId() default "";
}

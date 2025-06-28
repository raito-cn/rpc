package com.raito.rpc.server.annotation;

import java.lang.annotation.*;

/**
 * @author cn
 * @since 2025/6/27 16:08
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Inherited
public @interface RpcProxy {

}

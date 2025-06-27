package function;

/**
 * @author cn
 * @since 2025/6/27 18:02
 * @version 1.0
 */
@FunctionalInterface
public interface RpcMethodInvoker {
    Object invoke(Object... args);
}

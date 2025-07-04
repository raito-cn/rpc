package com.raito.rpc.server.factory;

import com.raito.rpc.common.exception.ProxyException;
import com.raito.rpc.common.factory.BeanFactory;
import com.raito.rpc.common.util.JsonUtils;
import com.raito.rpc.server.classloader.RpcProxyClass;
import com.raito.rpc.server.helper.MethodHelper;
import org.objectweb.asm.*;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author raito
 * @since 2025/6/28
 */
@SuppressWarnings("SpellCheckingInspection")
public class ProxyGenerator {
    public static final String RPC_PROXY_CLASS_INTERNAL_NAME = "com/raito/rpc/server/proxy/";
    public static final String RPC_PROXY_CLASS_INTERNAL_JVM_NAME = "com.raito.rpc.server.proxy.";

    public static byte[] generateProxyClass(String server, List<MethodHelper> helpers) {
        String internalClassName = "%s%s$Proxy".formatted(RPC_PROXY_CLASS_INTERNAL_NAME, server);

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        // 定义类：public class XxxProxy extends ProxyClass
        cw.visit(Opcodes.V21, Opcodes.ACC_PUBLIC,
                internalClassName,
                null,
                Type.getInternalName(RpcProxyClass.class),
                null);

        // 构造方法
        MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null, // 泛型签名（可选，也可以为 null）
                null);
        mv.visitCode();

        // super();
        mv.visitVarInsn(Opcodes.ALOAD, 0); // this
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                Type.getInternalName(RpcProxyClass.class), // 父类 internal name
                "<init>",
                "()V",        // 父类构造方法签名
                false);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1); // 栈深度 2，局部变量 2（this）
        mv.visitEnd();

        Map<String, MethodHelper> map = getMethods(helpers);
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "invoke",
                "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
                null, null);
        // 添加 @Override 注解
        mv.visitAnnotation("Ljava/lang/Override;", true).visitEnd();
        mv.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0); // this
        mv.visitVarInsn(Opcodes.ALOAD, 1); // methodName
        // String key = encode(methodName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                internalClassName,
                "encode",
                "(Ljava/lang/String;)Ljava/lang/String;",
                false);
        mv.visitVarInsn(Opcodes.ASTORE, 3); // key 存入局部变量槽 3

        generateInvokeMethod(mv, map);

        cw.visitEnd();
        return cw.toByteArray();
    }

    private static Map<String, MethodHelper> getMethods(List<MethodHelper> helpers) {
        Map<String, MethodHelper> map = new LinkedHashMap<>();
        for (var helper : helpers) {
            String methodName = helper.getMethodName();
            String key = RpcProxyClass.encode(methodName);
            if (map.containsKey(key)) {
                throw new ProxyException("Duplicate method name: " + methodName);
            }
            map.put(key, helper);
        }
        return map;
    }

    private static void generateInvokeMethod(MethodVisitor mv, Map<String, MethodHelper> methodMap) {
        for (Map.Entry<String, MethodHelper> entry : methodMap.entrySet()) {
            String key = entry.getKey();
            MethodHelper helper = entry.getValue();
            Method method = helper.getMethod();
            Class<?>[] paramTypes = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();

            Label nextLabel = new Label();

            // if (!key.equals("...")) goto nextLabel;
            mv.visitVarInsn(Opcodes.ALOAD, 3); // key
            mv.visitLdcInsn(key);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals",
                    "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(Opcodes.IFEQ, nextLabel);

            // 匹配成功，调用目标方法 BeanFactory.getBean(method.getDeclaringClass())).method(...)
            mv.visitLdcInsn(Type.getType(method.getDeclaringClass()));
            mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    BeanFactory.class.getName().replace(".", "/"), // internal name of BeanFactory
                    "getBean",
                    "(Ljava/lang/Class;)Ljava/lang/Object;",
                    false
            );
            // 强制类型转换为目标类
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(method.getDeclaringClass()));

            // 加载并强转参数
            for (int i = 0; i < paramTypes.length; i++) {
                mv.visitVarInsn(Opcodes.ALOAD, 2); // args
                mv.visitLdcInsn(i);
                mv.visitInsn(Opcodes.AALOAD);

                Class<?> pType = paramTypes[i];
                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");// args[i] 是字符串
                if (pType != String.class) {
                    mv.visitLdcInsn(Type.getType(pType)); // T.class
                    mv.visitMethodInsn(
                            Opcodes.INVOKESTATIC,
                            JsonUtils.class.getName().replace(".", "/"),
                            "fromJson",
                            "(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;",
                            false
                    );
                    mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(pType)); // 转成 T
                }
            }

            // 调用方法
            int opcode = method.getDeclaringClass().isInterface() ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
            mv.visitMethodInsn(opcode,
                    Type.getInternalName(method.getDeclaringClass()),
                    method.getName(),
                    Type.getMethodDescriptor(method),
                    opcode == Opcodes.INVOKEINTERFACE);

            // 返回值处理
            if (returnType == void.class) {
                mv.visitInsn(Opcodes.ACONST_NULL);
            } else if (returnType.isPrimitive()) {
                boxPrimitive(mv, returnType);
            }
            mv.visitInsn(Opcodes.ARETURN);

            mv.visitLabel(nextLabel);
        }

        Label defaultLabel = new Label();
        // 默认分支，抛异常
        mv.visitLabel(defaultLabel);
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/UnsupportedOperationException");
        mv.visitInsn(Opcodes.DUP);
        mv.visitLdcInsn("Unknown method key");
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V", false);
        mv.visitInsn(Opcodes.ATHROW);

        mv.visitMaxs(5, 6);
        mv.visitEnd();
    }

    // 辅助：装箱方法
    private static void boxPrimitive(MethodVisitor mv, Class<?> type) {
        if (type == int.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (type == long.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (type == boolean.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if (type == double.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        } else if (type == float.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (type == short.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if (type == byte.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (type == char.class) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else {
            throw new IllegalArgumentException("Unsupported primitive type: %s".formatted(type));
        }
    }
}

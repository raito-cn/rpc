package com.raito.rpc.server.factory;

import com.raito.rpc.server.classloader.RpcProxyClass;
import com.raito.rpc.common.exception.BoxException;
import com.raito.rpc.common.exception.UnboxException;
import org.objectweb.asm.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author raito
 * @since 2025/6/28
 */
public class ProxyGenerator {
    public static final String RPC_PROXY_CLASS_INTERNAL_NAME = "com/raito/rpc/proxy/";

    public static String getClassProxyName(Class<?> clazz, boolean isJVM) {
        String className = RPC_PROXY_CLASS_INTERNAL_NAME + clazz.getSimpleName() + "$Proxy";
        if (!isJVM) {
            className = className.replace('/', '.');
        }
        return className;
    }

    public static byte[] generateProxyClass(Class<?> originalClass, List<Method> methods) {
        String internalClassName = getClassProxyName(originalClass, true);

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
                "(Ljava/lang/Object;)V",
                null,
                null);
        mv.visitCode();

        // super(originalClass);
        mv.visitVarInsn(Opcodes.ALOAD, 0); // this
        mv.visitVarInsn(Opcodes.ALOAD, 1); // originalClass 参数
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                Type.getInternalName(RpcProxyClass.class), // 父类 internal name
                "<init>",
                "(Ljava/lang/Object;)V",                   // 父类构造方法签名
                false);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 2); // 栈深度 2，局部变量 2（this + 参数）
        mv.visitEnd();

        Map<String, Method> map = getMethodMap(methods);
        mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "invoke",
                "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/Object;",
                null, null);
        // 添加 @Override 注解
        mv.visitAnnotation("Ljava/lang/Override;", true).visitEnd();
        mv.visitCode();

        // String key = encode(returnType, methodName, argTypes);
        mv.visitVarInsn(Opcodes.ALOAD, 0); // this
        mv.visitVarInsn(Opcodes.ALOAD, 1); // returnType
        mv.visitVarInsn(Opcodes.ALOAD, 2); // methodName
        mv.visitVarInsn(Opcodes.ALOAD, 3); // argTypes
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                internalClassName,
                "encode",
                "(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;",
                false);
        mv.visitVarInsn(Opcodes.ASTORE, 5); // key 存入局部变量槽 5

        generateInvokeMethod(mv, internalClassName, map);

        cw.visitEnd();
        return cw.toByteArray();
    }

    private static Map<String, Method> getMethodMap(List<Method> methods) {
        Map<String, Method> map = new LinkedHashMap<>();
        for (var method : methods) {
            String returnType = method.getReturnType().getTypeName();
            String methodName = method.getName();
            String[] argTypes = Arrays.stream(method.getParameterTypes()).map(Class::getName).toArray(String[]::new);
            String encode = RpcProxyClass.encode(returnType, methodName, argTypes);
            map.put(encode, method);
        }
        return map;
    }

    private static void generateInvokeMethod(MethodVisitor mv, String proxyInternalName, Map<String, Method> methodMap) {
        for (Map.Entry<String, Method> entry : methodMap.entrySet()) {
            String key = entry.getKey();
            Method method = entry.getValue();
            Class<?>[] paramTypes = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();

            Label nextLabel = new Label();

            // if (!key.equals("...")) goto nextLabel;
            mv.visitVarInsn(Opcodes.ALOAD, 5); // key
            mv.visitLdcInsn(key);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals",
                    "(Ljava/lang/Object;)Z", false);
            mv.visitJumpInsn(Opcodes.IFEQ, nextLabel);

            // 匹配成功，调用目标方法 ((TargetClass)targetInstance).method(...)
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, proxyInternalName, "targetInstance", "Ljava/lang/Object;");
            mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(method.getDeclaringClass()));

            // 加载并强转参数
            for (int i = 0; i < paramTypes.length; i++) {
                mv.visitVarInsn(Opcodes.ALOAD, 4); // args
                mv.visitLdcInsn(i);
                mv.visitInsn(Opcodes.AALOAD);

                Class<?> pType = paramTypes[i];
                if (pType.isPrimitive()) {
                    // 反箱操作，如 Integer -> int
                    unboxPrimitive(mv, pType);
                } else {
                    mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(pType));
                }
            }

            // 调用方法
            int opcode = method.getDeclaringClass().isInterface() ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL;
            mv.visitMethodInsn(opcode,
                    Type.getInternalName(method.getDeclaringClass()),
                    method.getName(),
                    Type.getMethodDescriptor(method),
                    opcode == Opcodes.INVOKEINTERFACE);

            // 返回值装箱处理
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

    // 辅助：拆箱方法
    private static void unboxPrimitive(MethodVisitor mv, Class<?> type) {
        if (type == int.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        } else if (type == long.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
        } else if (type == boolean.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
        } else if (type == double.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
        } else if (type == float.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
        } else if (type == short.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
        } else if (type == byte.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
        } else if (type == char.class) {
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
        } else {
            throw new BoxException("Unsupported primitive: " + type);
        }
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
            throw new UnboxException("Unsupported primitive: " + type);
        }
    }
}

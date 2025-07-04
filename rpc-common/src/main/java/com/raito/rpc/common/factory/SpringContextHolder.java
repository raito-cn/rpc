package com.raito.rpc.common.factory;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author raito
 * @since 2025/6/28
 */
public class SpringContextHolder implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        SpringContextHolder.context = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        try {
            return (T) context.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> originalClass) {
        try {
            return context.getBean(originalClass);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> void destroy(String beanName) {
        DefaultListableBeanFactory factory =
                (DefaultListableBeanFactory) ((ConfigurableApplicationContext) context).getBeanFactory();

        factory.destroySingleton(beanName); // 从单例池中移除实例
        if (factory.containsBeanDefinition(beanName)) {
            factory.removeBeanDefinition(beanName);
        }
    }
}
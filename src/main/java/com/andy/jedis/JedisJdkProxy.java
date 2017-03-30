
package com.andy.jedis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * 获取jedis JDK代理
 *
 * @version $Revision: $
 * @since 1.0
 */

public class JedisJdkProxy {

    private JedisPool jedisPool;

    public JedisJdkProxy(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 默认已以redisCommands 提供服务
     *
     * @return JedisCommands
     */
    public JedisCommands getJedisJdkProxy() {

        Jedis resource = jedisPool.getResource();
        JedisHandler jedisHandler = new JedisHandler(resource);
        Object proxy = Proxy.newProxyInstance(resource.getClass().getClassLoader(),
                resource.getClass().getInterfaces(), jedisHandler);
        return (JedisCommands) proxy;
    }

    /**
     * 返回指定的命令接口
     *
     * @param clazz
     * @param <T>   命令接口 JedisCommands, MultiKeyCommands,
     *              AdvancedJedisCommands, ScriptingCommands,
     *              BasicCommands, ClusterCommands, SentinelCommands
     * @return
     */
    public <T> T getJedisJdkProxy(Class<T> clazz) {
        Jedis resource = jedisPool.getResource();
        JedisHandler jedisHandler = new JedisHandler(resource);
        Object proxy = Proxy.newProxyInstance(resource.getClass().getClassLoader(),
                resource.getClass().getInterfaces(), jedisHandler);
        return (T) proxy;
    }
}

class JedisHandler implements InvocationHandler {
    private Jedis target;

    public JedisHandler(Jedis target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Object object = null;
        //jdk代理每次调用前都toString,但不需要释放资源,不然会有target.close()抛出资源已返还
        for (Method method1 : Arrays.asList(Object.class.getDeclaredMethods())) {
            if (method1.getName().equals(method.getName())) return object;
        }

        try {
            object = method.invoke(target, args);
        } finally {
            target.close();
        }

        return object;

    }

}


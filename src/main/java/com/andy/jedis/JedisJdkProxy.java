
package com.andy.jedis;


import com.andy.utils.MethodMatchTool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

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
     * 默认已以redisCommands 提供服务,每一次都会返回redis连接,
     * 务必每次要getInstance
     *
     * @return JedisCommands
     */
    public JedisCommands getInstance() {

        Jedis resource = jedisPool.getResource();
        JedisHandler jedisHandler = new JedisHandler(resource,JedisCommands.class);
        Object proxy = Proxy.newProxyInstance(resource.getClass().getClassLoader(),
                resource.getClass().getInterfaces(), jedisHandler);
        return (JedisCommands) proxy;
    }

    /**
     * 返回对象可以重复使用,但是使用结束后必须调用close返回资源到连接池
     * @return
     */
    public Jedis getJedisResouce() {
        return jedisPool.getResource();
    }
    /**
     * 每次必须访问redis 必须调用getInstance 不能重复使用代理资源
     * 返回指定的命令接口
     *
     * @param clazz   命令接口 JedisCommands, MultiKeyCommands,
     *              AdvancedJedisCommands, ScriptingCommands,
     *              BasicCommands, ClusterCommands, SentinelCommands
     * @return
     */
    public <T> T getInstance(Class<T> clazz) {
        if(!clazz.isInterface()){
            throw new IllegalArgumentException("JDK class proxy must be interface");
        }
        Jedis resource = jedisPool.getResource();
        JedisHandler jedisHandler = new JedisHandler(resource,clazz);
        Object proxy = Proxy.newProxyInstance(resource.getClass().getClassLoader(),
                resource.getClass().getInterfaces(), jedisHandler);
        return (T) proxy;
    }
}

class JedisHandler implements InvocationHandler {
    private Jedis target;
    private Class clazz;
    
    public JedisHandler(Jedis target,Class clazz) {
        this.target = target;
        this.clazz=clazz;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Object object = null;

        if(MethodMatchTool.matchMethod(method,clazz)){
            try {
                object = method.invoke(target, args);
            } finally {
                target.close();
            }
        }

        return object;

    }


}


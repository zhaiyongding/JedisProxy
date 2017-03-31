
package com.andy.jedis;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
     * 默认已以redisCommands 提供服务
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
     * 返回指定的命令接口
     *
     * @param clazz
     * @param <T>   命令接口 JedisCommands, MultiKeyCommands,
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
    private static ConcurrentHashMap <String,HashSet<String>>methodCache=new ConcurrentHashMap<String,HashSet<String>>();
    
    public JedisHandler(Jedis target,Class clazz) {
        this.target = target;
        this.clazz=clazz;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        Object object = null;

        if(matchMethod(method)){
            try {
                object = method.invoke(target, args);
            } finally {
                target.close();
            }
        }

        return object;

    }
    //jdk代理每次调用前都toString,但不需要释放资源,不然会有target.close()抛出资源已返还
    private  Boolean matchMethod(Method method){
        //增强cache
        for (Method method1 : Arrays.asList(clazz.getDeclaredMethods())) {
            HashSet<String> methodSet = methodCache.get(clazz.getName());
            if (methodSet!=null){
                methodSet.contains(method1);
            }else{
                methodSet=new HashSet<String>();
                methodCache.put(clazz.getName(),methodSet);
            }
            if (method1.getName().equals(method.getName())){
                //加入cache
                methodSet.add(method.getName());
                return true;
            }
            
        }
        return false;
    }

}


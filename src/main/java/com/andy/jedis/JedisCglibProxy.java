
package com.andy.jedis;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 获取jedis cglib代理
 *
 * @since 1.0
 */

public class JedisCglibProxy {

    private JedisPool jedisPool;

    public JedisCglibProxy(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 返回命令接口
     *
     * @return
     */
    public Jedis getJedisCglibProxy() {
        Jedis jedis = jedisPool.getResource();
        Jedis jedisProxy = new CglibProxy().getProxy(jedis);
        jedisProxy.setDataSource(jedisPool);
        return jedis;
    }
}

class CglibProxy implements MethodInterceptor {
    private Jedis jedis;

    private Enhancer enhancer = new Enhancer();

    public Jedis getProxy(Jedis resource) {
        this.jedis = resource;
        enhancer.setSuperclass(Jedis.class);//设置创建子类的类
        enhancer.setCallback(this);
        enhancer.setClassLoader(resource.getClass().getClassLoader());
        //通过字节码技术动态创建子类实例,Cglib不支持代理类无空构造,
        //Jedis 2.7 开始有空构造
        return (Jedis) enhancer.create();
    }

    @Override
    public Object intercept(Object target, Method method, Object[] args,
                            MethodProxy proxy) throws Throwable {
        Object object = null;
        List<Method> methods = Arrays.asList(Object.class.getDeclaredMethods());
        for(Method method1:methods){
            if (method1.getName().equals(method.getName())) return object;
        }
        try {
            object = proxy.invokeSuper(target, args);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            jedis.close();
        }

        return object;
    }
}


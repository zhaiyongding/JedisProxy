
package com.andy.jedis;


import net.sf.cglib.proxy.*;
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

    private Enhancer enhancer = new Enhancer();

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

        enhancer.setSuperclass(Jedis.class);//设置创建子类的类
        enhancer.setCallbacks(new Callback[]{new CglibProxy(jedis),new CglibProxyNoOp()});
        enhancer.setClassLoader(jedis.getClass().getClassLoader());
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                if("decr".equals(method.getName())){
                    return 0;
                }
                //默认调用CglibProxyNoOp
                return 1;
            }
        });
        //通过字节码技术动态创建子类实例,Cglib不支持代理类无空构造,
        //Jedis 2.7 开始有空构造
        Jedis jedisProxy=  (Jedis) enhancer.create();
        jedisProxy.setDataSource(jedisPool);

        jedis.getClient();
        //jedisProxy.getClient() is null
        //TODO how to fix
        return jedisProxy;
    }
}

class CglibProxy implements MethodInterceptor {
    private Jedis jedis;

    public CglibProxy(Jedis jedis) {
        this.jedis = jedis;
    }
    @Override
    public Object intercept(Object target, Method method, Object[] args,
                            MethodProxy proxy) throws Throwable {
        Object object = null;
        //cglib代理会调用Object中的toString和hashCode方法,但不需要释放资源,不然会有target.close()抛出资源已返还
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
class CglibProxyNoOp implements MethodInterceptor {
    @Override
    public Object intercept(Object target, Method method, Object[] args,
                            MethodProxy proxy) throws Throwable {
        Object object = null;
        try {
            object = proxy.invokeSuper(target, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }
}



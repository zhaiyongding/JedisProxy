
package com.andy.jedis;


import com.andy.utils.MethodMatchTool;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;

/**
 * 获取jedis cglib代理
 *
 * @since 1.0
 */

public class JedisCglibProxy {

    private JedisPool jedisPool;

    private Enhancer enhancer = new Enhancer();

    public JedisCglibProxy(JedisPool jedisPool) {
        enhancer.setSuperclass(Jedis.class);//设置创建子类的类
        this.jedisPool = jedisPool;

    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 返回对象可以重复使用,但是使用结束后必须调用close返回资源到连接池
     *
     * @return
     */
    public Jedis getJedisResouce() {
        return jedisPool.getResource();
    }

    /**
     * 每次必须访问redis 必须调用getInstance 不能重复使用代理资源
     *
     * @return 命令接口
     */
    public Jedis getInstance() {
        final Jedis jedis = jedisPool.getResource();

        //enhancer.setClassLoader(Thread.currentThread().getContextClassLoader());
        enhancer.setCallback(new MethodInterceptor(){
            @Override
            public Object intercept(Object target, Method method, Object[] args,
                                    MethodProxy proxy) throws Throwable {
                Object object = null;
                //cglib代理会调用Object中的toString和hashCode方法,但不需要释放资源,不然会有target.close()抛出资源已返还
                try {
                    object = proxy.invoke(jedis, args);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (MethodMatchTool.methodCacheCglib.contains(method.getName()))
                        jedis.close();
                }

                return object;
            }
        });
        //通过字节码技术动态创建子类实例,Cglib不支持代理类无空构造,
        //Jedis 2.7 开始有空构造
        Jedis jedisProxy = (Jedis) enhancer.create();
        return jedisProxy;
    }
}





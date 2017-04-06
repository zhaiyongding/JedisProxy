
package com.andy.jedis;


import com.andy.utils.MethodMatchTool;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.framework.ProxyFactory;
import redis.clients.jedis.*;

import java.lang.reflect.Method;


public class JedisSpringAop {
    private JedisPool jedisPool;
    private ProxyFactory pf = new ProxyFactory();

    public JedisSpringAop(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        //只需要一个advice
        pf.addAdvice(new JedisAopAdvice());
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
     * @return
     */
    public Jedis getInstance() {
        pf.setTarget(jedisPool.getResource());
        Jedis jedisProxy = (Jedis) pf.getProxy();
        return jedisProxy;
    }

}

//MethodBeforeAdvice, , ThrowsAdvice
class JedisAopAdvice implements AfterReturningAdvice {

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {

        if(MethodMatchTool.methodCacheCglib.contains(method.getName())){
            try{
                ((Jedis) target).close();
            }catch (Exception e){
                throw e;
            }
        }
    }

}


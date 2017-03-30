
package com.andy.jedis;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.aop.framework.ProxyFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class JedisSpringAop {
    private JedisPool jedisPool;

    public JedisSpringAop(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private ProxyFactory pf = new ProxyFactory();

    public Jedis getProxy() {
        pf.setTarget(jedisPool.getResource());
        pf.addAdvice(new JedisAopAdvice());
        Jedis jedisProxy = (Jedis) pf.getProxy();
        jedisProxy.setDataSource(jedisPool);
        return jedisProxy;
    }

}

//MethodBeforeAdvice, , ThrowsAdvice
class JedisAopAdvice implements AfterReturningAdvice {

    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println(method.getName());
        List<Method> methods = Arrays.asList(Object.class.getDeclaredMethods());
        for (Method method1 : methods) {
            if (method1.getName().equals(method.getName())) return;
        }
        try {
            //System.out.println(target);
        } finally {
            ((Jedis) target).close();
        }
    }
}


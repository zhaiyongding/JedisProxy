
package com.andy.jedis;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.aop.framework.ProxyFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class JedisSpringAop {
    private JedisPool jedisPool;

    public JedisSpringAop(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private ProxyFactory pf = new ProxyFactory();

    public Jedis getInstance() {
        pf.setTarget(jedisPool.getResource());
        pf.addAdvice(new JedisAopAdvice());
        Jedis jedisProxy = (Jedis) pf.getProxy();
        //jedisProxy.setDataSource(jedisPool);
        return jedisProxy;
    }

}

//MethodBeforeAdvice, , ThrowsAdvice
class JedisAopAdvice implements AfterReturningAdvice {
    private static HashSet<String> methodCache;
    static {
        methodCache=new HashSet<String>();
        for (Method method1 : Arrays.asList(JedisCommands.class.getDeclaredMethods())) {
                //加入cache
                methodCache.add(method1.getName());
        }
    }
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        if(methodCache.contains(method.getName())){
            try{
                ((Jedis) target).close();
            }catch (JedisException exception){
                // no thing to do //重复调用造成 Object has already been returned to this pool or is invalid
            } finally{

            }
        }
        System.out.println(""+args[0]);
    }

}


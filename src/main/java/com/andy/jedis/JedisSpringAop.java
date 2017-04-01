
package com.andy.jedis;


import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.framework.ProxyFactory;
import redis.clients.jedis.*;

import java.lang.reflect.Method;
import java.util.*;

public class JedisSpringAop {
    private JedisPool jedisPool;
    private ProxyFactory pf = new ProxyFactory();

    public JedisSpringAop(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        //只需要一个advice
        pf.addAdvice(new JedisAopAdvice());
    }

    public Jedis getInstance() {
        pf.setTarget(jedisPool.getResource());
        Jedis jedisProxy = (Jedis) pf.getProxy();
        return jedisProxy;
    }

}

//MethodBeforeAdvice, , ThrowsAdvice
class JedisAopAdvice implements AfterReturningAdvice {
    private static HashSet<String> methodCache;
    static {
        methodCache = new HashSet<String>(256);
        ArrayList <Method> allMethodList=new ArrayList<Method>(256);
        Collections.addAll(allMethodList,JedisCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList,MultiKeyCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList,AdvancedJedisCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList,BasicCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList,ClusterCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList,SentinelCommands.class.getDeclaredMethods());
        for (Method methodInner : allMethodList) {
            //加入cache
            methodCache.add(methodInner.getName());
        }
    }
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {

        if(methodCache.contains(method.getName())){
            try{
                ((Jedis) target).close();
            }catch (Exception e){
                throw e;
            }
        }
    }

}


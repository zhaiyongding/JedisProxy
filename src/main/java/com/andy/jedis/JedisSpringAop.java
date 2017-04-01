
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


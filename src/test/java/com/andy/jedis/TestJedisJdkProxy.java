package com.andy.jedis;

import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhaiyongding on 2016/8/11.
 */
@Slf4j
public class TestJedisJdkProxy {

    static JedisPool jedisPool= null;
    @BeforeClass
    public static void init(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxWaitMillis(3000);
        jedisPool= new JedisPool( new JedisPoolConfig(),"120.26.106.94",6379,3000,"admin",2);

    }

    @Test
    public  void  testJedisProxy(){
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for(int i=0;i<20;i++){
            final  int temp =i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    Jedis resource = jedisPool.getResource();
                    JedisHandler jedisHandler=new JedisHandler(resource,JedisCommands.class);
                    JedisCommands proxy = (JedisCommands) Proxy.newProxyInstance(resource.getClass().getClassLoader(),
                            resource.getClass().getInterfaces(), jedisHandler);
                    log.info("before"+temp+"::{},{}",Thread.currentThread().getName(),jedisPool.getNumActive());
                    proxy.decr("testjedis:"+temp);
                    log.info("after"+temp+"::{},{}",Thread.currentThread().getName(),jedisPool.getNumActive());
                }
            });

        }
        try {
            Thread.currentThread().sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("over.....");
    }

    @Test
    public  void  testJedisProxy2(){
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for(int i=0;i<100;i++){
            final  int temp =i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    JedisJdkProxy jedisJdkProxy=new JedisJdkProxy(jedisPool);
                    jedisJdkProxy.getJedisJdkProxy(JedisCommands.class).decr("testjedis:3");
                    log.info("after"+temp+"::{},{}",Thread.currentThread().getName(),jedisPool.getNumActive());

                }
            });

        }
        try {
            Thread.currentThread().sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("over.....");
    }

    @Test
    public void testJedisProxy3() {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int temp = i;

            JedisJdkProxy jedisJdkProxy = new JedisJdkProxy(jedisPool);

            Jedis jedisJdkProxy1 = jedisJdkProxy.getJedisJdkProxy(Jedis.class);
            log.info("before" + temp + "::{},{}", Thread.currentThread().getName(), jedisPool.getNumActive());

            Long res=jedisJdkProxy1.decr("testjedis:3");

            log.info("after" + temp + "::{},{}", Thread.currentThread().getName(), jedisPool.getNumActive());

        }
        log.info("over.....");
    }
    @Test
    public void testJedisProxy5() {
        for (Method method1 : Arrays.asList(Jedis.class.getDeclaredMethods())) {
            log.info(method1.getName());
        }
    }
}


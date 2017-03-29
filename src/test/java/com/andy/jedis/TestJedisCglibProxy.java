package com.andy.jedis;

import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhaiyongding on 2016/8/11.
 */
@Slf4j
public class TestJedisCglibProxy {

    static JedisPool jedisPool = null;

    @BeforeClass
    public static void init() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxWaitMillis(3000);
        jedisPool = new JedisPool(new JedisPoolConfig(), "120.26.106.94", 6379, 3000, "admin", 2);
    }


    @Test
    public void testJedisProxyCglib() {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 100; i++) {
            final int temp = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    JedisCglibProxy jedisCglibProxy = new JedisCglibProxy(jedisPool);
                    //jedisJdkProxy.getJedisJdkProxyCommands().decr("testjedis:1");
                    log.info("before" + temp + "::{},{}", Thread.currentThread().getName(), jedisPool.getNumActive());
                    jedisCglibProxy.getJedisCglibProxy().decr("testjedis:4");
                    log.info("after" + temp + "::{},{}", Thread.currentThread().getName(), jedisPool.getNumActive());

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
    public void testJedisProxyCglib2() {
        //ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            JedisCglibProxy jedisCglibProxy = new JedisCglibProxy(jedisPool);
            //jedisJdkProxy.getJedisJdkProxyCommands().decr("testjedis:1");
            Jedis jedisCglibProxy1 = jedisCglibProxy.getJedisCglibProxy();
            Long ret=jedisCglibProxy1.decr("testjedis:2");
            log.info("after" + i + "::{},{}::::::{}", Thread.currentThread().getName(), jedisPool.getNumActive(),ret);
        }
    }


}


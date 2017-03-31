package com.andy.spring;

import com.andy.jedis.JedisJdkProxy;
import com.andy.jedis.JedisSpringAop;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ContextConfiguration(locations = "classpath:/applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class SpringTest {

    @Autowired
    private JedisJdkProxy jedisJdkProxy;


    @Autowired
    private JedisSpringAop jedisSpringAop;

    @Test
    public void testredisProxy() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {

                    log.info("before" + temp + "::{}", Thread.currentThread().getName());

                    jedisJdkProxy.getInstance().zincrby("testjedis121:" + temp, 1.0, temp + "");

                    log.info("after" + temp + "::{}", Thread.currentThread().getName());

                }
            });

        }
        Thread.currentThread().sleep(10000L);
        log.info("over.....");
    }

    @Test
    public void testRedisSpringProxy() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("before" + temp + "---{}", Thread.currentThread().getName());
                    jedisJdkProxy.getInstance().zincrby("testjedis123:" + temp, 1.0, temp + "");
                    log.info("after" + temp + "---{}", Thread.currentThread().getName());
                }
            });

        }
        Thread.currentThread().sleep(10000L);
        log.info("over.....");
    }

    @Test
    public void testRedisSpringProxy2() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            int temp = 0;
            log.info("after---{},{}", Thread.currentThread().getName(),temp++);
            Jedis instance = jedisSpringAop.getInstance();
            log.info("after---{},{}", Thread.currentThread().getName(),temp++);
            Long res= instance.incr("testjedis123:");
            log.info("after---{},{}", Thread.currentThread().getName(),temp++);
        }
        Thread.currentThread().sleep(10000L);
        log.info("over.....");
    }

    @Test
    public void testredisProxyCglib() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 100; i++) {
            jedisJdkProxy.getInstance().set("testjedis121:" + i, i+"");
        }
        for (int i = 0; i < 100; i++) {
            Long delS= jedisJdkProxy.getInstance().del("testjedis121:" + i);
            log.info("del:"+delS);
        }
        Thread.currentThread().sleep(10000L);
        log.info("over.....");
    }

}


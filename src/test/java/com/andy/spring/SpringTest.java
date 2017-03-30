package com.andy.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ContextConfiguration(locations = "classpath:/applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class SpringTest {

    @Autowired
    private Jedis redisProxy;
    @Autowired
    private Jedis redisSpringProxy;

    @Test
    public void testredisProxy() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {

                    log.info("before" + temp + "::{}", Thread.currentThread().getName());

                    redisProxy.zincrby("testjedis121:" + temp, 1.0, temp + "");

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
                    redisSpringProxy.zincrby("testjedis123:" + temp, 1.0, temp + "");
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
            final int temp = i;
            log.info("before" + temp + "---{}", Thread.currentThread().getName());
            //redisSpringProxy.del("testjedis123:" + temp, 1.0, temp + "");
            log.info("after" + temp + "---{}", Thread.currentThread().getName());
        }
        Thread.currentThread().sleep(10000L);
        log.info("over.....");
    }

    @Test
    public void testredisProxyCglib() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 100; i++) {
            redisProxy.set("testjedis121:" + i, i+"");
        }
        for (int i = 0; i < 100; i++) {
            Long delS=redisProxy.del("testjedis121:" + i);
            log.info("del:"+delS);
        }
        Thread.currentThread().sleep(10000L);
        log.info("over.....");
    }

}


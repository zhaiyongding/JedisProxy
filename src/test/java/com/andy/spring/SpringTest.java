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
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class SpringTest {

    @Autowired
    private Jedis promotionRedisProxy;

    @Test
    public void test111() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            final int temp = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    //jedisJdkProxy.getJedisJdkProxyCommands().decr("testjedis:1");
                    log.info("before" + temp + "::{}", Thread.currentThread().getName());
                    //promotionRedisProxy.zincrby("testjedis:"+temp,1.0,temp+"");
                    promotionRedisProxy.zincrby("testjedis121:"+temp,1.0,temp+"");
                    log.info("after" + temp + "::{}", Thread.currentThread().getName());

                }
            });

        }
        Thread.currentThread().sleep(10000L);
        log.info("over.....");
    }
}


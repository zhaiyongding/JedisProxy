package com.andy.component;

import com.andy.jedis.JedisCglibProxy;
import com.andy.jedis.JedisJdkProxy;
import com.andy.jedis.JedisSpringAop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis and  enhance
 * Created by zhaiyongding on 2016/10/18.
 */
@Configuration
public class JedisConfig {

    public final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private Integer port = 6379;


    @Value("${redis.pool.maxTotal}")
    private Integer maxTotal=10;//自动注入
    @Value("${redis.pool.maxIdle}")
    private Integer maxIdle=12;
    @Value("${redis.pool.maxWait}")
    private Integer maxWait=2000;


    @Value("${redis.host}")
    private String redisHost;


    @Value("${redis.db}")
    private Integer redisdb;

    @Value("${redis.timeout}")
    private Integer redisTimeOut;


    @Value("${redis.auth}")
    private String redisauth;



    @Bean(name ="jedisPoolConfig")
    public JedisPoolConfig getJedisPollConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWait);
        return jedisPoolConfig;
    }


    @Bean(name ="jedisJdkProxy")
    public JedisJdkProxy redisProxy() {
        JedisPool jedisPool = new JedisPool(getJedisPollConfig(), redisHost, port, redisTimeOut, redisauth, redisdb);
        JedisJdkProxy jedisJdkProxy = new JedisJdkProxy(jedisPool);
        return jedisJdkProxy;
    }
    @Bean(name ="jedisSpringProxy")
    public JedisSpringAop jedisSpringProxy() {
        JedisPool jedisPool = new JedisPool(getJedisPollConfig(), redisHost, port, redisTimeOut, redisauth, redisdb);
        JedisSpringAop jedisSpringProxy = new JedisSpringAop(jedisPool);
        return jedisSpringProxy;
    }

}

# JedisProxy
由于jedis连接池使用完成必须手动返还或关闭,带来大量重复代码
目前解决方案主要是创建RedisUtils,集中管理redis操作命令.

本文提出代理的解决方案.再redis命令完成后的切面完成连接返还到连接池
1,JedisJdkProxy JDK代理方案
2,JedisCglibProxy Cglib代理方案

接下来会提供SpringAOP 解决方案,

本开源代码暂没有充分的测试用例,以及压力测试,后期会提供测试用例,测试报告


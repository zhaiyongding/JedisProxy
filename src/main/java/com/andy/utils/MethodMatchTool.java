package com.andy.utils;

import redis.clients.jedis.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wy-zyd on 17-4-1.
 */
public class MethodMatchTool {

    private static ConcurrentHashMap<String, HashSet<String>> methodCacheJdk = new ConcurrentHashMap<String, HashSet<String>>();

    public static HashSet<String> methodCacheCglib;//一次性初始化

    public static Boolean matchMethod(Method method, Class clazz) {
        //增强cache
        HashSet<String> methodSet = methodCacheJdk.get(clazz.getName());
        if (methodSet != null) {
            methodSet.contains(method.getName());
        } else {
            methodSet = new HashSet<String>();
            methodCacheJdk.put(clazz.getName(), methodSet);
        }
        for (Method method1 : Arrays.asList(clazz.getDeclaredMethods())) {
            if (method1.getName().equals(method.getName())) {
                //加入cache
                methodSet.add(method.getName());
                return true;
            }
        }
        return false;
    }

    static {
        methodCacheCglib = new HashSet<String>(256);
        ArrayList<Method> allMethodList = new ArrayList<Method>(256);
        Collections.addAll(allMethodList, JedisCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList, MultiKeyCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList, AdvancedJedisCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList, BasicCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList, ClusterCommands.class.getDeclaredMethods());
        Collections.addAll(allMethodList, SentinelCommands.class.getDeclaredMethods());
        for (Method methodInner : allMethodList) {
            //加入cache
            methodCacheCglib.add(methodInner.getName());
        }
    }
}

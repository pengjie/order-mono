package com.huinong.truffle.payment.order.mono.util;

import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huinong.framework.autoconfigure.jackson.HnJson;

/**
 * 返回结果解析器
 * @author zhou 2016年9月6日
 * @since 1.0
 */
@Component
public class RespResultParser {
    
    @Autowired
    private static HnJson hnJson;
    
    /**
     * 解析返回结果成单对象
     * @param result
     */
    public static <T> T parse2Obj(String result, Class<? extends T> clazz){
        return hnJson.str2obj(result, clazz);
    }
    
    /**
     * 解析返回结果成单对象
     * @param result
     */
  /*  public static <T> T parse2Obj(String result, TypeToken<T> typeToken){
        return hnJson.str2obj(result, new TypeReference<T>() {}) ;
    }*/
    
    /**
     * 将结果转换成分页对象
     * @param result 请求返回结果
     * @param clazz 数据类
     * @return
     */
    public static <T> RespPageResult4List<T> parse2ObjAsPageAsList(String result ,Class<? extends T> clazz){
        RespPageResult4List<T> pageResult = new RespPageResult4List<T>();
        pageResult = hnJson.str2obj(result, new TypeReference<RespPageResult4List<T>>() {}) ;
        return pageResult ;
    }
    
    /**
     * 将结果转换成分页对象
     * @param result 请求返回结果
     * @param clazz 数据类
     * @return
    @SuppressWarnings("unchecked")
    public static <T> RespPageResult4List<T> parse2ObjAsPageAsList(String result , Type type){
        return (RespPageResult4List<T>)gson.fromJson(result, type);
    }*/
    
    /**
     * 将结果转换成分页对象
     * @param result 请求返回结果
     * @param clazz 数据类
     * @return
     */
    public static <T> RespPageResult4Obj<T> parse2ObjAsPageAsObj(String result ,Class<? extends T> clazz){
        RespPageResult4Obj<T> pageResult = new RespPageResult4Obj<T>();
        pageResult =  hnJson.str2obj(result, new TypeReference<RespPageResult4Obj<T>>() {
        });
        return pageResult ;
    }
    
    /**
     * 将结果转换成对象
     * @param result
     * @param type
     * @return
     */
    public static <T> RespPageResult4Obj<T> parse2ObjAsPageAsObj(String result ,Type type){
        return  hnJson.str2obj(result, new TypeReference<RespPageResult4Obj<T>>() {
        });
        
    }
    
    /**
     * 将结果转换成分页对象
     * @param result 请求返回结果
     * @param clazz 数据类
     * @return
     */
    /*public static <T> T parse2ObjAsPage(String result ,TypeToken<T> typeToken){
        return   gson.fromJson(result, typeToken.getType());
    }*/
     
}

package com.huinong.truffle.payment.order.mono.util;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 * 返回结果解析器
 * @author zhou 2016年9月6日
 * @since 1.0
 */
public class RespResultParser {
	
	/*private static GsonBuilder builder = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }
    });
	
	private static Gson gson = builder.serializeNulls().create();*/

	
	private static Gson gson = new GsonBuilder().serializeNulls()
			.setDateFormat("yyyy-MM-dd HH:mm:ss").create();  
	
	/**
	 * 解析返回结果成单对象
	 * @param result
	 */
	public static <T> T parse2Obj(String result, Class<? extends T> clazz){
		return gson.fromJson(result, clazz);
	}
	
	/**
     * 解析返回结果成单对象
     * @param result
     */
    public static <T> T parse2Obj(String result, TypeToken<T> typeToken){
        return gson.fromJson(result, typeToken.getType());
    }
	
	/**
	 * 将结果转换成分页对象
	 * @param result 请求返回结果
	 * @param clazz 数据类
	 * @return
	 */
	public static <T> RespPageResult4List<T> parse2ObjAsPageAsList(String result ,Class<? extends T> clazz){
		RespPageResult4List<T> pageResult = new RespPageResult4List<T>();
		pageResult =  gson.fromJson(result, new TypeToken<RespPageResult4List<T>>(){}.getType());
		return pageResult ;
	}
	
	/**
	 * 将结果转换成分页对象
	 * @param result 请求返回结果
	 * @param clazz 数据类
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> RespPageResult4List<T> parse2ObjAsPageAsList(String result , Type type){
		return (RespPageResult4List<T>)gson.fromJson(result, type);
	}
	
	/**
	 * 将结果转换成分页对象
	 * @param result 请求返回结果
	 * @param clazz 数据类
	 * @return
	 */
	public static <T> RespPageResult4Obj<T> parse2ObjAsPageAsObj(String result ,Class<? extends T> clazz){
		RespPageResult4Obj<T> pageResult = new RespPageResult4Obj<T>();
		pageResult =  gson.fromJson(result, new TypeToken<RespPageResult4Obj<T>>(){}.getType());
		return pageResult ;
	}
	
	/**
	 * 将结果转换成对象
	 * @param result
	 * @param type
	 * @return
	 */
	public static <T> RespPageResult4Obj<T> parse2ObjAsPageAsObj(String result ,Type type){
		RespPageResult4Obj<T> pageResult = new RespPageResult4Obj<T>();
		pageResult =  gson.fromJson(result, type);
		return pageResult ;
	}
	
	/**
	 * 将结果转换成分页对象
	 * @param result 请求返回结果
	 * @param clazz 数据类
	 * @return
	 */
	public static <T> T parse2ObjAsPage(String result ,TypeToken<T> typeToken){
		return   gson.fromJson(result, typeToken.getType());
	}
	 
}

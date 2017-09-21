/**create by liuhua at 2016年5月23日 下午1:43:07**/
package com.huinong.truffle.payment.order.mono.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.huinong.framework.autoconfigure.jackson.HnJson;

/**
 * 简化取参数的过程
 * @author liuhua
 *
 */
@Component
public class ParamHandler {

    private Map<String, Object> map;
    
    @Autowired
    private HnJson hnJson ;
    
    /**
     * 把已经封装在map里的参数 直接赋值过来。
     * @param map
     */
    public ParamHandler(Map<String, Object> map){
        this.map = map;
    }
    
    /**
     * 将一个bean 转换成map
     * @param object
     */
    public ParamHandler(Object object){
      String strjson = hnJson.obj2string(object);
      this.map = hnJson.str2obj(strjson, new TypeReference<Map<String, Object>>() {});
    }
    
    /**
     * 讲 从客户端传递过来的参数 找到，并封装在map里。
     * @param request
     */
    public ParamHandler(HttpServletRequest request){
        this.map = new HashMap<String, Object>();
        Enumeration<String> names = request.getParameterNames();
        while(names.hasMoreElements()){
            String name = names.nextElement();
            String value = request.getParameter(name);
            if (null == value || value.length() == 0) {
                continue;
            }
            map.put(name, value);
        }
        if (map.isEmpty()) {
            try{
                ServletInputStream inputStream = request.getInputStream();
                byte[] b = new byte[1024];
                int len = 0;
                StringBuffer temp = new StringBuffer();
                while((len = inputStream.read(b)) != -1){
                    temp.append(new String(b, 0, len));
                }
                if (temp.length() > 0) {
                  map = hnJson.str2obj(temp.toString(), new TypeReference<Map<String,Object>>() {});
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public Double getDouble(String name){
        Object object = getObject(name);
        if (null == object) {
            return null;
        }
        try{
            return Double.parseDouble(object.toString());
        }catch(Exception e){
            
        }
        return null;
    }
    
    public Long getLong(String name){
        Object object = getObject(name);
        if (null == object) {
            return null;
        }
        try{
            return Long.parseLong(object.toString());
        }catch(Exception e){
            
        }
        return null;
    }
    
    public Integer getInteger(String name){
        Object object = getObject(name);
        if (null == object) {
            return null;
        }
        try{
            return Integer.parseInt(object.toString());
        }catch(Exception e){
            
        }
        return null;
    }
    
    public Boolean getBoolean(String name){
        Object object = getObject(name);
        try{
            return Boolean.parseBoolean(object.toString());
        }catch(Exception e){
            
        }
        return null;
    }
    
    public String getString(String name){
        Object object = getObject(name);
        if (null == object) {
            return null;
        }
        return object.toString().trim();
    }
    
    public Object getObject(String name){
        return map.get(name);
    }

    /**
     * 可以把map里的参数 封装成希望的bean
     * @author liuhua
     *
     * @param classOfT
     * @return
     */
    public <T> T getBean(Class<T> classOfT) {
      String jsonStr = hnJson.obj2string(map);
      return hnJson.str2obj(jsonStr, classOfT);
    }
    
    public Map<String, Object> getMap() {
        return map;
    }
}

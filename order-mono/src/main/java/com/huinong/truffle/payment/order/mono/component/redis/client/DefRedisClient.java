package com.huinong.truffle.payment.order.mono.component.redis.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;


@Component
public class DefRedisClient {
	
    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    
    public  DefRedisClient(RedisTemplate<String,Object> redisTemplate) {
       this.redisTemplate = redisTemplate;
    }
    
    private static final int DEFAULT_EXPIRE_TIME = 60 * 60 * 24 * 30 ;
    
    
    public void set(String key, Object value){
        redisTemplate.opsForValue().set(key, value, DEFAULT_EXPIRE_TIME);
    }
    
//  @SuppressWarnings("unchecked")
//  public <T>T get(String key){
//      return (T)redisTemplate.opsForValue().get(key);
//  }
    
       
    public boolean set(final String key, final Object value,final int expireTime) {  
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
            @SuppressWarnings("unchecked")
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> kSeria = (RedisSerializer<String>) redisTemplate.getKeySerializer();  
                RedisSerializer vSeria = redisTemplate.getValueSerializer();  
                byte[] keys = kSeria.serialize(key);
                connection.set(keys, vSeria.serialize(value));  
                connection.expire(keys, new Long(expireTime));
                return true;  
            }  
        });  
        return result;  
    }  
    
    public <T> T get(final String key){  
        Object result = redisTemplate.execute(new RedisCallback<Object>() {  
            public Object doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                byte[] value =  connection.get(serializer.serialize(key)); 
                RedisSerializer<Object> vSeria = (RedisSerializer<Object>) redisTemplate.getValueSerializer();
               return  vSeria.deserialize(value);  
            }  
        });  
        return (T)result;  
    }
  
      
    public boolean expire(final String key, final long expire) {
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
            @SuppressWarnings("unchecked")
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> kSeria = (RedisSerializer<String>) redisTemplate.getKeySerializer();  
                byte[] keys = kSeria.serialize(key);
                connection.expire(keys, expire);
                return true;  
            }  
        });  
        return result;  
    }  
   
      
    public long lpush(final String key, final Object obj) {  
        long result = redisTemplate.execute(new RedisCallback<Long>() {  
            @SuppressWarnings("unchecked")
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> keySeria = redisTemplate.getStringSerializer();  
                RedisSerializer serializer = redisTemplate.getValueSerializer();  
                long count = connection.lPush(keySeria.serialize(key), serializer.serialize(obj));  
                return count;  
            }  
        });  
        return result;  
    }  
  
      
    public long rpush(final String key, final Object obj) {  
        long result = redisTemplate.execute(new RedisCallback<Long>() {  
            @SuppressWarnings("unchecked")
            public Long doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> keySeria = redisTemplate.getStringSerializer();  
                RedisSerializer serializer = redisTemplate.getValueSerializer();  
                long count = connection.rPush(keySeria.serialize(key), serializer.serialize(obj));  
                return count;  
            }  
        });  
        return result;  
    }  
  
      
    public Object lpop(final String key) {  
        Object result = redisTemplate.execute(new RedisCallback<Object>() {  
            public Object doInRedis(RedisConnection connection) throws DataAccessException {  
                RedisSerializer<String> keySeria = redisTemplate.getStringSerializer();  
                RedisSerializer serializer = redisTemplate.getValueSerializer();  
                byte[] res =  connection.lPop(keySeria.serialize(key));  
                return serializer.deserialize(res);  
            }  
        });  
        return result;  
    } 
    
    public boolean exists(final String key) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();  
                return connection.exists(serializer.serialize(key));
            }
        });
    }
    
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}

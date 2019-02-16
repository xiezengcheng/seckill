package org.seckill.dao;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 缓存
 */
public class RedisDAO {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private JedisPool jedisPool;

    public RedisDAO(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public Seckill getSeckill(long seckillId){

        //redis缓存逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try{
                String key = "seckill:"+seckillId;
                //实现序列化
                //get 的 是一个字节数组 反序列化
                //自定义序列化 protostuff序列化
                byte[] bytes = jedis.get(key.getBytes());
                if(bytes != null){
                    Seckill seckill = schema.newMessage();
                    //反序列化
                    ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                    return seckill;
                }

            }finally {
                jedis.close();
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    public String putSeckill(Seckill seckill){

        // set  seckill->byte[]
        try {

            Jedis jedis = jedisPool.getResource();
            try{

                String key = "seckill:"+seckill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

                //缓存超时
                int tiemout = 60*60;
                String result = jedis.setex(key.getBytes(),tiemout,bytes);
                return result;
            }finally {
                jedis.close();
            }

        }catch (Exception e){
            logger.error(e.getMessage(),e);

        }

        return null;
    }



}

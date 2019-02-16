package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})

public class RedisDAOTest {

    @Autowired
    private SeckillDAO seckillDAO;

    @Autowired
    private RedisDAO redisDAO;

    @Test
    public void getSeckill() {
        long id = 1001;
        Seckill seckill = redisDAO.getSeckill(id);
        if(seckill == null){
            seckill = seckillDAO.queryById(id);
            if(seckill != null){
                String result = redisDAO.putSeckill(seckill);
                System.out.println(result);
                seckill = redisDAO.getSeckill(id);
                System.out.println(seckill);
            }
        }

    }

    @Test
    public void putSeckill() {

    }
}
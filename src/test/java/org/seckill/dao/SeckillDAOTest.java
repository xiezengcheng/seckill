package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * 配置spring和junit整合
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDAOTest {
    //注入DAO实现类依赖
    @Autowired
    private SeckillDAO seckillDAO;

    @Test
    public void testReduceNumber() {

        Date killTime = new Date();
        int updateeCount = seckillDAO.reduceNumber(1000L,killTime);
        System.out.println(updateeCount);

    }

    @Test
    public void testQueryById() {
        long id = 1000;
        Seckill seckill = seckillDAO.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);

    }

    @Test
    public void testQueryAll() {

        List<Seckill> list = seckillDAO.queryAll(0,100);
        for (Seckill seckill: list
             ) {
            System.out.println(seckill);
        }


    }
}

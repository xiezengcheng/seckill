package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;


    @Test
    public void getSeckillList() {

        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);

    }

    @Test
    public void getSeckillById() {
        Seckill seckill = seckillService.getSeckillById(1000);
        logger.info("seckill={}",seckill);

    }

    @Test
    public void exportSeckillUrl() {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);


    }

    @Test
    public void executeSeckill() {
        long id = 1000;
        long phone= 13424234422L;
        String md5 = "efecb4d944c90ebf42776ed65a0dbed4";
        SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
        logger.info("result={}",execution);
    }
//
//    @Test
//    public void executeSeckillProcedure(){
//        long seckillId = 1000L;
//        long phone = 14325432456L;
//        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
//        if(exposer.isExposed()){
//            String md5 = exposer.getMd5();
//            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
//            logger.info(execution.getStateInfo());
//        }
//
//    }

}
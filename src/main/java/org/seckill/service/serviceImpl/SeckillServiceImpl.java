package org.seckill.service.serviceImpl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.RedisDAO;
import org.seckill.dao.SeckillDAO;
import org.seckill.dao.SuccessKilledDAO;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillColseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisDAO redisDAO;

    @Autowired
    private SeckillDAO seckillDAO;

    @Autowired
    private SuccessKilledDAO successKilledDAO;

    //盐值
    private final String slat = "akdjtoiakgj#$%%ak45u09uirjg934374t8*$*%";

    @Override
    public List<Seckill> getSeckillList() {
        //这里不做分页 简单测试 4个
        return seckillDAO.queryAll(0, 4);
    }

    @Override
    public Seckill getSeckillById(long seckillId) {
        return seckillDAO.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
       //优化点：缓存优化
        /**
         * get from cache if null
         * get db
         * put cache
         * 单独抽取 RedisDAO
         */
        Seckill seckill = redisDAO.getSeckill(seckillId);
        if(seckill == null){
            seckill = seckillDAO.queryById(seckillId);
            if(seckill != null){
                //放入redis
                String result = redisDAO.putSeckill(seckill);
            }else{
                return new Exposer(false,seckillId);
            }
        }

//        Seckill seckill = seckillDAO.queryById(seckillId);
//        if (seckill == null) {
//            return new Exposer(false, seckillId);
//        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //md5加密 不可逆
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    //生成MD5
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解声明事务优点
     * 明确标注事务方法 清晰
     * 保证事务方法的执行时间尽可能的短，不要穿插其它的网络操作或者剥离到事务方法外部
     * 不是所有的方法都需要事务只读操作 或只有一条修改操作
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillColseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑 减库存+ 记录购买行为
        Date nowTime = new Date();
        try {
            //成功 记录购买行为
            int insertCount = successKilledDAO.insertSuccessKilled(seckillId, userPhone);
            //唯一：seckillId,userPhone
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                int updateCount = seckillDAO.reduceNumber(seckillId, nowTime);
                //减库存 热点商品竞争
                if (updateCount <= 0) {
                    //没有更新到记录
                    throw new SeckillColseException("seckill is closed");
                } else {
                    //秒杀成功
                    SuccessKilled successKilled = successKilledDAO.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
                }
             }
        }catch (SeckillColseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常 转化为运行期异常 spring只接受运行时异常
            throw new SeckillException("seckill innwe error:"+e.getMessage());
        }
    }

    /**
     * 使用存储过程优化
     */
//    @Override
//    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
//       if(md5 == null || !md5.equals(getMD5(seckillId))){
//           return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRIRE);
//       }
//        Date killTime = new Date();
//       Map<String,Object> map = new HashMap<String,Object>();
//       map.put("seckillId",seckillId);
//       map.put("phone",userPhone);
//       map.put("killTime",killTime);
//       map.put("result",null);
//
//       //执行存储过程后result被赋值
//        try{
//             seckillDAO.killByProcedure(map);
//             //获取result
//            int result = MapUtils.getInteger(map,"result",-2);
//            if(result == 1){
//                SuccessKilled sk = successKilledDAO.queryByIdWithSeckill(seckillId, userPhone);
//                return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS);
//            }else{
//                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
//            }
//        }catch (Exception e){
//            logger.error(e.getMessage(),e);
//            return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
//        }
//
//    }
}

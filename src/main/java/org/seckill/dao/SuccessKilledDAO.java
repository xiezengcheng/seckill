package org.seckill.dao;

import com.sun.net.httpserver.Authenticator;
import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;
import org.springframework.core.annotation.Order;

/*
* 秒杀成功用户DAO层接口
* */
public interface SuccessKilledDAO {

    /**
     * 插入购买明细,可过滤重复 id和电话相同则重复秒杀 不允许
     * @param seckillId
     * @param userPhone
     * @return 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId , @Param("userPhone") long userPhone);

    /**
     * 根据id查询SuccessKilled并携带秒杀产品对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId , @Param("userPhone") long userPhone);

}

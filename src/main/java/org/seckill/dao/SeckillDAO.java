package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 秒杀DAO层接口
 */

public interface SeckillDAO {

    /**
     *减库存
     * @param seckillId 库存的ID
     * @param killTime 减库存的时间
     * @return 影响的行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀对象
     * @param seckillId 商品ID
     * @return
     */

    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    //使用存储过程优化 秒杀
//    void killByProcedure(Map<String,Object> paramMap);

}

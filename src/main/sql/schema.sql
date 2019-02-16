-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE seckill;
-- 使用数据库
use seckill;
-- 创建秒杀库存表
CREATE TABLE seckill(
seckill_id bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
nam varchar(120) NOT NULL COMMENT '商品名称',
number int NOT NULL COMMENT '库存数量',
start_time timestamp NOT NULL COMMENT '秒杀开启时间',
end_time timestamp NOT NULL COMMENT '秒杀结束时间',
create_time timestamp  NOT NULL COMMENT '创建时间',
PRIMARY KEY (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

--初始化数据
insert into seckill(name,number,start_time,end_time,create_time)
values
  ('1000元秒杀IPHONE6',100,'2019-3-01 00:00:00','2019-3-02 00:00:00','2019-2-27 00:00:00'),
  ('1元秒杀小米4',200,'2019-3-01 00:00:00','2019-3-02 00:00:00','2019-2-27 00:00:00'),
  ('100元秒杀IPHONE8',300,'2019-3-01 00:00:00','2019-3-02 00:00:00','2019-2-27 00:00:00'),
  ('3000元秒杀华为P9',400,'2019-3-01 00:00:00','2019-3-02 00:00:00','2019-2-27 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息
create  table success_killed(
seckill_id bigint NOT NULL COMMENT '秒杀商品id'，
user_phone bigint NOT NULL COMMENT '用户手机号',
state tinyint NOT NULL DEFAULT -1 COMMENT '状态标识 -1 无效 0成功 1 已付 2未付款',
create_time timestamp NOT NULL COMMENT '创建时间',
PRIMARY KEY(seckill_id,user_phone),/* 联合主键*/
key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- 连接数据库的控制台




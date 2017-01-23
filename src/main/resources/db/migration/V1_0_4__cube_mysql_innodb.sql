/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : webside

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2017-1-23
*/

DROP TABLE IF EXISTS `webside`.`tb_act_leavebill`;
CREATE TABLE  `webside`.`tb_act_leavebill` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `days` int(11) unsigned NOT NULL COMMENT '请假天数',
  `content` varchar(1000) COLLATE utf8_general_mysql500_ci NOT NULL COMMENT '请假内容',
  `leaveDate` datetime NOT NULL COMMENT '请假日期',
  `remark` varchar(1000) COLLATE utf8_general_mysql500_ci NOT NULL COMMENT '请假备注',
  `state` int(3) unsigned NOT NULL DEFAULT '0' COMMENT '请假状态：0初始录入,1.开始审批,2为审批完成',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='请假单业务表';

DROP TABLE IF EXISTS `webside`.`tb_act_leavebill_user`;
CREATE TABLE  `webside`.`tb_act_leavebill_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `b_id` int(11) unsigned NOT NULL COMMENT '请假单id',
  `u_id` int(11) unsigned NOT NULL COMMENT '请假人id',
  `t_create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='人员请假单对应表';

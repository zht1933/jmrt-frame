/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : webside

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2017年2月21日
*/

DROP TABLE IF EXISTS `webside`.`tb_jz_base`;
CREATE TABLE  `webside`.`tb_jz_base` (
  `ryjzxxb_id` varchar(32) COLLATE utf8_general_mysql500_ci NOT NULL COMMENT '主键ID',
  `ryjzxxb_xh` varchar(32) COLLATE utf8_general_mysql500_ci NOT NULL COMMENT '序号',
  `ryjzxxb_gzdw` varchar(128) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '工作单位',
  `ryjzxxb_cj` varchar(128) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '车间',
  `ryjzxxb_xm` varchar(32) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '姓名',
  `ryjzxxb_xl` varchar(16) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '学历',
  `ryjzxxb_xgw` varchar(128) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '现岗位',
  `ryjzxxb_jsjcsjrq` datetime DEFAULT NULL COMMENT '晋升机车司机日期',
  `ryjzxxb_jszlb` varchar(32) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '驾驶证类别',
  `ryjzxxb_sfzh` varchar(18) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '身份证号',
  `ryjzxxb_jszbh` varchar(16) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '驾驶证编号',
  `ryjzxxb_fzrq` datetime DEFAULT NULL COMMENT '发证日期',
  `ryjzxxb_dqhfxzrq` datetime DEFAULT NULL COMMENT '到期换发新证日期',
  `cjrq` datetime DEFAULT NULL COMMENT '创建日期',
  `cjrid` varchar(16) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '创建人id',
  `xgrq` datetime DEFAULT NULL COMMENT '修改日期',
  `xgrid` varchar(16) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '修改人id',
  `ryjzxxb_bz` text COLLATE utf8_general_mysql500_ci COMMENT '备注',
  PRIMARY KEY (`ryjzxxb_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='乘务员驾证基础信息表';
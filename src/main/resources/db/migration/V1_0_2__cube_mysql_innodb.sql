/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : webside

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2017-1-3
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rp_report
-- ----------------------------
DROP TABLE IF EXISTS `rp_report`;
CREATE TABLE  `rp_report` (
  `ID` varchar(64) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT 'id',
  `MDX` text COLLATE utf8_general_mysql500_ci COMMENT '查询语句',
  `TITLE` varchar(200) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '标题',
  `USERID` varchar(100) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '操作用户',
  `SUMROW` int(11) DEFAULT NULL COMMENT '行合计',
  `SUMCOL` int(11) DEFAULT NULL COMMENT '列合计',
  `MODULESEPARATE` varchar(64) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '模块',
  `FLAG` varchar(3) COLLATE utf8_general_mysql500_ci DEFAULT NULL COMMENT '标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_mysql500_ci COMMENT='多维度分析报表';

DROP VIEW IF EXISTS `v_rp_report`;
CREATE OR REPLACE VIEW `v_rp_report` AS select `rp_report`.`ID` AS `id`,`rp_report`.`TITLE` AS `name`,`rp_report`.`SUMCOL` AS `sumcol`,`rp_report`.`SUMROW` AS `sumrow`,`rp_report`.`MODULESEPARATE` AS `moduleseparate`,`rp_report`.`MDX` AS `mdx`,`rp_report`.`USERID` AS `userid`,'0' AS `parentid`,'.0.1.' AS `seq`,`rp_report`.`FLAG` AS `flag`,'true' AS `leaf` from `rp_report` order by `rp_report`.`ID`;


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

DROP TABLE IF EXISTS `tb_org`;
CREATE TABLE `tb_org` (
  `o_id` int(16) NOT NULL AUTO_INCREMENT COMMENT '组织机构id',
  `o_name` varchar(64) NOT NULL COMMENT '组织机构名称',
  `o_pid` int(16) DEFAULT NULL COMMENT '父机构id',
  `o_key` varchar(32) NOT NULL COMMENT '组织机构key',
  `o_status` int(1) DEFAULT '0' COMMENT '组织机构状态,0：正常；1：删除',
  `o_description` varchar(128) DEFAULT NULL COMMENT '组织机构描述',
  `o_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `o_update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`o_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='组织机构表';

DROP TABLE IF EXISTS `tb_org_user`;
CREATE TABLE `tb_org_user` (
  `id` int(16) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `o_id` int(16) DEFAULT NULL COMMENT '组织机构id',
  `u_id` int(16) DEFAULT NULL COMMENT '人员id',
  `t_create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='人员与组织机构关系表';

alter table `tb_user`
Add column `u_mgr_id` int(16) not null;

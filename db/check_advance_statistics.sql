/*
 Navicat Premium Data Transfer

 Source Server         : zxmember_new
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : 192.168.31.250:3306
 Source Schema         : zxmember_new

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 11/10/2024 16:50:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for check_advance_statistics
-- ----------------------------
DROP TABLE IF EXISTS `check_advance_statistics`;
CREATE TABLE `check_advance_statistics`  (
  `id` int(0) NOT NULL COMMENT '序号',
  `customer_id` int(0) NULL DEFAULT NULL COMMENT '客户id',
  `customer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '客户名字',
  `project_id` int(0) NULL DEFAULT NULL COMMENT '项目id',
  `project_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名称',
  `channel_id` int(0) NULL DEFAULT NULL COMMENT '渠道id',
  `channel_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '渠道名称',
  `agent_id` int(0) NULL DEFAULT NULL COMMENT '代理商id',
  `agent_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '代理商名称',
  `business` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商务',
  `ae` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'AE',
  `contract_expiry_time` varchar(0) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '合同到期时间',
  `repayment_policy_period` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回款政策账期',
  `advance_month` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '垫款月份',
  `advance_amount` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '垫款金额',
  `contract_payment_time` varchar(0) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '按照合同约定回款时间',
  `actual_payment_date` datetime(0) NULL DEFAULT NULL COMMENT '实际回款日期',
  `payment_recovery_stage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回款阶段',
  `payment_should_month` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应该回款月份',
  `estimated_payment_time` varchar(0) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '预计回款时间',
  `our_invoicing_entity` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '我方开票主体',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `overdue_reason` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逾期原因',
  `payment_status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '回款状态 1.未逾期 2.已逾期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '垫款统计表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

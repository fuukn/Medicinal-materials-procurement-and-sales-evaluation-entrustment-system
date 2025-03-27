package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 评估委托
 * 
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-03-08 23:05:08
 */
@Data
@TableName("evaluation_delegation")
public class EvaluationDelegationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 用户
	 */
	private Long userId;
	/**
	 * 药材
	 */
	private String herbalMedicine;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 委托状态：1.待处理 2.等待中 3.进行中 4.已结束
	 */
	private Integer status;
	/**
	 * 联系电话
	 */
	private String phone;
	/**
	 * 发布时间
	 */
	private Date createDate;
	/**
	 * 规模：1.小型药房与个体户 2.中小型企业 3.大型企业
	 */
	private Integer scale;
	/**
	 * 其他细节
	 */
	private String details;
	/**
	 * 评估用户id
	 */
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private Long appraiserId;
	/**
	 * 报告链接
	 */
	private String report;

}

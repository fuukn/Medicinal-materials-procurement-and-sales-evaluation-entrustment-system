package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-03-05 10:48:14
 */
@Data
@TableName("appraiser")
public class AppraiserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 
	 */
	private Long userId;
	/**
	 * 毕业院校
	 */
	private String school;
	/**
	 *个人介绍
	 */
	private String introduction;
	private String specialty;       // 涉及领域
	private String workYears;       // 工作年限
	private String rating;          // 评级
	private String photo;
	private String certificate;
	private String reportList;
}

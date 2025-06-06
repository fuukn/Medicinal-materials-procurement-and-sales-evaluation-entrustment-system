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
 * @date 2025-05-07 10:17:04
 */
@Data
@TableName("supply")
public class SupplyEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private String supdemName;
	/**
	 * 
	 */
	private String specification;
	/**
	 * 
	 */
	private String quantity;
	/**
	 * 
	 */
	private String inventoryPlace;
	/**
	 * 
	 */
	private String quality;
	/**
	 * 
	 */
	private String phone;
	/**
	 * 
	 */
	private String contactPerson;
	/**
	 * 
	 */
	private String time;

}

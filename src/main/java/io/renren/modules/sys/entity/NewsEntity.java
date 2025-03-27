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
 * @date 2025-02-15 09:47:41
 */
@Data
@TableName("news")
public class NewsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	private Integer id;
	/**
	 * 
	 */
	private String title;
	/**
	 * 
	 */
	private Date date;
	/**
	 * 
	 */
	private String content;

}

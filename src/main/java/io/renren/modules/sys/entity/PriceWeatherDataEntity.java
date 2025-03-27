package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-03-06 21:17:40
 */
@Data
@TableName("price_weather_data")
public class PriceWeatherDataEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 日期
	 */
	@TableId
	private Date createDate;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 最高温度
	 */
	private Float lowestTemperature;
	/**
	 * 最低温度
	 */
	private Float highestTemperature;
	/**
	 * 天气
	 */
	private String weather;
	/**
	 * 风向风力
	 */
	private String wind;
	/**
	 * 空气质量
	 */
	private String airQuality;
	/**
	 * 天气(简)
	 */
	private String simplifiedWeather;
	/**
	 * 平均温度
	 */
	private Float averageTemperature;

}

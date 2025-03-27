package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.PriceData;
import io.renren.modules.sys.entity.PriceWeatherDataEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-03-06 21:17:40
 */
public interface PriceWeatherDataService extends IService<PriceWeatherDataEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void exportToExcel(HttpServletResponse response);
    List<PriceWeatherDataEntity> getAllData();
}


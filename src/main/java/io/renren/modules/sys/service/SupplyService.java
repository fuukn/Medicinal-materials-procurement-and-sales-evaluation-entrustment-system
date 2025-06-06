package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.SupplyEntity;

import java.util.Map;

/**
 * 
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-05-07 10:17:04
 */
public interface SupplyService extends IService<SupplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


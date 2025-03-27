package io.renren.modules.sys.service;



import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.NewsEntity;

import java.util.Map;

/**
 * 
 *
 * @author gjz
 * @email 1826473923@qq.com
 * @date 2025-02-15 09:47:41
 */
public interface NewsService extends IService<NewsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}


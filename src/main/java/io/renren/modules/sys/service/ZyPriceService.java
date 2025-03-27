package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.sys.entity.PriceData;


import java.util.List;

public interface ZyPriceService extends IService<PriceData> {
    List<PriceData> selectAll();
}

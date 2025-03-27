package io.renren.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.sys.entity.DailyIndex;


import java.util.List;

public interface ZyDailyIndexService extends IService<DailyIndex> {
    List<DailyIndex> selectAllIndex();

}

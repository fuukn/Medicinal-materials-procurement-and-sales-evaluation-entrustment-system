package io.renren.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.sys.dao.ZyDailyIndexDao;
import io.renren.modules.sys.entity.DailyIndex;
import io.renren.modules.sys.service.ZyDailyIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ZyDailyIndexService")
public class ZyDailyIndexServiceImpl extends ServiceImpl<ZyDailyIndexDao, DailyIndex> implements ZyDailyIndexService {
    @Autowired
    private ZyDailyIndexDao zyDailyIndexDao;

    @Override
    public List<DailyIndex> selectAllIndex(){
        List<DailyIndex> datas = zyDailyIndexDao.selectAllIndex();
        return datas;
    }

}

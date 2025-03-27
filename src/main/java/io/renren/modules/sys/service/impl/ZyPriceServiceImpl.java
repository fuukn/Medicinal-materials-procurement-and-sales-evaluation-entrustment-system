package io.renren.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.modules.sys.dao.ZyPriceDao;
import io.renren.modules.sys.entity.PriceData;
import io.renren.modules.sys.service.ZyPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ZyPriceService")
public class ZyPriceServiceImpl extends ServiceImpl<ZyPriceDao, PriceData> implements ZyPriceService {
    @Autowired
    private ZyPriceDao zyPriceDao;

    @Override
    public List<PriceData> selectAll(){
        List<PriceData> prices = zyPriceDao.selectAll();
        return prices;
    }



}

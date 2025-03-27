package io.renren.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import io.renren.modules.sys.dao.NewsDao;
import io.renren.modules.sys.entity.NewsEntity;
import io.renren.modules.sys.service.NewsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;



@Service("newsService")
public class NewsServiceImpl extends ServiceImpl<NewsDao, NewsEntity> implements NewsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String startDate = (String) params.get("startDate");
        String endDate = (String) params.get("endDate");

        QueryWrapper<NewsEntity> queryWrapper = new QueryWrapper<>();

        // 如果 startDate 和 endDate 都不为空，则添加 between 条件
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            queryWrapper.between("date", startDate, endDate);
        }
        // 如果只有 startDate 不为空，则添加 >= 条件
        else if (StringUtils.isNotBlank(startDate)) {
            queryWrapper.ge("date", startDate);
        }
        // 如果只有 endDate 不为空，则添加 <= 条件
        else if (StringUtils.isNotBlank(endDate)) {
            queryWrapper.le("date", endDate);
        }

        IPage<NewsEntity> page = this.page(
                new Query<NewsEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

}
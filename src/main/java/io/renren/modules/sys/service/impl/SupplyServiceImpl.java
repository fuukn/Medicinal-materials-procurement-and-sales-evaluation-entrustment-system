package io.renren.modules.sys.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.sys.dao.SupplyDao;
import io.renren.modules.sys.entity.SupplyEntity;
import io.renren.modules.sys.service.SupplyService;


@Service("supplyService")
public class SupplyServiceImpl extends ServiceImpl<SupplyDao, SupplyEntity> implements SupplyService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 创建查询条件包装器
        QueryWrapper<SupplyEntity> queryWrapper = new QueryWrapper<>();

        // 添加按时间字段降序排序的条件
        // "time" 是您的数据库表中的列名
        queryWrapper.orderByDesc("time");

        // 执行分页查询
        IPage<SupplyEntity> page = this.page(
                new Query<SupplyEntity>().getPage(params), // 获取分页参数
                queryWrapper // 使用带有排序条件的包装器
        );

        // 返回分页结果
        return new PageUtils(page);
    }

}
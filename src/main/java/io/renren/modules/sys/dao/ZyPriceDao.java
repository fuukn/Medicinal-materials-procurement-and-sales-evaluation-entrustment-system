package io.renren.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.modules.sys.entity.PriceData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ZyPriceDao extends BaseMapper<PriceData> {
    List<PriceData> selectAll();


}

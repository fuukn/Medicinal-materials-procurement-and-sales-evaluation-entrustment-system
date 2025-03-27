package io.renren.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.renren.modules.sys.entity.DailyIndex;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ZyDailyIndexDao extends BaseMapper<DailyIndex> {
    List<DailyIndex> selectAllIndex();

}

package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("daily_index")
public class DailyIndex {
    @TableId
    private  int id;
    private  int data;
    private  String date;
}

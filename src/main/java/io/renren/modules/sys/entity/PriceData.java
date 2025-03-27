package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("price_data")
public class PriceData {
    @TableId
    private int pid;
    private int price;
    private String createDate;

}

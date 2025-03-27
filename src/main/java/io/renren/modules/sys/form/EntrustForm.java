package io.renren.modules.sys.form;

import lombok.Data;

@Data
public class EntrustForm {
    private String herbalMedicine; // 药材
    private String address;        // 地址
    private String phone;         // 联系电话
    private Integer scale;        // 规模
    private String details;       // 其他细节
}
package io.renren.modules.sys.form;

import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.UpdateGroup;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class AppraiserForm {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 毕业院校
     */
    private String school;
    /**
     *个人介绍
     */
    private String introduction;
    /**
     * 邮箱
     */
    @Email(message="邮箱格式不正确", groups = {AddGroup.class, UpdateGroup.class})
    private String email;
    /**
     * 手机号
     */
    private String mobile;
    private String specialty;       // 涉及领域
    private String workYears;       // 工作年限
    private String rating;          // 评级
    private Date registerDate;    // 注册日期

}

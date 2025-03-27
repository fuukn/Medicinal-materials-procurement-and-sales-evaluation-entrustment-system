package io.renren.modules.sys.form;

import lombok.Data;

@Data
public class RegisterForm {
    private Long role;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String captcha;
    private String uuid;
}

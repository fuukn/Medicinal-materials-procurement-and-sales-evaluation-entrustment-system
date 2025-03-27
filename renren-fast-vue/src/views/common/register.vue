<template>
  <div class="site-wrapper site-page--register">
    <div class="site-content__wrapper">
      <div class="site-content">
        <div class="register-main">
          <h3 class="register-title">用户注册</h3>
          <el-form :model="registerForm" :rules="registerRules" ref="registerForm" @keyup.enter.native="registerSubmit()" status-icon>
            <!-- 登录类型选择 -->
            <el-form-item prop="role">
              <el-select v-model="registerForm.role" placeholder="选择登录类型">
                <el-option label="数据管理员" value="0"></el-option>
                <el-option label="采购用户" value="1"></el-option>
                <el-option label="采购评估师" value="2"></el-option>
              </el-select>
            </el-form-item>
            <!-- 帐号输入框 -->
            <el-form-item prop="userName">
              <el-input v-model="registerForm.userName" placeholder="帐号"></el-input>
            </el-form-item>
            <!-- 密码输入框 -->
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="密码"></el-input>
            </el-form-item>
            <!-- 确认密码输入框 -->
            <el-form-item prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码"></el-input>
            </el-form-item>
            <!-- 邮箱输入框 -->
            <el-form-item prop="email">
              <el-input v-model="registerForm.email" placeholder="邮箱"></el-input>
            </el-form-item>
            <!-- 电话输入框 -->
            <el-form-item prop="phone">
              <el-input v-model="registerForm.phone" placeholder="电话"></el-input>
            </el-form-item>
            <!-- 注册按钮 -->
            <el-form-item>
              <el-button class="register-btn-submit" type="primary" @click="registerSubmit()">注册</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      registerForm: {
        role: '',
        userName: '',
        password: '',
        confirmPassword: '',
        email: '',
        phone: ''
      },
      registerRules: {
        role: [
          { required: true, message: '登录类型不能为空', trigger: 'blur' }
        ],
        userName: [
          { required: true, message: '帐号不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '确认密码不能为空', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ],
        email: [
          { required: true, message: '邮箱不能为空', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
        ],
        phone: [
          { required: true, message: '电话不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: ['blur', 'change'] }
        ]
      }
    };
  },
  methods: {
    // 提交注册表单
    registerSubmit() {
      this.$refs['registerForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl('/sys/register'),
            method: 'post',
            data: this.$http.adornData({
              role: this.registerForm.role,
              username: this.registerForm.userName,
              password: this.registerForm.password,
              email: this.registerForm.email,
              phone: this.registerForm.phone
            })
          })
          .then(({ data }) => {
            if (data && data.code === 0) {
              this.$message.success('注册成功！');
              this.$router.replace({ name: 'login' });
            } else {
              this.$message.error(data.msg || '注册失败，请稍后重试');
            }
          })
          .catch((error) => {
            console.error('注册请求出错:', error);
            this.$message.error('注册请求出错，请稍后重试');
          });
        }
      });
    },
    // 验证确认密码
    validateConfirmPassword(rule, value, callback) {
      if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'));
      } else {
        callback();
      }
    }
  }
};
</script>

<style lang="scss">
  /* 样式类似登录页面 */
</style>
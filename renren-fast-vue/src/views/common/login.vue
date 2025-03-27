<template>
  <div class="site-wrapper site-page--login">
    <div class="site-content__wrapper">
      <div class="site-content">
        <div class="brand-info">
          <h2 class="brand-info__text">中药材评估与委托平台</h2>
          <p class="brand-info__intro">智能分析药材价格评估，精准估值，让您有更优质的采购体验</p>
        </div>
        <div class="login-main">
          <h3 class="login-title">{{ loginType === '0' ? '管理员登录' : '采购用户登录' }}</h3>
          <!-- 登录类型选择 -->
          <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" status-icon>
            <el-form-item prop="loginType">
              <el-select v-model="loginType" placeholder="选择登录类型">
                <el-option label="数据管理员" value="0"></el-option>
                <el-option label="采购用户" value="1"></el-option>
                <el-option label="采购评估师" value="2"></el-option>
              </el-select>
            </el-form-item>
            <!-- 帐号输入框 -->
            <el-form-item prop="userName">
              <el-input v-model="dataForm.userName" placeholder="帐号"></el-input>
            </el-form-item>
            <!-- 密码输入框 -->
            <el-form-item prop="password">
              <el-input v-model="dataForm.password" type="password" placeholder="密码"></el-input>
            </el-form-item>
            <!-- 验证码输入框 -->
            <el-form-item prop="captcha">
              <el-row :gutter="20">
                <el-col :span="14">
                  <el-input v-model="dataForm.captcha" placeholder="验证码"></el-input>
                </el-col>
                <el-col :span="10" class="login-captcha">
                  <img :src="captchaPath" @click="getCaptcha()" alt="">
                </el-col>
              </el-row>
            </el-form-item>
            <!-- 登录按钮 -->
            <el-form-item>
              <el-button class="login-btn-submit" type="primary" @click="dataFormSubmit()">登录</el-button>
            </el-form-item>
            <!-- 注册按钮 -->
            <el-form-item>
              <el-button class="login-btn-register" type="default" @click="goToRegister">注册</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getUUID } from '@/utils';

export default {
  data() {
    return {
      loginType: '1',
      dataForm: {
        role: '',
        userName: '',
        password: '',
        uuid: '',
        captcha: ''
      },
      dataRule: { /* 规则保持不变 */ },
      captchaPath: ''
    };
  },
  created() {
    this.getCaptcha();
  },
  methods: {
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const loginUrl = this.loginType === '0' ? '/sys/login' : '/sys/login';
          this.$http({
            url: this.$http.adornUrl(loginUrl),
            method: 'post',
            data: this.$http.adornData({
              'role': this.loginType,
              'username': this.dataForm.userName,
              'password': this.dataForm.password,
              'uuid': this.dataForm.uuid,
              'captcha': this.dataForm.captcha
            })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$cookie.set('token', data.token);
              this.$router.replace({ name: 'home' });
              this.$nextTick(() => {
                window.dispatchEvent(new Event('resize')); // 强制刷新布局
              });
            } else {
              this.getCaptcha();
              this.$message.error(data.msg);
            }
          });
        }
      });
    },
    getCaptcha() {
      this.dataForm.uuid = getUUID();
      this.captchaPath = this.$http.adornUrl(`/captcha.jpg?uuid=${this.dataForm.uuid}`);
    },
    goToRegister() {
      this.$router.push({ name: 'register' });
    }
  }
};
</script>

<style lang="scss">
.site-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f7f8fa;
}



.site-content {
  background-color: #fff;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.register-main {
  text-align: center;
}

.register-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 30px;
}

.el-form {
  width: 100%;
}

.el-form-item {
  margin-bottom: 20px;

  .el-input {
    width: 100%;
    border-radius: 4px;
    height: 40px;
    line-height: 40px;
  }
}

.register-captcha {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  
  img {
    cursor: pointer;
    border-radius: 4px;
    width: 100%;
    height: 40px;
    object-fit: cover;
    transition: all 0.3s ease;

    &:hover {
      opacity: 0.8;
    }
  }
}

.register-btn-submit {
  width: 100%;
  height: 45px;
  background-color: #409EFF;
  border-color: #409EFF;
  color: #fff;
  font-size: 16px;
  font-weight: 500;
  border-radius: 4px;
  transition: all 0.3s ease;

  &:hover {
    background-color: #66b1ff;
    border-color: #66b1ff;
  }
}

/* 提示信息样式 */
.el-message {
  margin-top: 20px;
}

/* 媒体查询：适应不同设备 */
@media (max-width: 480px) {
  .site-content__wrapper {
    padding: 10px;
  }

  .register-title {
    font-size: 20px;
  }
}
</style>

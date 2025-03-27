<template>
  <div>
    <input v-model="userInput" placeholder="请输入您的问题" />
    <button @click="sendMessage">发送</button>

    <div v-if="loading" class="loading-container">
      <i class="fa fa-spinner fa-spin"></i> 正在生成回复，请稍候...
    </div>

    <div v-if="responseMessage && !loading">
      <p>AI 回复: {{ responseMessage }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AiChatComponent',
  data() {
    return {
      userInput: '',         // 用户输入的内容
      responseMessage: '',   // AI 回复的信息
      loading: false         // 是否正在加载
    };
  },
  methods: {
    sendMessage() {
      if (!this.userInput.trim()) {
        console.error('请输入有效的问题');
        return;
      }

      this.loading = true;  // 开始加载
      this.responseMessage = ''; // 清空上一次的回复

      this.$http({
        url: this.$http.adornUrl('/generate-report'), // 后端接口的 URL
        method: 'post',
        data: this.$http.adornData({ content: this.userInput }) // 发送的数据格式
      })
      .then(({ data }) => {
        this.loading = false;  // 请求完成后停止加载
        if (data && data.code === 0) {
          this.responseMessage = data.data; // 成功时更新 AI 的回复
        } else {
          console.error('AI接口返回了错误: ', data.message);
        }
      })
      .catch(error => {
        this.loading = false;  // 请求失败后停止加载
        console.error('请求AI接口时发生错误: ', error);
      });
    }
  }
};
</script>

<style scoped>
/* 适配 UI 的简单样式 */
input {
  padding: 8px;
  width: 300px;
  margin-right: 10px;
}

button {
  padding: 8px 16px;
}

p {
  margin-top: 20px;
  font-size: 16px;
  color: #333;
}

/* 加载图标样式 */
.loading-container {
  display: flex;
  align-items: center;
  margin-top: 20px;
  font-size: 16px;
  color: #333;
}

.loading-container .fa-spinner {
  margin-right: 10px;
}

/* 确保 Font Awesome 图标正确显示 */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css');
</style>

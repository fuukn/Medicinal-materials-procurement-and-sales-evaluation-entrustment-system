<template>
  <div>
    <!-- ECharts 图表容器 -->
    <div ref="chart" style="width: 100%; height: 400px;"></div>
    <!-- 数据选择控件 -->
    <div style="margin-top: 20px; text-align: center;">
      <el-button type="primary" @click="downloadReport">下载报告</el-button>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'PriceWeatherChart',
  data() {
    return {
      chart: null,
      priceWeatherData: [], // 存储后端返回的数据
      showPrice: true, // 是否显示价格
      showTemp: true, // 是否显示平均温度
    };
  },
  mounted() {
    // 初始化 ECharts
    this.chart = echarts.init(this.$refs.chart);
    this.fetchData(); // 初次获取数据
  },
  methods: {
    // 获取后端数据
    fetchData() {
      this.$http({
        url: this.$http.adornUrl('/priceweatherdata/selectAll'),
        method: 'post',
        data: this.$http.adornData({})
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.priceWeatherData = data.data;
          this.updateChart();
        } else {
          this.$message.error('数据获取失败');
        }
      }).catch(error => {
        console.error('Error fetching price weather data:', error);
        this.$message.error('请求失败');
      });
    },
    // 格式化日期为 YYYY-MM-DD
    formatDate(date) {
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    },
    // 更新图表
    updateChart() {
      const dates = this.priceWeatherData.map(item => this.formatDate(item.createDate));
      const prices = this.priceWeatherData.map(item => item.price);
      const avgTemps = this.priceWeatherData.map(item => item.averageTemperature);

      const series = [];
      if (this.showPrice) {
        series.push({
          name: '价格',
          type: 'line',
          yAxisIndex: 0,
          data: prices,
        });
      }
      if (this.showTemp) {
        series.push({
          name: '平均温度',
          type: 'line',
          yAxisIndex: 1,
          data: avgTemps,
        });
      }

      const option = {
        tooltip: { trigger: 'axis' },
        legend: { data: ['价格', '平均温度'] },
        xAxis: {
          type: 'category',
          data: dates,
          name: '日期',
        },
        yAxis: [
          {
            type: 'value',
            name: '价格 (元)',
            position: 'left',
            axisLabel: { formatter: '{value}' },
          },
          {
            type: 'value',
            name: '平均温度 (°C)',
            position: 'right',
            axisLabel: { formatter: '{value}' },
          },
        ],
        series: series,
      };

      this.chart.setOption(option);
    },
    // 下载报告
    downloadReport() {
      this.$http({
        url: this.$http.adornUrl('/priceweatherdata/export'),
        method: 'get',
        responseType: 'blob' // 重要：设置为 blob 类型以处理文件下载
      }).then(response => {
        // 创建下载链接
        const blob = new Blob([response.data], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        
        // 设置文件名
        const now = new Date();
        const timestamp = `${now.getFullYear()}${String(now.getMonth() + 1).padStart(2, '0')}${String(now.getDate()).padStart(2, '0')}`;
        link.download = `PriceWeatherReport_${timestamp}.xlsx`;
        
        // 触发下载
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
        
        this.$message.success('报告下载成功');
      }).catch(error => {
        console.error('Error downloading report:', error);
        this.$message.error('报告下载失败');
      });
    }
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose();
    }
  },
};
</script>

<style scoped>
/* 可根据需要调整样式 */
.el-button {
  margin: 0 10px;
}
</style>
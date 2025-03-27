<template>
  <div>
    <div id="chartContainer">
      <div id="myChart" style="width: 600px; height: 400px;"></div>
      <div id="mySecondChart" style="width: 600px; height: 400px;"></div>
    </div>
  </div>
</template>

<script>
// import * as echarts from 'echarts'

export default {
  name: 'ChartComponent',
  activated () {
    this.init()
  },
  methods: {
    init () {
      // Fetch data for the first chart
      this.$http({
        url: this.$http.adornUrl('/zy/selectAll'),
        method: 'post',
        data: this.$http.adornData()
      }).then(({data}) => {
        if (data && data.code === 0) {
          console.log(data.data)
          const createDate = data.data.map(item => item.createDate) // 假设Price类中有create_date字段
          const price = data.data.map(item => item.price) // 假设Price类中有price字段

          // Initialize the first ECharts instance
          const myChart = echarts.init(document.getElementById('myChart'))

          // Specify the configuration items and data for the chart
          const option = {
            title: {
              text: '川芎历史价格'
            },
            tooltip: {
              trigger: 'axis'
            },
            xAxis: {
              type: 'category',
              data: createDate // X-axis data
            },
            yAxis: {
              type: 'value'
            },
            series: [{
              name: 'Price',
              data: price, // Y-axis data
              type: 'line',
              areaStyle: {}
            }]
          }

          // Display the chart using the specified configuration items and data
          myChart.setOption(option)
        }
      }).catch(error => console.error('Error fetching data for first chart:', error))

      // Fetch data for the second chart
      this.$http({
        url: this.$http.adornUrl('/zy/selectAllIndex'),
        method: 'post',
        data: this.$http.adornData()
      }).then(({data}) => {
        if (data && data.code === 0) {
          console.log(data.data)
          const date = data.data.map(item => item.date) // 假设类中有anotherDate字段
          const dataPoints = data.data.map(item => item.data) // 假设类中有anotherValue字段

          // Initialize the second ECharts instance
          const mySecondChart = echarts.init(document.getElementById('mySecondChart'))

          // Specify the configuration items and data for the second chart
          const secondOption = {
            title: {
              text: '中药材指数'
            },
            tooltip: {
              trigger: 'axis'
            },
            xAxis: {
              type: 'category',
              data: date // X-axis data
            },
            yAxis: {
              type: 'value'
            },
            series: [{
              name: '指數',
              data: dataPoints, // Y-axis data
              type: 'line',
              areaStyle: {}
            }]
          }

          // Display the second chart using the specified configuration items and data
          mySecondChart.setOption(secondOption)
        }
      }).catch(error => console.error('Error fetching data for second chart:', error))
    }
  }
}
</script>

<style scoped>
#chartContainer {
  display: flex;
  justify-content: space-between;
}

#myChart, #mySecondChart {
  width: 800px;
  height: 600px;
}
</style>

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
  mounted () {
    // Fetch data for the first chart
    fetch('http://localhost:8080/zy/selectAll', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data => {
        const createDate = data.map(item => item.createDate) // 假设Price类中有create_date字段
        const price = data.map(item => item.price) // 假设Price类中有price字段

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
      })
      .catch(error => console.error('Error fetching data:', error))

    console.log('后端返回数据：11111111111111')

    // Fetch data for the second chart
    fetch('http://localhost:8080/zy/selectAllIndex', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => response.json())
      .then(data1 => {
        const date = data1.map(item => item.date) // 假设类中有anotherDate字段
        const data = data1.map(item => item.data) // 假设类中有anotherValue字段

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
            data: data, // Y-axis data
            type: 'line',
            areaStyle: {}
          }]
        }

        // Display the second chart using the specified configuration items and data
        mySecondChart.setOption(secondOption)
      })
      .catch(error => console.error('Error fetching data for second chart:', error))
  },

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

<template>
  <div class="mod-home">
    <!-- 项目介绍标题 -->
    <h3 class="title">项目介绍</h3>
    <!-- 项目描述列表 -->
    <ul class="description-list">
      <li>欢迎使用中药材市价评估系统</li>
    </ul>
    <!-- 系统详细介绍 -->
    <div class="system-details">
      <h4 class="sub-title">系统特点</h4>
      <p class="detail-text">
        我们的中药材市价评估系统具备精准的数据采集功能，能够实时跟踪各大中药材市场的价格动态。通过先进的数据分析算法，对海量的市场数据进行深度挖掘和分析，为您提供最准确、最及时的中药材价格评估。
      </p>
      <h4 class="sub-title">应用场景</h4>
      <p class="detail-text">
        该系统广泛应用于中药材种植户、经销商、药企等多个领域。种植户可以根据系统提供的价格信息，合理规划种植品种和规模；经销商能够及时掌握市场价格波动，优化采购和销售策略；药企则可以通过准确的价格评估，控制生产成本，保障药品质量。
      </p>
    </div>
  </div>
</template>

<script>
export default {
}
</script>

<style scoped>
.mod-home {
  line-height: 1.5;
  padding: 20px;
  width: 80%;
  margin: 0 auto;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 24px;
  color: #333;
  border-bottom: 2px solid #555;
  padding-bottom: 5px;
  margin-bottom: 15px;
}

.description-list {
  padding-left: 0;
  list-style-type: none;
}

.description-list li {
  font-size: 18px;
  padding: 10px;
  background-color: #fff;
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 10px;
  transition: all 0.3s ease;
}

.description-list li:hover {
  background-color: #eef7ff;
  border-color: #aaccee;
  transform: translateY(-3px);
}

.system-details {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #ccc;
}

.sub-title {
  font-size: 20px;
  color: #555;
  margin-bottom: 10px;
}

.detail-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
  line-height: 1.6;
}
</style>
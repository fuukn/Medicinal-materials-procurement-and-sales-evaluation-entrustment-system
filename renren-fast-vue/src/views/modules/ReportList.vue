<template>
  <div class="delegation-hall">
    <!-- 顶部概览 -->
    <el-row :gutter="20" class="overview">
      <el-col :span="8">
        <el-statistic title="总委托" :value="totalDelegations" />
      </el-col>
      <el-col :span="8">
        <el-statistic title="待处理" :value="pendingDelegations" />
      </el-col>
      <el-col :span="8">
        <el-statistic title="我的委托" :value="myDelegations" />
      </el-col>
    </el-row>

    <!-- 搜索与筛选 -->
    <el-row :gutter="20" class="filters">
      <el-col :span="6">
        <el-input v-model="searchQuery" placeholder="搜索药材或地址" />
      </el-col>
      <el-col :span="6">
        <el-select v-model="filterStatus" placeholder="状态">
          <el-option label="全部" value />
          <el-option label="待处理" value="1" />
          <el-option label="等待中" value="2" />
          <el-option label="进行中" value="3" />
          <el-option label="已完成" value="4" />
        </el-select>
      </el-col>
      <el-col :span="6">
        <el-button type="primary" @click="fetchDelegations">查询</el-button>
      </el-col>
    </el-row>

    <!-- 委托列表 -->
    <el-table :data="delegationList" style="width: 100%" border>
      <el-table-column prop="herbalMedicine" label="药材" />
      <el-table-column prop="address" label="地址" />
      <el-table-column label="状态">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="联系电话" />
      <el-table-column prop="createDate" label="发布时间" :formatter="dateFormatter" />
      <el-table-column label="规模">
        <template slot-scope="scope">{{ getScaleText(scope.row.scale) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button type="text" @click="viewDetail(scope.row)">详情</el-button>
          <el-button
            type="text"
            v-if="scope.row.status === 1"
            @click="acceptDelegation(scope.row)"
          >申请接单</el-button>
          <el-button
            type="text"
            v-if="scope.row.report"
            @click="downloadReport(scope.row.report)"
          >下载报告</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      @current-change="handlePageChange"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      style="margin-top: 20px; text-align: center;"
    />

    <!-- 詳情彈窗 -->
    <el-dialog title="委托詳情" :visible.sync="detailDialogVisible" width="40%">
  <el-card v-if="detailData" class="info-card">
    <div class="single-field-info">
      <div class="field-item">
        <span class="field-label">委托ID:</span>
        <span class="field-value">{{ detailData.id || '無' }}</span>
      </div>
      <div class="field-item">
        <span class="field-label">藥材名稱:</span>
        <span class="field-value">{{ detailData.herbalMedicine || '無' }}</span>
      </div>
      <div class="field-item">
        <span class="field-label">狀態:</span>
        <span class="field-value">{{ getStatusText(detailData.status) }}</span>
      </div>
      <div class="field-item">
        <span class="field-label">創建日期:</span>
        <span class="field-value">{{ dateFormatter(detailData) }}</span>
      </div>
      <div class="field-item">
        <span class="field-label">地址:</span>
        <span class="field-value">{{ detailData.address || '無' }}</span>
      </div>
      <div class="field-item">
        <span class="field-label">聯繫電話:</span>
        <span class="field-value">{{ detailData.phone || '無' }}</span>
      </div>
      <div class="field-item">
        <span class="field-label">規模:</span>
        <span class="field-value">{{ getScaleText(detailData.scale) }}</span>
      </div>
      <div class="field-item">
        <span class="field-label">詳情:</span>
        <span class="field-value">{{ detailData.details || '無' }}</span>
      </div>
    </div>
  </el-card>
  <div v-else>暫無詳情</div>
  <span slot="footer">
    <el-button @click="detailDialogVisible = false">關閉</el-button>
  </span>
</el-dialog>
  </div>
</template>

<script>
import L from "leaflet";
import "leaflet/dist/leaflet.css";

export default {
  name: "DelegationHall",
  data() {
    return {
      totalDelegations: 0,
      pendingDelegations: 0,
      myDelegations: 0,
      searchQuery: "",
      filterStatus: "",
      delegationList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      mapDialogVisible: false,
      map: null,
      coordinates: "",
      form: {
        herbalMedicine: "",
        address: "",
        phone: "",
        scale: "",
        details: ""
      },
      detailDialogVisible: false, // 控制詳情彈窗顯示
      detailData: {} // 存儲詳情數據
    };
  },
  mounted() {
    this.fetchDelegations();
  },
  methods: {
    fetchDelegations() {
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        query: this.searchQuery,
        status: this.filterStatus
      };
      this.$http({
        url: this.$http.adornUrl("/sys/evaluationdelegation/list"),
        method: "get",
        params: this.$http.adornParams(params)
      })
        .then(({ data }) => {
          if (data && data.code === 0) {
            this.delegationList = data.page.list;
            this.total = data.page.totalCount;
            // 模拟统计数据（实际需后端返回）
            this.totalDelegations = data.page.totalCount;
            this.pendingDelegations = data.page.list.filter(
              item => item.status === 1
            ).length;
            this.myDelegations = data.page.list.filter(
              item => item.userId === 1
            ).length; // 假设 userId=1 为当前用户
          }
        })
        .catch(error => {
          console.error("Error fetching delegations:", error);
          this.$message.error("获取委托列表失败");
        });
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.fetchDelegations();
    },
    getStatusText(status) {
      return { 1: "待处理", 2: "等待中", 3: "进行中" , 4: "已完成"}[status] || "未知";
    },
    getStatusTagType(status) {
      return { 1: "warning", 2: "primary", 3: "success" }[status] || "info";
    },
    getScaleText(scale) {
      return (
        { 1: "小型药房与个体户", 2: "中小型企业", 3: "大型企业" }[scale] ||
        "未知"
      );
    },
    dateFormatter(row, column) {
      return new Date(row.createDate).toLocaleDateString("zh-CN");
    },
    viewDetail(row) {
      this.$http({
        url: this.$http.adornUrl(`/sys/evaluationdelegation/info/${row.id}`),
        method: "get"
      })
        .then(({ data }) => {
          if (data && data.code === 0) {
            this.detailData = data.evaluationDelegation || {};
            this.detailDialogVisible = true;
          } else {
            this.$message.error("獲取詳情失敗");
          }
        })
        .catch(error => {
          console.error("Error fetching detail:", error);
          this.$message.error("獲取詳情失敗");
        });
    },
    acceptDelegation(row) {
      this.$confirm(`确认接单 ${row.herbalMedicine} 的委托吗？`, "提示", {
        type: "warning"
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl(
            `/sys/evaluationdelegation/accept/${row.id}`
          ),
          method: "post"
        })
          .then(() => {
            this.$message.success("接单成功");
            this.fetchDelegations();
          })
          .catch(error => {
            console.error("Accept delegation error:", error);
            this.$message.error("接单失败");
          });
      });
    },
    downloadReport(reportUrl) {
      this.$http({
        url: this.$http.adornUrl(reportUrl), // 假设 report 是相对路径，如 /report/xxx.pdf
        method: "get",
        responseType: "blob"
      })
        .then(response => {
          const blob = new Blob([response.data], { type: "application/pdf" }); // 假设是 PDF，可根据实际调整
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement("a");
          link.href = url;
          link.download = `Report_${new Date().toLocaleDateString(
            "zh-CN"
          )}.pdf`;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
          this.$message.success("报告下载成功");
        })
        .catch(error => {
          console.error("Error downloading report:", error);
          this.$message.error("报告下载失败");
        });
    },
    showDialog() {
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.form.resetFields();
      });
    },
    resetForm() {
      this.$refs.form.resetFields();
      this.coordinates = "";
    },
    showMapDialog() {
      this.mapDialogVisible = true;
      this.$nextTick(() => {
        this.initMap();
      });
    },

    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const params = {
            herbalMedicine: this.form.herbalMedicine,
            address: this.form.address,
            phone: this.form.phone,
            scale: this.form.scale,
            details: this.form.details,
            userId: 1 // 替换为实际用户ID
          };
          this.$http({
            url: this.$http.adornUrl("/sys/evaluationdelegation/add"),
            method: "post",
            data: this.$http.adornData(params)
          })
            .then(({ data }) => {
              this.$message.success("委托发布成功");
              this.dialogVisible = false;
              this.fetchDelegations();
            })
            .catch(error => {
              this.$message.error("发布失败");
              console.error(error);
            });
        }
      });
    }
  },
  showDialog() {
    this.dialogVisible = true;
    this.$nextTick(() => {
      this.$refs.form.resetFields();
    });
  },
  resetForm() {
    this.$refs.form.resetFields();
    this.coordinates = "";
  },
  showMapDialog() {
    this.mapDialogVisible = true;
    this.$nextTick(() => {
      this.initMap();
    });
  },
  fetchTile(z, x, y) {
    return this.$http({
      url: this.$http.adornUrl(`/api/map/tile/${z}/${x}/${y}`),
      method: "get",
      responseType: "blob" // 返回二进制图片数据
    })
      .then(({ data }) => {
        return URL.createObjectURL(data); // 返回 blob URL
      })
      .catch(error => {
        console.error("Error fetching tile:", error);
        throw error; // 抛出错误供 Leaflet 处理
      });
  },
  showDialog() {
    this.dialogVisible = true;
    this.$nextTick(() => {
      this.$refs.form.resetFields();
    });
  },
  resetForm() {
    this.$refs.form.resetFields();
    this.coordinates = "";
  },
  showMapDialog() {
    this.mapDialogVisible = true;
    this.$nextTick(() => {
      this.initMap();
    });
  },
  beforeDestroy() {
    if (this.map) {
      this.map.remove();
    }
  }
};
</script>

<style scoped>
.delegation-hall {
  padding: 20px;
}
.overview {
  margin-bottom: 20px;
}
.filters {
  margin-bottom: 20px;
}
</style>
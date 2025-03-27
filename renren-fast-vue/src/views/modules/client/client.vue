<template>
  <div class="evaluation-delegation">
    <!-- 发布委托按钮 -->
    <el-button type="primary" @click="showDialog">发布委托</el-button>

    <!-- 表单对话框 -->
    <el-dialog title="新增评估委托" :visible.sync="dialogVisible" width="800px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="药材" prop="herbalMedicine">
          <el-input v-model="form.herbalMedicine" placeholder="请输入药材名称"></el-input>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input
            v-model="form.address"
            placeholder="点击地图选择地址"
            readonly
            @click.native="showMapDialog"
          ></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="规模" prop="scale">
          <el-select v-model="form.scale" placeholder="请选择规模">
            <el-option label="小型药房与个体户" :value="1"></el-option>
            <el-option label="中小型企业" :value="2"></el-option>
            <el-option label="大型企业" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="其他细节" prop="details">
          <el-input type="textarea" v-model="form.details" placeholder="请输入其他细节" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </span>
    </el-dialog>

    <!-- 地图对话框 -->
    <el-dialog title="选择地址" :visible.sync="mapDialogVisible" width="80%" append-to-body>
      <div id="map" style="height: 500px; width: 100%;"></div>
      <div style="margin-top: 10px; text-align: center;">
        <el-input
          id="coords"
          v-model="coordinates"
          readonly
          placeholder="点击地图显示经纬度"
          style="width: 300px;"
        ></el-input>
      </div>
      <span slot="footer">
        <el-button @click="mapDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCoordinates">确认</el-button>
      </span>
    </el-dialog>

    <!-- 委托列表 -->
    <div class="delegation-list" style="margin-top: 20px;">
      <h3>我的委托列表</h3>
      <el-table :data="delegationList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="herbalMedicine" label="药材" width="150"></el-table-column>
        <el-table-column prop="address" label="地址" width="200"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="150"></el-table-column>
        <el-table-column prop="createDate" label="发布时间" width="180"></el-table-column>
        <el-table-column prop="scale" label="规模" width="150">
          <template slot-scope="scope">{{ getScaleText(scope.row.scale) }}</template>
        </el-table-column>
        <el-table-column prop="details" label="其他细节" show-overflow-tooltip></el-table-column>
        <el-table-column prop="appraiserId" label="申请人ID" width="120">
          <el-table-column prop="appraiserId" label="查看与批准" width="150">
            <template slot-scope="scope">
              <div style="display: flex; align-items: center; gap: 10px;">
                <span>{{ scope.row.appraiserId || '未分配' }}</span>
                <el-button
                  v-if="scope.row.appraiserId"
                  type="primary"
                  size="mini"
                  @click="showAppraiserInfo(scope.row.appraiserId, scope.row.id)"
                >查看信息</el-button>
              </div>
            </template>
          </el-table-column>
          <template slot-scope="scope">
            <el-link
              v-if="scope.row.appraiserId"
              type="primary"
              :underline="true"
              @click="debugClick(scope.row.appraiserId, scope.row.id)"
              style="cursor: pointer;"
            >{{ scope.row.appraiserId }}</el-link>
            <span v-else>未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="report" label="报告" width="150">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.report"
              type="primary"
              size="mini"
              @click="downloadReport(scope.row.report)"
            >下载报告</el-button>
            <span v-else>暂无</span>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog title="申请人信息" :visible.sync="appraiserDialogVisible" width="600px">
        <el-card v-if="appraiserInfo" class="info-card">
          <div class="single-field-info">
            <div class="field-item">
              <span class="field-label">用户名:</span>
              <span class="field-value">{{ appraiserInfo.userName || '未设置' }}</span>
            </div>
            <div class="field-item">
              <span class="field-label">邮箱:</span>
              <span class="field-value">{{ appraiserInfo.email || '未设置' }}</span>
            </div>
            <div class="field-item">
              <span class="field-label">手机号:</span>
              <span class="field-value">{{ appraiserInfo.mobile || '未设置' }}</span>
            </div>
            <div class="field-item">
              <span class="field-label">毕业院校:</span>
              <span class="field-value">{{ appraiserInfo.school || '未设置' }}</span>
            </div>
            <div class="field-item">
              <span class="field-label">专业领域:</span>
              <span class="field-value">{{ appraiserInfo.specialty || '未设置' }}</span>
            </div>
            <div class="field-item">
              <span class="field-label">工作年限:</span>
              <span
                class="field-value"
              >{{ appraiserInfo.workYears ? appraiserInfo.workYears + '年' : '未设置' }}</span>
            </div>
            <div class="field-item">
              <span class="field-label">评级:</span>
              <span class="field-value">{{ appraiserInfo.rating || '未设置' }}</span>
            </div>
            <div class="field-item">
              <span class="field-label">注册日期:</span>
              <span class="field-value">{{ appraiserInfo.registerDate || '未设置' }}</span>
            </div>
            <div class="field-item">
              <span class="field-label">个人介绍:</span>
              <span class="field-value">{{ appraiserInfo.introduction || '未设置' }}</span>
            </div>
          </div>
        </el-card>
        <div v-else>暂无信息</div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="appraiserDialogVisible = false">关闭</el-button>
          <el-button type="success" @click="handleApprove(1)">批准</el-button>
          <el-button type="danger" @click="handleApprove(0)">拒绝</el-button>
        </span>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import L from "leaflet";
import "leaflet/dist/leaflet.css";

export default {
  name: "EvaluationDelegation",
  data() {
    return {
      appraiserDialogVisible: false,
      appraiserInfo: null,
      currentDelegationId: null,
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
      rules: {
        herbalMedicine: [
          { required: true, message: "请输入药材名称", trigger: "blur" }
        ],
        address: [{ required: true, message: "请选择地址", trigger: "change" }],
        phone: [
          { required: true, message: "请输入联系电话", trigger: "blur" },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入有效的手机号",
            trigger: "blur"
          }
        ],
        scale: [{ required: true, message: "请选择规模", trigger: "change" }]
      },
      delegationList: [] // 委托列表数据
    };
  },
  mounted() {
    this.fetchDelegationList(); // 页面加载时获取委托列表
  },
  methods: {
    downloadReport(delegateId) {
      this.$http({
        url: this.$http.adornUrl(
          `/sys/evaluationdelegation/download?delegateId=${delegateId}`
        ),
        method: "get",
        responseType: "blob" // 重要：处理文件流
      })
        .then(response => {
          // 创建 Blob 对象，指定类型为 PDF（根据后端返回的类型）
          const blob = new Blob([response.data], { type: "application/pdf" });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement("a");
          link.href = url;

          // 设置文件名，可以从后端 Content-Disposition 获取，或者自定义
          const contentDisposition = response.headers["content-disposition"];
          let fileName = `${delegateId}.pdf`; // 默认文件名
          if (contentDisposition) {
            const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
            if (fileNameMatch && fileNameMatch[1]) {
              fileName = fileNameMatch[1];
            }
          }
          link.download = fileName;

          // 触发下载
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);

          this.$message.success("报告下载成功");
        })
        .catch(error => {
          console.error("Error downloading report:", error);
          // 检查是否是 401 错误
          if (error.response && error.response.status === 401) {
            this.$message.error("登录已过期，请重新登录");
            // 可选：跳转到登录页
            // this.$router.push('/login');
          } else {
            this.$message.error("报告下载失败");
          }
        });
    },
    fetchTile(z, x, y) {
      return this.$http({
        url: this.$http.adornUrl(`/api/map/tile/${z}/${x}/${y}`),
        method: "get",
        responseType: "blob"
      })
        .then(({ data }) => {
          return URL.createObjectURL(data);
        })
        .catch(error => {
          console.error("Error fetching tile:", error);
          throw error;
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
    initMap() {
      if (this.map) {
        this.map.remove();
      }
      this.map = L.map("map", {
        minZoom: 2,
        maxZoom: 10,
        maxBounds: [
          [-85, -180],
          [85, 180]
        ],
        maxBoundsViscosity: 1.0
      }).setView([0, 0], 2);

      const customTileLayer = L.TileLayer.extend({
        createTile: (coords, done) => {
          const tile = document.createElement("img");
          this.fetchTile(coords.z, coords.x, coords.y)
            .then(blobUrl => {
              tile.src = blobUrl;
              done(null, tile);
            })
            .catch(error => {
              console.error("Tile fetch error:", coords, error);
              done(error, tile);
            });
          return tile;
        }
      });

      const tileLayer = new customTileLayer("/api/map/tile/{z}/{x}/{y}", {
        attribution: "Google Maps Tiles",
        tileSize: 256,
        noWrap: true,
        errorTileUrl:
          "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg=="
      }).addTo(this.map);

      tileLayer.on("tileload", e => console.log("Tile loaded:", e.tile.src));
      tileLayer.on("tileerror", e =>
        console.error("Tile error:", e.tile.src, e.error)
      );

      this.map.on("click", e => {
        const zoom = this.map.getZoom();
        const point = this.map.project(e.latlng, zoom);
        const tileSize = 256;
        const tileX = Math.floor(point.x / tileSize);
        const tileY = Math.floor(point.y / tileSize);
        const pixelX = Math.floor(point.x % tileSize);
        const pixelY = Math.floor(point.y % tileSize);

        const params = {
          z: zoom,
          tileX: tileX,
          tileY: tileY,
          pixelX: pixelX,
          pixelY: pixelY
        };

        this.$http({
          url: this.$http.adornUrl("/api/map/coordinates"),
          method: "get",
          params: this.$http.adornParams(params)
        })
          .then(({ data }) => {
            this.coordinates = `经度: ${data[0].toFixed(
              6
            )}, 纬度: ${data[1].toFixed(6)}`;
          })
          .catch(error => {
            console.error("Coordinates error:", error);
            this.$message.error("获取坐标失败");
            this.coordinates = "获取坐标失败";
          });
      });
    },
    confirmCoordinates() {
      if (this.coordinates && !this.coordinates.includes("失败")) {
        this.form.address = this.coordinates;
        this.mapDialogVisible = false;
      } else {
        this.$message.warning("请先选择有效的地址坐标");
      }
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
            userId: 1 // 假设当前用户ID为1，实际应从登录状态获取
          };

          this.$http({
            url: this.$http.adornUrl("/sys/evaluationdelegation/add"),
            method: "post",
            data: this.$http.adornData(params)
          })
            .then(({ data }) => {
              this.$message.success("委托发布成功");
              this.dialogVisible = false;
              this.fetchDelegationList(); // 发布成功后刷新列表
            })
            .catch(error => {
              this.$message.error("发布失败");
              console.error(error);
            });
        }
      });
    },
    // 获取委托列表
    fetchDelegationList() {
      this.$http({
        url: this.$http.adornUrl("/sys/evaluationdelegation/CXlist"),
        method: "get"
      })
        .then(({ data }) => {
          if (data && data.code === 0) {
            this.delegationList = data.data; // 假设后端返回的数据在 data 字段中
          } else {
            this.$message.error(data.msg || "获取委托列表失败");
          }
        })
        .catch(error => {
          this.$message.error("网络错误，请稍后重试");
          console.error(error);
        });
    },
    // 状态文本转换
    getStatusText(status) {
      const statusMap = {
        1: "待处理",
        2: "等待中",
        3: "进行中",
        4: "已结束"
      };
      return statusMap[status] || "未知";
    },
    // 状态标签样式
    getStatusType(status) {
      const typeMap = {
        1: "info", // 待处理 - 灰色
        2: "warning", // 等待中 - 黄色
        3: "", // 进行中 - 默认蓝色
        4: "success" // 已结束 - 绿色
      };
      return typeMap[status] || "";
    },
    // 规模文本转换
    getScaleText(scale) {
      const scaleMap = {
        1: "小型药房与个体户",
        2: "中小型企业",
        3: "大型企业"
      };
      return scaleMap[scale] || "未知";
    },
    handleApprove(select) {
      if (!this.currentDelegationId) {
        this.$message.error("未选择委托");
        return;
      }
      this.$confirm(`确认${select === 1 ? "批准" : "拒绝"}此委托吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/sys/evaluationdelegation/approve"),
            method: "post",
            data: this.$http.adornData({
              id: this.currentDelegationId,
              select: select
            })
          })
            .then(({ data }) => {
              if (data && data.code === 0) {
                this.$message.success("操作成功");
                this.appraiserDialogVisible = false;
                this.fetchDelegationList();
              } else {
                this.$message.error(data.msg || "操作失败");
              }
            })
            .catch(error => {
              this.$message.error("网络错误，请稍后重试");
              console.error(error);
            });
        })
        .catch(() => {
          this.$message.info("已取消操作");
        });
    },
    showAppraiserInfo(appraiserId, delegationId) {
      this.currentDelegationId = delegationId;
      this.$http({
        url: this.$http.adornUrl(`/appraiser/info/${appraiserId}`),
        method: "get"
      })
        .then(({ data }) => {
          if (data && data.code === 0) {
            this.appraiserInfo = data.appraiser;
            this.appraiserDialogVisible = true;
          } else {
            this.$message.error(data.msg || "获取申请人信息失败");
          }
        })
        .catch(error => {
          this.$message.error("网络错误，请稍后重试");
          console.error(error);
        });
    }
  },
  beforeDestroy() {
    if (this.map) {
      this.map.remove();
    }
  }
};
</script>

<style scoped>
.evaluation-delegation {
  padding: 20px;
}

.delegation-list {
  margin-top: 20px;
}

.el-table {
  margin-top: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 添加按钮样式 */
.el-button--mini {
  padding: 5px 10px;
}
</style>
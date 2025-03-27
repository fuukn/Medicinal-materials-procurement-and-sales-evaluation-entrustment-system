<template>
  <div class="appraiser-info">
    <h2>鉴定师信息</h2>
    <el-button type="primary" @click="showEditDialog" style="margin-bottom: 20px">修改信息</el-button>
    <el-card v-if="appraiser" class="info-card">
      <div class="single-field-info">
        <div class="field-item">
          <span class="field-label">用户名:</span>
          <span class="field-value">{{ appraiser.userName || '未设置' }}</span>
        </div>
        <div class="field-item">
          <span class="field-label">邮箱:</span>
          <span class="field-value">{{ appraiser.email || '未设置' }}</span>
        </div>
        <div class="field-item">
          <span class="field-label">手机号:</span>
          <span class="field-value">{{ appraiser.mobile || '未设置' }}</span>
        </div>
        <div class="field-item">
          <span class="field-label">毕业院校:</span>
          <span class="field-value">{{ appraiser.school || '未设置' }}</span>
        </div>
        <div class="field-item">
          <span class="field-label">涉及领域:</span>
          <span class="field-value">{{ appraiser.specialty || '未设置' }}</span>
        </div>
        <div class="field-item">
          <span class="field-label">工作年限:</span>
          <span class="field-value">{{ appraiser.workYears ? appraiser.workYears + '年' : '未设置' }}</span>
        </div>
        <div class="field-item">
          <span class="field-label">评级:</span>
          <span class="field-value">{{ appraiser.rating || '未设置' }}</span>
        </div>
        <div class="field-item">
          <span class="field-label">注册日期:</span>
          <span class="field-value">{{ appraiser.registerDate || '未设置' }}</span>
        </div>
        <div class="field-item">
          <span class="field-label">个人介绍:</span>
          <span class="field-value">{{ appraiser.introduction || '未设置' }}</span>
        </div>
      </div>
    </el-card>
    <div v-else class="no-data">暂无信息</div>

    <!-- 任务列表 -->
    <div class="task-list" style="margin-top: 20px;">
      <h3>任务列表</h3>
      <el-table :data="taskList" style="width: 100%" border>
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
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button type="text" @click="viewDetail(scope.row)">详情</el-button>
            <el-button
              type="text"
              v-if="scope.row.status === 1"
              @click="acceptDelegation(scope.row)"
            >申请接单</el-button>
            <el-upload
              v-if="scope.row.status === 3 && !scope.row.report"
              action=""
              :http-request="uploadReport"
              :data="{ id: scope.row.id }"
              :show-file-list="false"
              accept=".pdf"
              style="display: inline-block; margin-left: 10px;"
            >
              <el-button type="text">上传报告</el-button>
            </el-upload>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog title="委托详情" :visible.sync="detailDialogVisible" width="40%">
      <el-card v-if="detailData" class="info-card">
        <div class="single-field-info">
          <div class="field-item">
            <span class="field-label">委托ID:</span>
            <span class="field-value">{{ detailData.id || '无' }}</span>
          </div>
          <div class="field-item">
            <span class="field-label">药材名称:</span>
            <span class="field-value">{{ detailData.herbalMedicine || '无' }}</span>
          </div>
          <div class="field-item">
            <span class="field-label">状态:</span>
            <span class="field-value">{{ getStatusText(detailData.status) }}</span>
          </div>
          <div class="field-item">
            <span class="field-label">创建日期:</span>
            <span class="field-value">{{ dateFormatter(detailData) }}</span>
          </div>
          <div class="field-item">
            <span class="field-label">地址:</span>
            <span class="field-value">{{ detailData.address || '无' }}</span>
          </div>
          <div class="field-item">
            <span class="field-label">联系电话:</span>
            <span class="field-value">{{ detailData.phone || '无' }}</span>
          </div>
          <div class="field-item">
            <span class="field-label">规模:</span>
            <span class="field-value">{{ getScaleText(detailData.scale) }}</span>
          </div>
          <div class="field-item">
            <span class="field-label">详情:</span>
            <span class="field-value">{{ detailData.details || '无' }}</span>
          </div>
        </div>
      </el-card>
      <div v-else>暂无详情</div>
      <span slot="footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <!-- 修改信息的弹窗 -->
    <el-dialog title="修改鉴定师信息" :visible.sync="editDialogVisible" width="50%">
      <el-form :model="editForm" :rules="rules" ref="editForm" label-width="100px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="editForm.userName"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="editForm.mobile"></el-input>
        </el-form-item>
        <el-form-item label="毕业院校" prop="school">
          <el-input v-model="editForm.school"></el-input>
        </el-form-item>
        <el-form-item label="专业领域" prop="specialty">
          <el-select v-model="editForm.specialty" placeholder="请选择领域">
            <el-option label="药企采购" value="药企采购"></el-option>
            <el-option label="供应链" value="供应链"></el-option>
            <el-option label="风险管理" value="风险管理"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工作年限" prop="workYears">
          <el-input v-model="editForm.workYears" placeholder="请输入工作年限"></el-input>
        </el-form-item>
        <el-form-item label="注册日期" prop="registerDate">
          <el-input v-model="editForm.registerDate" disabled placeholder="由系统自动生成"></el-input>
        </el-form-item>
        <el-form-item label="个人介绍" prop="introduction">
          <el-input type="textarea" v-model="editForm.introduction" rows="3"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditForm">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'AppraiserInfo',
  data() {
    return {
      appraiser: null,
      taskList: [],
      detailDialogVisible: false,
      detailData: null,
      editDialogVisible: false,
      editForm: {
        userName: '',
        school: '',
        introduction: '',
        email: '',
        mobile: '',
        specialty: '',
        workYears: '',
        rating: '',
        registerDate: ''
      },
      rules: {
        userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        specialty: [{ required: false, message: '请选择涉及领域', trigger: 'change' }],
        workYears: [{ required: false, message: '请输入工作年限', trigger: 'blur' }]
      }
    };
  },
  mounted() {
    this.fetchAppraiserInfo();
    this.fetchTasks();
  },
  methods: {
    fetchAppraiserInfo() {
      this.$http({
        url: this.$http.adornUrl('/appraiser/info'),
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.appraiser = data.appraiser;
          console.log('后端返回数据:', this.appraiser);
        } else {
          this.$message.error(data.msg || '获取信息失败');
        }
      }).catch(error => {
        console.log('请求失败：', error.response || error.message);
        this.$message.error('网络错误，请稍后重试');
      });
    },
    fetchTasks() {
      this.$http({
        url: this.$http.adornUrl('/sys/evaluationdelegation/PGlist'),
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.taskList = data.data || [];
          console.log('任务列表:', this.taskList);
        } else {
          this.$message.error(data.msg || '获取任务列表失败');
          this.taskList = [];
        }
      }).catch(error => {
        this.$message.error('网络错误，请稍后重试');
        console.error(error);
        this.taskList = [];
      });
    },
    showEditDialog() {
      this.$http({
        url: this.$http.adornUrl('/appraiser/info'),
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.editForm = { ...data.appraiser };
          this.editDialogVisible = true;
        } else {
          this.$message.error(data.msg || '获取信息失败');
        }
      }).catch(error => {
        this.$message.error('网络错误，请稍后重试');
        console.error(error);
      });
    },
    submitEditForm() {
      this.$refs['editForm'].validate(valid => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl('/appraiser/edit'),
            method: 'post',
            data: this.editForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message.success('信息修改成功');
              this.editDialogVisible = false;
              this.fetchAppraiserInfo();
              this.fetchTasks();
            } else {
              this.$message.error(data.msg || '修改失败');
            }
          }).catch(error => {
            this.$message.error('网络错误，请稍后重试');
            console.error(error);
          });
        } else {
          return false;
        }
      });
    },
    viewDetail(row) {
      this.detailData = { ...row };
      this.detailDialogVisible = true;
    },
    acceptDelegation(row) {
      this.$confirm('确认申请接单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/sys/evaluationdelegation/accept'),
          method: 'post',
          data: { id: row.id }
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success('申请接单成功');
            this.fetchTasks();
          } else {
            this.$message.error(data.msg || '申请失败');
          }
        }).catch(error => {
          this.$message.error('网络错误，请稍后重试');
          console.error(error);
        });
      }).catch(() => {
        this.$message.info('已取消操作');
      });
    },
    downloadReport(report) {
      window.open(this.$http.adornUrl(`/sys/evaluationdelegation/download?report=${report}`));
    },
    uploadReport(params) {
      const file = params.file;
      const formData = new FormData();
      formData.append('file', file);
      formData.append('id', params.data.id); // 传递任务 ID

      this.$http({
        url: this.$http.adornUrl('/sys/evaluationdelegation/uploadReport'),
        method: 'post',
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data' }
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message.success('报告上传成功');
          this.fetchTasks(); // 刷新任务列表
        } else {
          this.$message.error(data.msg || '上传失败');
        }
      }).catch(error => {
        this.$message.error('网络错误，请稍后重试');
        console.error(error);
      });
    },
    getStatusText(status) {
      const statusMap = { 0: '待审核', 1: '待处理', 3: '进行中' };
      return statusMap[status] || '未知';
    },
    getStatusTagType(status) {
      const typeMap = { 0: 'info', 1: 'warning', 3: 'success' };
      return typeMap[status] || 'info';
    },
    dateFormatter(row) {
      return row.createDate ? new Date(row.createDate).toLocaleString() : '未设置';
    },
    getScaleText(scale) {
      const scaleMap = { 1: '小型', 2: '中型', 3: '大型' };
      return scaleMap[scale] || '未设置';
    }
  }
};
</script>

<style scoped>
.single-field-info {
  font-size: 16px;
}

.field-item {
  margin-bottom: 10px;
}

.field-label {
  font-weight: bold;
  margin-right: 10px;
}

.field-value {
  display: inline;
}

.task-list {
  margin-top: 20px;
}

.no-data {
  text-align: center;
  color: #909399;
}
</style>
<template>
    <div>
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="日期筛选">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
        </el-form-item>
      </el-form>
  
      <el-table
        :data="newsList"
        border
        style="width: 100%"
      >
        <el-table-column
          prop="id"
          label="ID"
          width="180"
        ></el-table-column>
        <el-table-column
          prop="title"
          label="标题"
          width="180"
        ></el-table-column>
        <el-table-column
          prop="content"
          label="内容"
        ></el-table-column>
        <el-table-column
          prop="date"
          label="发布时间"
          width="180"
        ></el-table-column>
      </el-table>
  
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30, 40]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </div>
  </template>
  
  <script>
  export default {
    name: 'NewsList',
    data() {
      return {
        searchForm: {
          dateRange: []
        },
        newsList: [],
        currentPage: 1,
        pageSize: 10,
        total: 0
      };
    },
    created() {
      this.fetchNewsList();
    },
    methods: {
      fetchNewsList() {
        const params = {
          page: this.currentPage,
          limit: this.pageSize,
          startDate: this.searchForm.dateRange ? this.searchForm.dateRange[0] : null,
          endDate: this.searchForm.dateRange ? this.searchForm.dateRange[1] : null
        };
  
        this.$http({
          url: this.$http.adornUrl('/news/list'),
          method: 'get',
          params: this.$http.adornParams(params)
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.newsList = data.page.list;
            this.total = data.page.totalCount;
          }
        }).catch(error => {
          console.error('Error fetching news list:', error);
        });
      },
      onSearch() {
        this.currentPage = 1;
        this.fetchNewsList();
      },
      handleSizeChange(val) {
        this.pageSize = val;
        this.fetchNewsList();
      },
      handleCurrentChange(val) {
        this.currentPage = val;
        this.fetchNewsList();
      }
    }
  };
  </script>
  
  <style scoped>
  .demo-form-inline {
    margin-bottom: 20px;
  }
  </style>
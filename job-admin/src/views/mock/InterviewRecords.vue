<template>
  <div class="app-container">
    <div class="filter-container">
      <el-form :inline="true" :model="listQuery">
        <el-form-item>
          <el-input v-model="listQuery.orderId" placeholder="订单号ID" @keyup.enter.native="handleFilter"
                    clearable @change="handleFilter"></el-input>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="beginTime"
            type="date"
            value-format="yyyy-MM-dd"
            @change="handleFilter"
            placeholder="选择日期" clearable>
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-input v-model="listQuery.interviewerName" placeholder="面试官昵称" @keyup.enter.native="handleFilter"
                    clearable @change="handleFilter"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="listQuery.userName" placeholder="面试者姓名" @keyup.enter.native="handleFilter"
                    clearable @change="handleFilter"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">查询</el-button>
        </el-form-item>

        <el-form-item>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-button type="success" @click="" icon="el-icon-plus">添加</el-button>
    </div>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="handleRouteList"
    />

    <el-table
      :key="tableKey"
      v-loading="listLoading"
      :data="list"
      border
      highlight-current-row
      style="margin-top: 10px"
    >
      <el-table-column label="面试ID" prop="id" width="80"></el-table-column>
      <el-table-column label="面试时间" prop="beginTime" min-width="150">
        <template slot-scope="scope">
          <div>
            {{scope.row.beginTime | timestampToDateTime}}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="面试官昵称" prop="interviewerName"></el-table-column>
      <el-table-column label="面试者姓名" prop="userName"></el-table-column>
      <!--      <el-table-column label="面试者评分与反馈" prop="phone"></el-table-column>-->
      <!--      <el-table-column label="状态" prop="status">-->
      <!--        <template slot-scope="row">-->
      <!--          <el-tag>{{userStatus[row.status]}}</el-tag>-->
      <!--        </template>-->
      <!--      </el-table-column>-->
      <el-table-column label="订单号ID" prop="orderId"></el-table-column>
      <el-table-column label="付款金额" prop="amount"></el-table-column>
      <el-table-column label="面试状态" prop="status">
        <template slot-scope="scope">
        <span>{{userStatus[scope.row.status]}}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230">
        <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="onCancel(scope.row)" style="border-bottom: 1px solid #409eff;margin-right: 20px;" v-if="scope.row.status<=2">取消订单</el-link>
          <el-link type="primary" :underline="false" style="border-bottom: 1px solid #409eff;" v-if="scope.row.status!==6&&scope.row.status!==1" @click="onRefund(scope.row)">退费</el-link>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="handleRouteList"
    />
  </div>
</template>

<script>
  import waves from '@/directive/waves' // waves directive
  import Pagination from '@/components/Pagination' // secondary package based on el-pagination
  import { getInterviewRecordList } from '@/api/mock_api'

  export default {
    name: 'InterviewRecords',
    components: { Pagination },
    directives: { waves },
    data() {
      return {
        tableKey: 0,
        list: [],
        total: 0,
        listLoading: true,
        listQuery: {
          orderId: undefined,
          beginTime: undefined,
          userName: undefined,
          interviewerName: undefined,
          page: 1,
          limit: 20
        },
        beginTime: undefined,
        userStatus: {
          1: '待支付',
          2: '待面试',
          3: '面试中',
          4: '已面试',
          5: '已关闭',
          6: '已退款'
        }
      }
    },
    created() {
      this.getList()
    },
    watch: {
      $route() {
        this.getList()
      }
    },
    methods: {
      getList() {
        this.listLoading = true
        const query = this.$route.query
        Object.keys(query).forEach(key => {
          this.listQuery[key] = query[key]
        })
        if (query.page) {
          this.listQuery.page = parseInt(query.page)
        }
        if (query.limit) {
          this.listQuery.limit = parseInt(query.limit)
        }
        getInterviewRecordList(this.listQuery).then(response => {
          console.log(this.listQuery)
          const { total, list } = response.data
          this.list = list
          console.log(111111111)
          console.log(this.list)
          this.total = total
          this.listLoading = false
        })

        // this.$axios.post("/mock/interview/time", {this.listQuery}).then(data => {
        //   let event = data.data;
        //   this.calendarOptions.events.push({
        //     id: event.id,
        //     interviewerId: event.id,
        //     start: parseInt(event.beginTime),
        //     end: parseInt(event.endTime),
        //     borderColor: '#D3F261', // 块边框颜色
        //     backgroundColor: '#D3F261', // 块背景色
        //   });
        //   this.beginTime = undefined;
        //   this.endTime = undefined;
        //   this.dialogVisible = false;
        // })
      },
      handleRouteList() {
        this.$router.push({ path: this.$route.path, query: this.listQuery })
      },
      handleFilter() {
        this.listQuery.page = 1
        this.getList()
      },
      // 重置
      handleReset(){
        this.listQuery={orderId: undefined,
          beginTime: undefined,
          userName: undefined,
          interviewerName: undefined,  page: 1,
          limit: 20}
        this.getList()
      },
      //取消订单
      onCancel(row){
        console.log(row)
        this.$axios.patch(`/pay/goods/order/cancel/${row.orderId}`).then(()=>{
          this.$message('已取消订单')
          this.getList()
        })
      },
      onRefund(row){
        console.log(row)
        this.$axios.patch(`mock/interviewer/income/refund/${row.id}`).then((data)=>{
          console.log(22222222)
          console.log(data)
          this.$message('已退费')
          this.getList()
        })
      }

    }
  }
</script>

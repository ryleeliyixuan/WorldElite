<template>
  <div class="app-container">
    <div class="login-box">
      <div class="section1"></div>
      <div class="section2 text-center" v-loading="loading">
        <div class="section2-header">填写求职意向，打造职业档案</div>
        <div class="section2-input">
          <el-input
            class="section-input-box"
            v-model="form.expectPosition"
            placeholder="请输入您的意向职位"
          ></el-input>
          <el-select
            class="section-input-box"
            v-model="form.expectWorkType"
            placeholder="意向求职类型"
          >
            <el-option
              v-for="item in workTypeOptions"
              :key="item.value"
              :label="item.name"
              :value="item.name"
            >
            </el-option>
          </el-select>
          <!-- <el-select
            class="section-input-box"
            v-model="form.expectCity"
            multiple
            placeholder="意向工作城市"
            @change="citySelectChange()"
          >
            <el-option
              v-for="item in cityOptions"
              :key="item.value"
              :label="item.name"
              :value="item.name"
            >
            </el-option>
          </el-select> -->
          <el-cascader
            class="section-input-box"
            placeholder="意向工作城市"
            :show-all-levels="true"
            :options="cityOptions"
            :props="cityIdProps"
            clearable
            v-model="form.expectCity"
          >
          </el-cascader>
          <el-select
            class="section-input-box"
            v-model="form.industry"
            placeholder="意向求职行业"
          >
            <el-option
              v-for="item in industryOptions"
              :key="item.value"
              :label="item.name"
              :value="item.name"
            >
            </el-option>
          </el-select>
          <el-select
            class="section-input-box"
            v-model="salaryIds"
            multiple
            placeholder="薪资范围"
          >
            <el-option
              v-for="item in salaryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
          <!-- <el-checkbox class="section-input-check" v-model="subscribeFlag"
            >订阅职位邮件消息</el-checkbox
          > -->
        </div>
        <div class="section2-button">
          <el-button @click="onPrevPage">上一步</el-button>
          <el-button class="next" @click="onSubmitForm">下一步</el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { saveUserExpectJob, getMyInfo } from "@/api/user_api";
import { getResumeInfo, saveResumeBasic } from "@/api/resume_api";
import { listByType } from "@/api/dict_api";
import Toast from "@/utils/toast";

export default {
  name: "RegisterJobOriPage",

  data() {
    return {
      loading: false,
      subscribeFlag: "",
      form: {
        resumeId: undefined,
        expectPosition: "",
        expectCity: "",
        expectWorkType: "",
        industry: "",
        salaryId: "",
      },
      salaryIds: [],
      subscribeForm: {
        email: "",
        subscribeFlag: 1,
      },
      cityOptions: [],
      // 城市选择框属性
      cityIdProps: {
        multiple: false,
        lazy: false,
        emitPath: false,
        checkStrictly: false,
        expandTrigger: "hover",
        value: "name",
        label: "name",
        children: "children",
      },
      industryOptions: [],
      salaryOptions: [],
      workTypeOptions: [
        {
          value: "1",
          name: "全职",
        },
        {
          value: "2",
          name: "实习",
        },
        {
          value: "3",
          name: "兼职",
        },
      ],
    };
  },
  created() {
    this.initData();
    this.$emit("complete");
  },
  methods: {
    initData() {
      console.log("[[[[[", this.$store.state.user.userId);
      this.loading = true;
      listByType(6).then(
        (response) => (this.industryOptions = response.data.list)
      );

      //获取全部城市cityOptions
      this.$axios
        .request({
          url: "/city/list",
          method: "get",
        })
        .then((data) => {
          let municipality = ["北京市", "上海市", "天津市", "重庆市"];
          this.cityOptions = data.data.map((first) => {
            if (first.children) {
              first.children = first.children.map((second) => {
                if (municipality.includes(second.name)) {
                  delete second.children;
                } else if (second.children) {
                  second.children = second.children.map((third) => {
                    delete third.children;
                    return third;
                  });
                }
                return second;
              });
            }
            return first;
          });
          this.cityOptions.push({ name: "不限" });
        });

      //获取salaryOptions
      this.$axios
        .request({
          url: "/dict/list",
          method: "get",
          params: { type: 26, sort: "+id" },
        })
        .then((response) => {
          this.salaryOptions = response.data.list;
        });
      this.getResumeInfo();
      this.loading = false;
    },
    getResumeInfo() {
      getResumeInfo().then((response) => {
        // 新注册账号，没有简历名称的，assign简历名称
        let resume = response.data;
        if (
          resume &&
          resume.length > 0 &&
          (!resume[0].title || (resume[0].title && resume[0].title == ""))
        ) {
          saveResumeBasic({
            userId: this.$store.state.user.userId,
            id: resume[0].id,
            title: "简历1",
          }).then(() => {
            this.getResumeInfo();
          });
        }

        if (
          response.data &&
          response.data[0] &&
          response.data[0].id &&
          response.data[0].id != ""
        ) {
          this.form = response.data[0].userExpectJob;
          this.form.resumeId = response.data[0].id;
          //初始化预期薪资
          //获取salaryId -> salaryList
          let tempSalaryList = response.data[0].userExpectJob.salaryId;
          let salaryList = [];
          tempSalaryList.forEach((item) => {
            this.salaryOptions.forEach((option) => {
              if (option.id == item) {
                salaryList.push(option.id);
              }
            });
          });
          this.salaryIds = salaryList;
        }
      });
    },
    onSubmitForm() {
      this.loading = true;
      if (this.form.expectCity && this.form.expectCity.length > 0) {
        let temp = this.form.expectCity.toString();
        this.form.expectCity = temp;
      }
      this.form.categoryId = 122;
      this.form.salaryId = this.salaryIds;
      if (this.form.salaryId && this.form.salaryId.length > 0) {
        let temp = this.form.salaryId.toString();
        this.form.salaryId = temp;
      }
      saveUserExpectJob(this.form).then(() => {
        this.$router.push({ path: "/register-recommendation" });
        this.loading = false;
      });
    },
    onPrevPage() {
      this.$router.push({ path: "/register-basic" });
    },
    // citySelectChange() {
    //   //没有选不限，直接return
    //   let pos = this.form.expectCity.indexOf("不限");
    //   if (pos == -1) {
    //     return;
    //   }
    //   //选了不限，不限为最后一个
    //   if (pos == this.form.expectCity.length - 1) {
    //     let tempList = [];
    //     tempList.push("不限");
    //     this.form.expectCity = tempList;
    //   } else if (pos == 0) {
    //     //不限为第一个
    //     let city = this.form.expectCity[1];
    //     let tempList = [];
    //     tempList.push(city);
    //     this.form.expectCity = tempList;
    //   }
    // },
  },
};
</script>

<style scoped lang="scss">
/deep/ .el-button {
  border: 0px;
}

.app-container {
  max-width: 1200px;
  .login-box {
    height: 655px;
    box-shadow: 0px 4px 16px 3px rgba(191, 199, 215, 0.31);
    border-radius: 24px;
    display: flex;
    background: #f6f9fc;
    margin-top: 35px;
    margin-bottom: 88px;

    .section1 {
      background: url("../assets/login-background.svg") repeat;
      flex: 1;
    }
    .section2 {
      flex: 1;
      background: #ffffff;
      border-top-right-radius: 24px;
      border-bottom-right-radius: 24px;

      .section2-header {
        margin-top: 93px;
        margin-bottom: 40px;
        font-size: 24px;
        font-family: PingFangSC-Medium, PingFang SC;
        font-weight: 500;
        color: #333333;
        line-height: 33px;
      }

      .section2-input {
        display: flex;
        flex-direction: column;
        width: 312px;
        margin: auto;

        .section-input-box {
          margin-bottom: 22px;
        }

        .section-input-box:last-of-type {
          margin-bottom: 13px;
        }

        .section-input-check {
          display: flex;
          align-items: center;
          font-size: 14px;
          font-family: PingFangSC-Regular, PingFang SC;
          font-weight: 400;
          color: #4895ef;
          line-height: 20px;

          /deep/.el-checkbox__inner {
            border: 1px solid #4895ef;
          }
        }

        /deep/ .el-input__inner {
          height: 35px;
          background: #ffffff;
          border-radius: 20px;
          border: 1px solid #cccccc;
        }

        /deep/.el-input__inner:focus {
          border: 1px solid #4895ef;
        }
      }

      .section2-button {
        display: flex;
        justify-content: center;
        margin-top: 60px;

        /deep/.el-button {
          width: 151px;
          height: 35px;
          background: #eceff1;
          border-radius: 18px;
          font-size: 16px;
          font-family: PingFangSC-Medium, PingFang SC;
          font-weight: 500;
          color: #666666;
          line-height: 22px;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .next {
          background: #4895ef;
          font-size: 16px;
          font-family: PingFangSC-Medium, PingFang SC;
          font-weight: 500;
          color: #ffffff;
          line-height: 22px;
        }
      }
    }
  }
}

@media screen and (max-width: 850px) {
  .app-container {
    padding: 20px 20px 0;

    .login-box {
      .section1 {
        display: none;
      }

      .section2 {
        border-radius: 24px;
      }
    }

    .text-center {
      font-size: 18px;
    }

    .register-form {
      width: 300px;

      .item-container {
        margin-top: 20px;
      }
    }
  }
}

@media screen and (max-width: 450px) {
  /deep/.el-form-item {
    flex-direction: column !important;
  }

  .app-container {
    padding: 20px 20px 0;

    .login-box {
      .section1 {
        display: none;
      }

      .section2 {
        border-radius: 24px;
      }
    }

    .text-center {
      font-size: 18px;
    }

    .text-center1 {
      font-size: 14px;
    }
  }
}
</style>

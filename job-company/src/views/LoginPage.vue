<template>
  <div class="app-container">
    <h3 class="text-center">欢迎回来</h3>
    <p class="text-center">登录{{$t('app_name')}}，马上开始招聘。</p>
    <b-container>
      <b-row align-h="center">
        <b-form @submit="onSubmit" class="register-form">
          <b-input-group class="mt-4">
            <b-form-input size="lg" v-model="form.email" type="email" required placeholder="邮箱地址"></b-form-input>
          </b-input-group>

          <b-input-group class="mt-4">
            <b-form-input
              size="lg"
              v-model="form.password"
              type="password"
              required
              placeholder="密码"
            ></b-form-input>
          </b-input-group>
           <b-input-group class="mt-4">
            <b-form-checkbox v-model="form.rememberFlag" value="1" unchecked-value="0">保持登录状态</b-form-checkbox>
          </b-input-group>
          <b-button type="submit" size="lg" variant="info" class="mt-4" block>登录</b-button>
        </b-form>
      </b-row>
      <p class="text-center m-4">
        <b-link href="/forget-pwd">忘记密码?</b-link>
      </p>
      <p class="text-center m-4">
        没有帐号？<b-link to="/register">立即加入</b-link>
      </p>
    </b-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      form: {
        email: "",
        password: "",
        rememberFlag: 1
      },
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  methods: {
    onSubmit(evt) {
      evt.preventDefault();
      this.$store.dispatch("user/LOGIN", this.form).then(() => {
        let query = {...this.$route.query};
        delete query.redirect;
        this.$router.push({path: this.redirect || "/", query});
      });
    }
  }
};
</script>

<style scoped>
.register-form {
  width: 400px;
}
.app-container {
  margin-top: 100px;
  min-height: calc(100vh - 430px);
}
</style>

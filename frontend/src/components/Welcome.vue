<template>
  <div>
    <h3>欢迎！<span>{{ userType }}</span> {{ name }}</h3>
  </div>
</template>

<script>


export default {
  data() {
    return {
      isManager: null,
      isVip: null,
      name: null,
      id: null
    };
  },
  async created() {
    // 获取 sessionStorage 中的id(注册用户)
    this.id=sessionStorage.getItem('id');

    //获取用户名（登录用户和注册用户）
    this.name = sessionStorage.getItem('name');
   
    // 判断用户名是否存在
    if (this.id) {
      try {
        const id = this.id;
        const url = `/user/get-one-user/${id}`; // 使用模板字符串拼接 URL
        const { data: res } = await this.$http.get(url); // 发起请求

        console.log(res);
        if (res.message === '查询成功') {
          // 更新 isManager 和 isVip 状态
          this.isManager = res.data.isManager;
          this.isVip = res.data.isVip;
          sessionStorage.setItem('isVip', this.isVip);
        } else {
          console.error('未找到相关用户信息');
        }
      } catch (error) {
        console.error('查询用户信息失败', error);
      }
    }
  },
  computed: {
    // 根据 isManager 和 isVip 计算用户类型
    userType() {
      if (this.isManager === 1) {
        return '管理员';
      } else if (this.isVip === 1) {
        return '会员用户';
      } else {
        return '普通用户';
      }
    }
  }
};
</script>

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
    // 获取 sessionStorage 中的用户名
    this.name = sessionStorage.getItem('name');
    console.log(this.name);

    // 判断用户名是否存在
    if (this.name) {
      try {
        const { data: res } = await this.$http.post('/user/list/1/9999', { name: this.name });
        if (res.data.records && res.data.records.length > 0) {
          // 更新 isManager 和 isVip 状态
          this.isManager = res.data.records[0].isManager;
          this.isVip = res.data.records[0].isVip;
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

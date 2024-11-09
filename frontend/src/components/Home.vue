<template>
  <el-container class="home_container">
    <!--头部-->
    <el-header>
      <div>
        <span>餐厅管理系统</span>
      </div>

      <el-button type="info" @click="logout">退出</el-button>
    </el-header>
    <!--主体-->
    <el-container>
        <el-aside width="auto">
          <div class="toggle-button" @click="toggleCollapse">
            |||
          </div>
          <!--侧边栏-->
          <el-menu
            active-text-color="#409eff"
            background-color="#373D41"
            unique-opened = "true"
            class="el-menu-vertical-demo"
            default-active="2"
            text-color="#fff"
            @open="handleOpen"
            @close="handleClose"
            :collapse="isCollapse"
          >
            <el-sub-menu index="1" v-if="!isManager">
              <template #title>
                <el-icon><Dish /></el-icon>
                <span>前台服务</span>
              </template>
              <el-menu-item-group title="前台服务">
                <el-menu-item index="1-1">
                  <el-icon><location /></el-icon>
                  item one
                </el-menu-item>
                <el-menu-item index="1-2">item two</el-menu-item>
              </el-menu-item-group>
            </el-sub-menu>
            <el-menu-item index="2" v-if="isManager">
              <el-icon><Management /></el-icon>
              <span>后台服务</span>
            </el-menu-item>
            <el-menu-item index="3" v-if="isManager">
              <el-icon><document /></el-icon>
              <span>结账报表</span>
            </el-menu-item>
            <el-sub-menu index="4">
              <template #title>
                <el-icon><setting /></el-icon>
                <span>系统安全</span>
              </template>
              <el-menu-item index="4-1" v-if="isManager">
                <el-icon><avatar /></el-icon>
                  用户管理
              </el-menu-item>
              <el-menu-item index="4-2">
                <el-icon><key /></el-icon>
                  修改密码
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>
      <el-container>
        <el-main>主界面</el-main>
      </el-container>
    </el-container>
  </el-container>
</template>
<script>


export default{
  data(){
    return{
      isCollapse: false,
      isManager: null
    }
  },
  async created() {
    const name = sessionStorage.getItem('name');
    const { data: res } = await this.$http.post('/user/list/1/9999', {name});
    this.isManager = res.data.records[0]["isManager"];
    console.log(res.data.records[0]);
  },
  methods:{
    logout(){
      window.sessionStorage.clear()
      this.$router.push('/login')
    },
    toggleCollapse(){
      this.isCollapse = !this.isCollapse
    }
  }
}
</script>
<style lang="less" scoped>
  .home_container{
    height: 100%
  }
  .el-header{
    background-color: #373D41;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #fff;
    font-size: 20px;
    >div{
      display: flex;
      align-items: center;
    }
  }
  .el-aside{
    background-color: #373D41;
    .el-menu{
      border-right: 0;
    }
  }
  .el-main{
    background-color: #EAEDF1
  }
  .toggle-button{
    font-size: 10px;
    line-height: 24px;
    background-color: #4a5064;
    color:#fff;
    text-align: center;
    letter-spacing: 0.2em;
    cursor: pointer;
  }
</style>

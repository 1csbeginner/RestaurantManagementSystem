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
            @select="saveState"
            :collapse="isCollapse"
            :router="true"
            :default-active="activePath"
          >
            <!--只有用户可见1和4的部分功能。管理员可见234。(通过调整v-if)-->
            <el-sub-menu index="1" v-if="!isManager">
              <template #title>
                <el-icon><KnifeFork /></el-icon>
                <span>前台服务</span>
              </template>
              <el-menu-item index="/order">
                <el-icon><food /></el-icon>
                点菜
              </el-menu-item>
              <el-menu-item index="/pay" >
                <el-icon><ShoppingCart /></el-icon>
                结账
              </el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="2" v-if="isManager">
              <template #title>
                <el-icon><Collection /></el-icon>
                <span>后台服务</span>
              </template>
              <el-menu-item index="/table" >
                <el-icon><Finished/></el-icon>
                  上菜管理
              </el-menu-item>
              <el-menu-item index="/report" >
                <el-icon><OfficeBuilding /></el-icon>
                  订单管理
              </el-menu-item>
              <el-menu-item index="/dish" >
                <el-icon><Dish /></el-icon>
                  菜品管理
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item index="/data" v-if="isManager" >
              <el-icon><DataAnalysis /></el-icon>
              <span>结账报表</span>
            </el-menu-item>
            <el-sub-menu index="4">
              <template #title>
                <el-icon><setting /></el-icon>
                <span>系统安全</span>
              </template>
              <el-menu-item index="/users" v-if="isManager" >
                <el-icon><avatar /></el-icon>
                  用户管理
              </el-menu-item>
              <el-menu-item index="/info" >
                <el-icon><key /></el-icon>
                  修改信息
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </el-aside>
      <el-container>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>
<script>
import router from '@/router';

export default{
  data(){
    return{
      isCollapse: false,
      isManager: null,
      activePath: sessionStorage.getItem('activePath') || this.$route.path,
    }
  },
  async created() {
    let id = sessionStorage.getItem('id'); // 获取 id
    const name = sessionStorage.getItem('name'); // 获取用户名
    if(!id){
      const { data: res } = await this.$http.post('/user/list/1/9999', { name: name });
      if (res.data.records.length != 0) {
        id = res.data.records[0].id;
        window.sessionStorage.setItem('id', id);
      }
    }
    const url = `/user/get-one-user/${id}`; // 使用模板字符串拼接 URL
    const { data: res } = await this.$http.get(url); // 发起请求

    this.isManager = res.data.isManager
    this.activePath = sessionStorage.getItem('activePath')
  },
  watch:{
    '$route.path'(newPath){
      this.activePath = newPath;
      sessionStorage.setItem('activePath', newPath);
    }
  },
  methods:{
    logout() {
      // 获取当前桌号
      const deleteTable = sessionStorage.getItem('table');

      // 删除订单历史
      const allHistory = JSON.parse(localStorage.getItem('history') || '{}'); // 确保结构为对象
      if (allHistory[deleteTable]) {
        delete allHistory[deleteTable]; // 删除对应桌号的历史数据
        localStorage.setItem('history', JSON.stringify(allHistory)); // 保存更新后的历史
        console.log(`已删除桌号 ${deleteTable} 的历史记录`);
      } else {
        console.warn(`历史记录中未找到桌号 ${deleteTable}`);
      }

      // 清理 sessionStorage
      sessionStorage.clear();

      // 跳转到登录页面
      this.$router.push('/login');
    },


    toggleCollapse(){
      this.isCollapse = !this.isCollapse
    },
    //保存链接的激活状态
    saveState(activePath){
      this.activePath = activePath
      sessionStorage.setItem('activePath', activePath);
    }
  },
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

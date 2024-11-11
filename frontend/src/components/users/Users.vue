<template>
  <!--面包屑导航区域（点击标签返回到指定页面）（。。竟然叫面包屑？？）-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>系统安全</el-breadcrumb-item>
    <el-breadcrumb-item>用户管理</el-breadcrumb-item>
  </el-breadcrumb>
  <!--卡片视图-->
  <el-card >

    <el-row :gutter="20">
      <el-col :span="7">
      <!--搜索-->
        <el-input placeholder="请输入用户名搜索"
          v-model="queryInfo.query"
          clearable
          @clear="getUserList"
        >
          <template #append>
            <el-button :icon="Search" @click="getUserList"/>
          </template>
        </el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary">添加用户</el-button>
      </el-col>
    </el-row>
    <!--用户列表-->
    <el-table :data="userList" stripe>
      <el-table-column prop="id" label="id"></el-table-column>
      <el-table-column prop="name" label="用户名"></el-table-column>
      <el-table-column prop="isVip" label="会员"></el-table-column>
      <!--修改用户（需要提供用户id）-->
      <el-table-column prop="isManager" label="管理员">
        <template v-slot="scope">
          <!-- 使用 el-switch 控件，绑定 isManager -->
          <el-switch v-model="scope.row.isManager" @change="userChange(scope.row)"/>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button type="danger" :icon="Delete"/>
        </template>
      </el-table-column>
    </el-table>
    <!--分页-->
    <el-pagination
      :page-sizes="[1, 5, 10, 15, 20]"
      :current-page="queryInfo.pagenum"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </el-card>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue'; // 导入 Vue 的功能
import { ArrowRight, Search, Delete, Edit} from '@element-plus/icons-vue'; // 导入图标
import { ElMessage } from 'element-plus'; // 导入 ElMessage
import axios from 'axios'; // 导入 axios

// 使用 ref 来定义响应式数据
const queryInfo = ref({
  query: '',
  //当前页码
  pagenum: 1,
  //每页显示多少条
  pagesize: 10
});
const userList = ref([]); // 定义用户列表
const total = ref(0);

const getUserList = async () => {
// 定义获取用户列表的方法
  const queryParam = {
    name: queryInfo.value.query  // 将查询条件直接作为用户名字段传递
  };
  const { data: res } = await axios.post(`/user/list/${queryInfo.value.pagenum}/${queryInfo.value.pagesize}`,
  queryParam);

  userList.value = res.data.records; // 将用户列表数据赋值给 userList
  total.value= res.data.total; // 将用户总数赋值给 total

  userList.value = userList.value.map(item => {
    return {
      ...item,
      isManager: item.isManager ? true : false,
      isVip: item.isVip ? '是' : '否',
    };
  });
};
//一页多少条
const handleSizeChange = (newSize) => {
  queryInfo.value.pagesize = newSize;
  getUserList();
};
//当前页
const handleCurrentChange = (newPage) => {
  queryInfo.value.pagenum = newPage;
  getUserList();
};
const userChange = async (row) => {
  const changeParams = {
    id: row.id,
    isManager: row.isManager ? 1 : 0
  };
  console.log(changeParams);
  const response = await axios.post(`/user/upd-one-user`, changeParams);
  if(response.data.message === "修改用户信息成功！"){
    ElMessage.success('修改用户信息成功！');
  }else{
    row.isManager = !row.isManager;
    ElMessage.error('修改用户信息失败！');
  }
};

// 在组件挂载时调用 getUserList
onMounted(() => {
  getUserList();
});
</script>
<style lang="less" scoped></style>

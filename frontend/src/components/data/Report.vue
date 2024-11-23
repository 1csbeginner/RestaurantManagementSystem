<template>
  <!--面包屑导航区域（点击标签返回到指定页面）（。。竟然叫面包屑？？）-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>后台管理</el-breadcrumb-item>
    <el-breadcrumb-item>订单管理</el-breadcrumb-item>
  </el-breadcrumb>
  <!--卡片视图-->
  <el-card >

    <el-row :gutter="20">
      <el-col :span="7">
      <!--搜索-->
        <el-input placeholder="请输入用户名搜索"
          v-model="queryInfo.query"
          clearable
          @clear="defaultGetTableList"
        >
          <template #prepend>
            <el-select v-model="searchType" placeholder="选择查询方式" style="width: 120px">
             <el-option label="桌号" value="tablenumber" />
             <el-option label="订单ID" value="id" />
            </el-select>
          </template>
          <template #append>
            <el-button :icon="Search" @click="getTableList"/>
          </template>
        </el-input>
      </el-col>
    </el-row>
    <!--用户列表-->
    <el-table :data="tableList" stripe @row-click="openDialog">
      <el-table-column prop="id" label="订单ID"></el-table-column>
      <el-table-column prop="tablenumber" label="桌号"></el-table-column>
      <el-table-column prop="gmtCreate" label="创建时间"></el-table-column>
      <el-table-column prop="price" label="总金额"></el-table-column>
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
  <!--添加用户-->
   <el-dialog
    title="详情"
    v-model="DialogVisible"
    width="750px"
    @close="closeDialog"
  >
    <el-scrollbar style="max-height: 400px;">
      <el-descriptions title="订单详情" border>
        <el-descriptions-item label="用户"">{{ dialogData.user }}</el-descriptions-item>
        <el-descriptions-item label="桌号">{{ dialogData.table }}</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ dialogData.total }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTimestamp(dialogData.timestamp) }}</el-descriptions-item>
      </el-descriptions>

      <h4>菜品列表</h4>
      <el-table :data="dialogData.items" stripe>
        <el-table-column prop="name" label="菜品名称"></el-table-column>
        <el-table-column prop="quantity" label="数量"></el-table-column>
        <el-table-column prop="price" label="单价"></el-table-column>
      </el-table>
    </el-scrollbar>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeDialog">返回</el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue'; // 导入 Vue 的功能
import { ArrowRight, Search} from '@element-plus/icons-vue'; // 导入图标
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
const tableList = ref([]); // 定义用户列表
const total = ref(0);
// 添加用户对话框是否可见
const DialogVisible = ref(false);
const dialogData = ref({}); // 定义对话框数据
const openDialog = async (row) => {
  DialogVisible.value = true;

  try {
    // 从行数据中的 URL 加载 JSON 数据
    const response = await axios.get(row.preview);
    dialogData.value = response.data;
  } catch (error) {
    console.error('加载 JSON 数据失败:', error);
    ElMessage.error('加载失败，请稍后重试！');
  }
};
const closeDialog = () => {
  DialogVisible.value = false;
};
//格式化时间
const formatTimestamp = (timestamp) => {
  if (!timestamp) return "无效时间"; // 如果时间戳为空，返回默认值
  const date = new Date(timestamp);
  if (isNaN(date.getTime())) return "无效时间"; // 检查时间戳是否有效
  return new Intl.DateTimeFormat("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  })
    .format(date)
    .replace(/\//g, "-")
    .replace(",", "");
};
//搜索类型
const searchType = ref('tablenumber');
// 获取通过 ID 查询用户
const getTableById = async () => {
  try {
    const removedTables = JSON.parse(localStorage.getItem('removedTables') || '[]');  // 获取已删除的用户列表

    // 发起 GET 请求，根据 ID 查询用户
    const { data: res } = await axios.get(`/indent/get-one-indent/${queryInfo.value.query}`);

    // 如果返回的 data 不是数组而是单个对象，直接将该对象添加到 tableList 中
    if (res.data) {
      const table = res.data;

      // 过滤已删除的用户
      if (!removedTables.includes(table.id)) {
        tableList.value = [table];  // 将单个用户对象放入数组
      } else {
        tableList.value = [];  // 如果该用户已被删除，则清空列表
      }
      total.value = tableList.value.length;  // 更新用户总数

    }else {
    // 如果没有用户数据，清空列表并提示用户
    tableList.value = [];
    total.value = 0;
    ElMessage.warning('未找到匹配的订单');
  }

  } catch (error) {
    ElMessage.error('订单不存在');
  }
};

//默认方式
const defaultGetTableList = () => {
  queryInfo.value.query = '';
  searchType.value = 'tablenumber';
  getTableList();
};
// 获取通过用户名查询用户列表
const getTableListByName = async () => {
  try {
    const removedTables = JSON.parse(localStorage.getItem('removedTables') || '[]');  // 获取已删除的用户列表

    const queryParam = {
      tablenumber: queryInfo.value.query,  // 查询条件为用户名
    };

    // 发起 POST 请求，根据用户名查询用户列表
    const { data: res } = await axios.post(
      `/indent/list/${queryInfo.value.pagenum}/${queryInfo.value.pagesize}`,
      queryParam
    );

    // 过滤已删除的用户
    const filteredTables = res.data.records.filter(table => !removedTables.includes(table.id));

    // 更新用户列表和总数
    tableList.value = filteredTables;
    total.value = res.data.total;

  } catch (error) {
    console.error('获取订单列表失败', error);
  }
};

// 查询用户列表，根据 searchType 来决定查询方式
const getTableList = async () => {
  if (searchType.value === 'id') {
    await getTableById();  // 通过 ID 查询
  } else {
    await getTableListByName();  // 通过用户名查询
  }
};


//一页多少条
const handleSizeChange = (newSize) => {
  queryInfo.value.pagesize = newSize;
  getTableList();
};
const currentName = ref(sessionStorage.getItem("name"));
//当前页
const handleCurrentChange = (newPage) => {
  queryInfo.value.pagenum = newPage;
  getTableList();
};
//删除用户提示
import { ElMessageBox } from 'element-plus'
// 在组件挂载时调用 getTableList
onMounted(() => {
  getTableList();
});
</script>

<template>
  <!-- 面包屑导航 -->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>后台管理</el-breadcrumb-item>
    <el-breadcrumb-item>上菜管理</el-breadcrumb-item>
  </el-breadcrumb>

  <!-- 卡片视图 -->
  <el-card>
    <!-- 订单列表 -->
    <el-table :data="tableList" stripe empty-text="暂无订单！">
      <el-table-column prop="tablenumber" label="桌号"></el-table-column>
      <el-table-column prop="id" label="菜品编号"></el-table-column>
      <el-table-column prop="name" label="菜品"> </el-table-column>
      <el-table-column prop="quantity" label="数量"> </el-table-column>
      <el-table-column prop="price" label="价格"> </el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
        <el-button
          size="mini"
          type="success"
          @click="finishDish(row.tablenumber, row)"
        >上菜</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { ArrowRight } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

interface CartItem {
  id: number;
  name: string;
  price: number;
  quantity: number;
}

const tableList = ref([]);

const getTableList = () => {
  const savedOrder = localStorage.getItem('orders');

  if (savedOrder) {
    const orderData = JSON.parse(savedOrder);

    // 遍历每个桌号的订单
    tableList.value = Object.entries(orderData).flatMap(([table, items]: [string, CartItem[]]) => {
      // 确保 items 是一个数组
      if (!Array.isArray(items)) {
        items = [items];  // 如果不是数组，将其包装成数组
      }

      return items.map((item) => ({
        tablenumber: table,  // 桌号
        id: item.id,  // 菜品编号
        name: item.name,  // 菜品名称
        quantity: item.quantity,  // 数量
        price: item.price,  // 价格
      }));
    });
  } else {
    // 如果没有数据，清空 tableList
    tableList.value = [];
  }
};

const finishDish = (table, dish) => {
  // 从 localStorage 获取orders 和 history
  console.log(dish)
  const orders = JSON.parse(localStorage.getItem('orders') || '{}');
  const historyByTable = JSON.parse(localStorage.getItem('history') || '{}');

  // 确保所有数据结构都已初始化
  if (!orders[table] || !historyByTable[table]) {
    ElMessage.warning(`无法找到桌号 ${table} 的相关数据`);
    return;
  }

  // 删除 orders 中对应的菜品
  orders[table] = orders[table].filter(
    (item) => !(item.id === dish.id && item.name === dish.name && item.price === dish.price)
  );
  localStorage.setItem('orders', JSON.stringify(orders));

  // 更新 history 中对应菜品的状态
  historyByTable[table] = historyByTable[table].map((item) => {
    if (item.id === dish.id && item.name === dish.name && item.price === dish.price) {
      return { ...item, state: true }; // 更新状态为 true
    }
    return item;
  });
  localStorage.setItem('history', JSON.stringify(historyByTable));

  ElMessage.success(`已完成上菜：${dish.name}`);
};

let intervalId: ReturnType<typeof setInterval>;

onMounted(() => {
  getTableList();
  intervalId = setInterval(getTableList, 500);
});
onUnmounted(() => {
  clearInterval(intervalId);
});
</script>
<style lang="less" scoped></style>

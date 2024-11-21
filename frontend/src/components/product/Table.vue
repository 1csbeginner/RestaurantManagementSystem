<template>
  <!-- 面包屑导航 -->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>后台管理</el-breadcrumb-item>
    <el-breadcrumb-item>订单管理</el-breadcrumb-item>
  </el-breadcrumb>

  <!-- 卡片视图 -->
  <el-card>
    <!-- 订单列表 -->
    <el-table :data="tableList" stripe empty-text="暂无订单！">
      <el-table-column prop="tablenumber" label="桌号"></el-table-column>
      <el-table-column label="菜品">
        <!-- 使用 scoped slots 来动态显示菜品 -->
        <template #default="{ row }">
          <div v-for="item in row.items" :key="item.id">
            {{ item.name }} x {{ item.quantity }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="总金额"></el-table-column>
    </el-table>
  </el-card>
</template>
<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { ArrowRight } from '@element-plus/icons-vue';

interface CartItem {
  id: string;
  name: string;
  quantity: number;
  price: number;
}

interface Order {
  tablenumber: string;
  items: CartItem[];
  price: number;
}

const tableList = ref<Order[]>([]);

const getTableList = () => {
  const savedOrder = localStorage.getItem('orders');

  if (savedOrder) {
    const orderData = JSON.parse(savedOrder);

    // 遍历每个桌号的订单
    tableList.value = Object.entries(orderData).map(([table, items]: [string, CartItem[]]) => {
      // 确保 items 是一个数组
      if (!Array.isArray(items)) {
        console.error(`Expected items to be an array, but got:`, items);
        items = [items];  // 如果不是数组，将其包装成数组
      }

      // 计算每个桌号的总金额
      const totalAmount = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

      return {
        tablenumber: table,  // 桌号
        items,  // 菜品列表
        price: totalAmount,  // 总金额
      };
    });
  } else {
    // 如果没有数据，清空 tableList
    tableList.value = [];
  }
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

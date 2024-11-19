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
    <el-table :data="tableList" stripe>
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
import { ref, onMounted } from 'vue';
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

// 获取订单数据
const getTableList = () => {
  const savedOrder = sessionStorage.getItem('order');
  if (savedOrder) {
    const orderData = JSON.parse(savedOrder);
    // 解析订单数据
    tableList.value = Object.entries(orderData).map(([table, items]: [string, CartItem[]]) => {
      const totalAmount = items.reduce((sum, item) => sum + item.price * item.quantity, 0);
      return {
        tablenumber: table,
        items,
        price: totalAmount,
      };
    });
  }
};

onMounted(() => {
  getTableList();
});
</script>
<style lang="less" scoped></style>

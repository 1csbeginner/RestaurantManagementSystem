<template>
  <!--面包屑导航区域-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>前台服务</el-breadcrumb-item>
    <el-breadcrumb-item>结账</el-breadcrumb-item>
  </el-breadcrumb>
  <!--卡片视图-->
  <el-card >
    <el-table :data="Object.values(cart)" style="width: 100%">
      <el-table-column label="菜品" prop="name"></el-table-column>
      <el-table-column label="数量" prop="quantity"></el-table-column>
      <el-table-column label="单价" prop="price"></el-table-column>
      <el-table-column label="总价" :formatter="formatTotalPrice"></el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="danger" size="mini" :disabled="isPaid" @click="removeFromCart(row.id)">移除</el-button>
        </template>
  </el-table-column>
</el-table>
<el-row type="flex" justify="end" class="total">
  <el-col :span="7" style="text-align: right; padding-right: 50px; font-weight: bold;">
    <span>总计: </span>
    <span>{{ totalAmount }}元</span>
  </el-col>
  <el-col :span="2">
    <el-button type="primary" :disabled="isPaid" @click="cancel">继续加菜</el-button>
  </el-col>
  <el-col :span="2">
    <el-button type="primary" :disabled="isPaid || totalAmount === 0" @click="submit">提交订单</el-button>
  </el-col>
  <el-col :span="2">
    <el-button type="primary" :disabled="isPaid || totalAmount === 0" @click="pay" style="margin-left: 20px;">结账</el-button>
  </el-col>
</el-row>
  </el-card>
  <!--添加-->
</template>
<script lang="ts" setup>
import { ref, onMounted, computed, watch } from 'vue'; // 导入 Vue 的功能
import { useRouter } from 'vue-router'; // 导入 useRouter
import { ArrowRight} from '@element-plus/icons-vue'; // 导入图标
import { ElMessage } from 'element-plus'; // 导入 ElMessage
import axios from 'axios'; // 导入 axios

// 使用 ref 来定义响应式数据
const loadCart = () => {
  const cartData = sessionStorage.getItem('cartForm');
  if (cartData) {
    cart.value = JSON.parse(cartData);
  }
};
const cart = ref<Array<{ id: string; name: string; quantity: number; price: number; table:string}>>([]);
const selectedTable = ref(sessionStorage.getItem('table'));
const isVip = ref(sessionStorage.getItem('isVip'));
// 是否支付（保存在 sessionStorage 中）
const isPaid = ref(sessionStorage.getItem('isPaid') === 'true' || false);
const isSubmit = ref(false);
//提交订单（保存在session中）

const submit = () => {
  // 确保 cart 为数组格式
  cart.value = Object.values(cart.value); // 将对象转换为数组

  // 获取当前桌号（用桌号作为标识）
  const currentTable = sessionStorage.getItem("table"); // 获取当前桌号

  // 从 localStorage 获取所有用户的订单
  const allOrders = JSON.parse(localStorage.getItem("orders") || "{}"); // 如果没有数据则初始化为空对象

  // 创建或更新当前桌号的订单
  const userOrders = cart.value.map((item) => {
    const { table, ...orderItem } = item;
    return orderItem; // 只需要订单项，不需要桌号信息
  });

  // 将新订单替换掉原来桌号的订单
  allOrders[currentTable] = userOrders;

  // 更新所有用户的订单并保存到 localStorage
  localStorage.setItem("orders", JSON.stringify(allOrders)); // 保存到 localStorage

  ElMessage.success("订单已提交！");
  isSubmit.value = true;

  // 打印结果以便调试
  console.log("Updated Orders:", allOrders);
};

const formatTotalPrice = (row: { quantity: number; price: number }) => {
    if(row.quantity && row.price){
      const totalPrice = row.quantity * row.price;
      return totalPrice.toFixed(2); // 保留两位小数
    }
  }
const totalAmount = computed(() => {
  if(isVip.value === '0'){
    return Object.values(cart.value).reduce((total, item) => total + item.price * item.quantity, 0);
  }else{
    return Object.values(cart.value).reduce((total, item) => total + item.price * item.quantity, 0) * 0.5;
  }
});
const router = useRouter();
const cancel = () => {
  if(isPaid.value){
   sessionStorage.removeItem('cartForm');
  }
  isPaid.value = false;
  router.push('/order');
  sessionStorage.setItem('activePath', '/order');
};
const removeFromCart = (productId) => {
  ElMessageBox.confirm('您确定要删除这个菜品吗？', '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    if (cart.value[productId]) {
      delete cart.value[productId]; // 直接删除键
      saveCart();
      ElMessage.success('菜品已删除！');
    } else {
      ElMessage.error('菜品未找到！');
    }
  }).catch(() => {
    ElMessage.info('删除已取消');
  });
};
const pay = async () => {
  // 1. 构造数据
  if (!isSubmit.value) {
    ElMessage.error('请先提交订单！');
    return;
  }

  // 2. 弹出确认框，确认付款
  ElMessageBox.confirm(
    '确认付款吗？付款后不能再加菜！',
    '支付确认',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
  .then(async () => {
    // 用户点击确认后的逻辑（支付操作）
    const data = {
      table: selectedTable.value, // 桌号
      user: sessionStorage.getItem('name'), // 用户名
      Vip: isVip.value, // 是否会员
      items: Object.values(cart.value).map(item => ({
        id: item.id,
        name: item.name,
        price: item.price,
        quantity: item.quantity,
      })), // 菜品信息
      total: Object.values(cart.value).reduce((total, item) => total + item.price * item.quantity, 0), // 总金额
      timestamp: new Date().toISOString(), // 时间戳
    };

    // 3. 生成 JSON 文件
    const jsonData = JSON.stringify(data, null, 2); // 格式化 JSON
    const blob = new Blob([jsonData], { type: 'application/json' });
    const fileName = `order_${selectedTable.value}_${Date.now()}.json`;
    // 创建 FormData 对象并附加文件
    const formData = new FormData();
    formData.append('file', blob, fileName);

    // 4. 上传文件到后端
    const response = await axios.post('/indent/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data', // 告诉服务器这是一个文件上传请求
      },
    });

    if (response.data.message === '上传成功') {
      const orderData = {
        tablenumber: selectedTable.value,
        preview: response.data.data,
        price: totalAmount.value,
      };
      const { data: res } = await axios.post('/indent/add-one-indent', orderData);
      if (res.message === '新增产品成功！') {
        ElMessage.success('支付成功');
        isPaid.value = true;
        //删除订单
        const allOrders = JSON.parse(localStorage.getItem("orders") || "{}");
        const deleteTable = sessionStorage.getItem("table");
        if (allOrders[deleteTable]) {
          delete allOrders[deleteTable]; // 删除桌号 A1 的订单
          localStorage.setItem("orders", JSON.stringify(allOrders));
        }

        // 释放桌子
        // 获取本地存储的桌子列表
        const tableList = JSON.parse(localStorage.getItem('tableList') || '[]');
        console.log('tableList:', tableList);

        // 获取 sessionStorage 中存储的当前桌号
        const currentTableNumber = sessionStorage.getItem('table');
        console.log('currentTableNumber:', currentTableNumber);

        // 查找当前桌号对应的桌子信息
        const currentTable = tableList.find((table) => table.id === currentTableNumber);
        console.log('currentTable:', currentTable);

        if (currentTable) {
          console.log('currentTable:', currentTable);
          // 修改桌子的状态，例如释放桌子
          currentTable.isOccupied = false;  // 假设桌子有 isOccupied 状态，表示是否被占用
          currentTable.user = null;
        }

        // 更新本地存储中的桌子列表
        localStorage.setItem('tableList', JSON.stringify(tableList));

        // 清除 sessionStorage 中的桌号信息
        sessionStorage.removeItem('table');
        loadCart();

      } else {
        ElMessage.error('支付失败，请稍后再试！');
      }
    } else {
      ElMessage.error('支付失败，请稍后再试！');
    }
  })
  .catch(() => {
    // 用户点击取消或者关闭时执行的逻辑
    ElMessage.info('支付已取消');
  });
};
const saveCart = () => {
  sessionStorage.setItem('cartForm', JSON.stringify(cart.value));
};

//删除用户提示
import { ElMessageBox } from 'element-plus'
// 在组件挂载时调用 getProductList
onMounted(() => {
  loadCart();
});
watch(isPaid, (newVal) => {
  sessionStorage.setItem('isPaid', newVal.toString());
});
</script>
<style lang="less" scoped></style>

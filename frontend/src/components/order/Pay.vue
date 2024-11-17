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
    <el-button type="primary" @click="cancel">继续加菜</el-button>
  </el-col>
  <el-col :span="3">
    <el-button type="primary" :disabled="isPaid || totalAmount === 0" @click="pay" style="margin-left: 40px;">结账</el-button>
  </el-col>
</el-row>
  </el-card>
  <!--添加-->
</template>
<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'; // 导入 Vue 的功能
import { useRouter , onBeforeRouteLeave} from 'vue-router'; // 导入 useRouter
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
const cart = ref<Array<{ id: string; name: string; quantity: number; price: number }>>([]);
const selectedTable = ref(sessionStorage.getItem('table'));
const isVip = ref(sessionStorage.getItem('isVip'));
const isPaid = ref(false);
const formatTotalPrice = (row: { quantity: number; price: number }) => {
    const totalPrice = row.quantity * row.price;
    return totalPrice.toFixed(2); // 保留两位小数
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
  //创建 FormData 对象并附加文件
  const formData = new FormData();
  formData.append('file', blob, fileName);

  // 4. 上传文件到后端
  try {
    const response = await axios.post('/indent/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data', // 告诉服务器这是一个文件上传请求
      },
    });

    if (response.data.message === '上传成功') {
      const orderData = {
        tablenumber: selectedTable.value,
        preview : response.data.data,
        price: totalAmount.value,
      };
      const {data : res} = await axios.post('/indent/add-one-indent', orderData);
      if(res.message === '新增产品成功！'){
        ElMessage.success('订单已成功上传！');
        isPaid.value = true;
        loadCart();
      } else {
        ElMessage.error('支付失败，请稍后再试！');
      }
    } else {
      ElMessage.error('支付失败，请稍后再试！');
    }
  } catch (error) {
    ElMessage.error('上传时出现错误，请检查网络连接！');
    console.error(error);
  }
};
const saveCart = () => {
  sessionStorage.setItem('cartForm', JSON.stringify(cart.value));
};
// 使用路由守卫控制跳转时是否清空购物车
onBeforeRouteLeave((to, from, next) => {
  console.log('Before route change', to.path, isPaid.value); // 打印调试信息
  if (to.path === '/order' && isPaid.value === true) {
    // 如果是跳转到 /order 页面且支付，清空购物车
    sessionStorage.removeItem('cartForm');  // 清空购物车数据（如果存在于 localStorage）
    cart.value = [];  // 清空购物车数据
  }
  next();  // 确保路由继续跳转
});

// 你的路由跳转方法
const goToOrderPage = () => {
  router.push({ name: '/order' });  // 跳转到订单页面
};
//删除用户提示
import { ElMessageBox } from 'element-plus'
// 在组件挂载时调用 getProductList
onMounted(() => {
  loadCart();
});
</script>
<style lang="less" scoped></style>

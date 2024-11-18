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
    <el-button type="primary" @click="openhistoryDialog">订单记录</el-button>
  </el-col>
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
<el-dialog
  title="历史记录"
  v-model="historyDialogVisible"
  width="750px"
  @close="closeHistoryDialog"
>
  <el-table :data="Object.values(history)" style="width: 100%">
    <el-table-column label="菜品" prop="name"></el-table-column>
    <el-table-column label="数量" prop="quantity"></el-table-column>
    <el-table-column label="单价" prop="price"></el-table-column>
    <el-table-column label="总价" :formatter="formatTotalPrice"></el-table-column>
  </el-table>
  <el-row type="flex" justify="end" class="total">
    <el-col :span="7" style="text-align: right; padding-right: 50px; font-weight: bold;">
      <el-button type="primary" @click="closeHistoryDialog">关闭</el-button>
    </el-col>
  </el-row>
</el-dialog>

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
const cart = ref<Array<{ id: string; name: string; quantity: number; price: number }>>([]);
const selectedTable = ref(sessionStorage.getItem('table'));
const isVip = ref(sessionStorage.getItem('isVip'));
// 是否支付（保存在 sessionStorage 中）
const isPaid = ref(sessionStorage.getItem('isPaid') === 'true' || false);
//保存历史记录方便用户查询
interface CartItem {
  id: string;
  name: string;
  quantity: number;
  price: number;
}

interface history {
  [key: string]: CartItem;
}
const history = ref<{ [key: string]: { id: string; name: string; price: number; quantity: number } }>({});
const historyDialogVisible = ref(false);
const openhistoryDialog = () => {
  historyDialogVisible.value = true;
  const storedHistory = sessionStorage.getItem('history');
  if (storedHistory) {
    const parsedHistory = JSON.parse(storedHistory);
    console.log('Parsed history from sessionStorage:', parsedHistory);
    history.value = parsedHistory;
    console.log('Updated history.value:', history.value);
  } else {
    history.value = {};
  }
};

const closeHistoryDialog = () => {
  historyDialogVisible.value = false;
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
    const data = {
      table: selectedTable.value, // 桌号
      user: sessionStorage.getItem('name'), // 用户名
      Vip: isVip.value, // 是否会员
      state: false, // 是否支付
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
        ElMessage.success('支付成功');
        isPaid.value = true;
        loadCart();
        //保存历史记录
         // 先从 sessionStorage 中获取当前的历史记录
        let currentHistory = JSON.parse(sessionStorage.getItem('history') || '{}');

        // 将新的菜品项添加到历史记录中
        data.items.forEach(item => {
          if (!currentHistory[item.id]) {
            currentHistory[item.id] = item; // 如果历史记录中没有该菜品，直接添加
          } else {
            currentHistory[item.id].quantity += item.quantity; // 如果已有，更新数量
          }
        });

        // 将更新后的历史记录保存回 sessionStorage
        sessionStorage.setItem('history', JSON.stringify(currentHistory));

        // 更新 Vue 响应式的 history
        history.value = { ...currentHistory }; // 确保响应式更新

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

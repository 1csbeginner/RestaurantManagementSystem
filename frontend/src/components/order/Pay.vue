<template>
  <!--面包屑导航区域-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>前台服务</el-breadcrumb-item>
    <el-breadcrumb-item>结账</el-breadcrumb-item>
  </el-breadcrumb>
  <!--卡片视图-->
  <el-card >
    <el-row>
      <el-col :span="12">
        <span style="font-weight: bold;">
          订单状态：{{ currentOrderStatus }}
        </span>
      </el-col>
    </el-row>
    <el-table :data="Object.values(cart)" style="width: 100%" empty-text="暂无订单！">
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
        <el-button type="primary" :disabled="isPaid || totalAmount === 0 || isFinish[currentTable] === true" @click="submit">提交订单</el-button>
      </el-col>
      <el-col :span="2">
        <el-button type="primary" :disabled="isPaid || totalAmount === 0" @click="pay" style="margin-left: 20px;">结账</el-button>
      </el-col>
    </el-row>
  </el-card>
  <!--添加-->
</template>
<script lang="ts" setup>
import { ref, onMounted, computed, watch, onUnmounted } from 'vue'; // 导入 Vue 的功能
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
const currentTable = ref(sessionStorage.getItem('table'));
const selectedTable = ref(sessionStorage.getItem('table'));
const isVip = ref(sessionStorage.getItem('isVip'));
// 是否支付（保存在 sessionStorage 中）
const isPaid = ref(sessionStorage.getItem('isPaid') === 'true' || false);
const isSubmit = ref(false);
//订单状态
let isFinish = ref(JSON.parse(localStorage.getItem('isFinish') || '{}'));
//提交订单（保存在session中）

const submit = () => {
  // 确保 cart 为数组格式
  cart.value = Object.values(cart.value); // 将对象转换为数组

  // 从 localStorage 获取所有用户的订单
  const allOrders = JSON.parse(localStorage.getItem("orders") || "{}"); // 如果没有数据则初始化为空对象

  // 创建或更新当前桌号的订单
  const userOrders = cart.value.map((item) => {
    const { table, ...orderItem } = item;
    return orderItem; // 只需要订单项，不需要桌号信息
  });

  // 将新订单替换掉原来桌号的订单
  allOrders[currentTable.value] = userOrders;

  // 更新所有用户的订单并保存到 localStorage
  localStorage.setItem("orders", JSON.stringify(allOrders)); // 保存到 localStorage

  // 提取菜品的 id 并按桌号保存到 localStorage
  const dishIdsByTable = JSON.parse(localStorage.getItem("dishIds") || "{}"); // 如果没有数据则初始化为空对象
  const dishIds = userOrders.map((item) => item.id); // 获取当前桌号的所有菜品 ID
  dishIdsByTable[currentTable.value] = dishIds; // 按桌号保存
  localStorage.setItem("dishIds", JSON.stringify(dishIdsByTable)); // 保存到 localStorage

  // 按桌号保存 isFinish 状态
  const isFinishByTable = JSON.parse(localStorage.getItem("isFinish") || "{}"); // 如果没有数据则初始化为空对象

  isFinishByTable[currentTable.value] = false; // 设置当前桌号的 isFinish 状态为 false
  localStorage.setItem("isFinish", JSON.stringify(isFinishByTable)); // 保存到 localStorage

  ElMessage.success("订单已提交！");
  isSubmit.value = true;

  // 打印结果以便调试
  console.log("Updated Orders:", allOrders);
  console.log("Dish IDs by Table:", dishIdsByTable);
  console.log("IsFinish by Table:", isFinishByTable);
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
  // 按桌号保存 isFinish 状态
  const isFinishByTable = JSON.parse(localStorage.getItem("isFinish") || "{}"); // 如果没有数据则初始化为空对象

  isFinishByTable[currentTable.value] = false; // 设置当前桌号的 isFinish 状态为 false
  localStorage.setItem("isFinish", JSON.stringify(isFinishByTable)); // 保存到 localStorage

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
// 收集统计数据
const addForm = async () => {
  const items = await Promise.all(
    Object.values(cart.value).map(async item => {
      try {
        // 获取菜品分类信息
        const response = await axios.get(`product/get-one-product/${item.id}`);
        const sort = response.data.data.sort;  // 获取分类信息
        if (!sort) {
          throw new Error(`分类信息未找到，菜品 ID: ${item.id}`);
        }
        return {
          sort: sort,  // 从后端获取分类信息
          earning: item.price * item.quantity,  // 菜品价格
        };
      } catch (error) {
        console.error(`获取菜品 ${item.id} 的分类失败：`, error);
        return {
          sort: '未知',  // 默认值，避免中断整个流程
          earning: item.price * item.quantity,  // 菜品价格
        };
      }
    })
  );

  const form = {
    items, // 菜品信息
  };

  return form; // 返回表单数据
};
const pay = async () => {
  // 1. 检查订单是否已提交
  if (!isSubmit.value && isFinish[currentTable.value] === false) {
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
    addForm().then(form => {
    console.log('统计表单：', form); // 输出最终表单数据

    // 为每一条信息单独提交 API 请求
    form.items.forEach(item => {
      axios.post('/statistic/add-one-statistic', item)
        .then(() => {
          ElMessage.success('统计数据提交成功');
        })
        .catch(() => {
          ElMessage.error('统计数据提交失败');
        });
    });
  });
    //保存到json文件中
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

        // 删除订单
        const allOrders = JSON.parse(localStorage.getItem('orders') || '{}');
        const deleteTable = sessionStorage.getItem('table');
        if (allOrders[deleteTable]) {
          delete allOrders[deleteTable];
          localStorage.setItem('orders', JSON.stringify(allOrders));
        }

        // 删除对应桌号的 isFinish 状态
        const isFinishByTable = JSON.parse(localStorage.getItem('isFinish') || '{}');
        delete isFinishByTable[deleteTable];
        localStorage.setItem('isFinish', JSON.stringify(isFinishByTable));

        // 删除对应桌号的菜品 ID
        const dishIdsByTable = JSON.parse(localStorage.getItem('dishIds') || '{}');
        delete dishIdsByTable[deleteTable];
        localStorage.setItem('dishIds', JSON.stringify(dishIdsByTable));

        // 释放桌子
        const tableList = JSON.parse(localStorage.getItem('tableList') || '[]');
        const currentTableNumber = sessionStorage.getItem('table');
        const currentTable = tableList.find((table) => table.id === currentTableNumber);

        if (currentTable) {
          currentTable.isOccupied = false; // 假设桌子有 isOccupied 状态，表示是否被占用
          currentTable.user = null;
        }

        localStorage.setItem('tableList', JSON.stringify(tableList));
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
    ElMessage.info('支付已取消');
  });
};
const saveCart = () => {
  sessionStorage.setItem('cartForm', JSON.stringify(cart.value));
};
const currentOrderStatus = computed(() => {
  // 确保 currentTable 有值
  if (!currentTable.value) {
    return '未知'; // 如果桌号不存在，返回“未知”状态
  }

  // 检查 isFinish 中是否存在当前桌号
  if (isFinish.value && isFinish.value[currentTable.value] !== undefined) {
    return isFinish.value[currentTable.value] ? '已完成' : '未完成';
  }

  return '无订单'; // 如果找不到对应桌号的状态，显示“无订单”
});
const loadState = async () => {
  const stateData = await JSON.parse(localStorage.getItem('isFinish') || '{}');
  isFinish.value = stateData;
};
//删除用户提示
import { ElMessageBox } from 'element-plus'
// 在组件挂载时调用 getProductList
let intervalId;

onMounted(() => {
  loadCart();
  loadState();
  intervalId = setInterval(loadState, 500);
});

onUnmounted(() => {
  clearInterval(intervalId);
});
watch(isPaid, (newVal) => {
  sessionStorage.setItem('isPaid', newVal.toString());
});
</script>
<style lang="less" scoped></style>

<template>
  <!--面包屑导航区域-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>前台服务</el-breadcrumb-item>
    <el-breadcrumb-item>结账</el-breadcrumb-item>
  </el-breadcrumb>
  <!--卡片视图-->
  <el-card >
    <span style="font-weight: bold;">{{selectedTable}}桌 已提交订单：</span>
    <p>共计：{{ historyAmount }}元</p>
    <el-table :data="history" style="width: 100%; margin-bottom: 20px;" empty-text="暂无订单！">
      <el-table-column label="菜品" prop="name"></el-table-column>
      <el-table-column label="数量" prop="quantity"></el-table-column>
      <el-table-column label="单价" prop="price"></el-table-column>
      <el-table-column label="总价" :formatter="formatTotalPrice"></el-table-column>
      <el-table-column label="菜品状态" prop="state">
        <template #default="{ row }">
          {{ row.state === true ? '已完成' : '未完成' }}
        </template>
      </el-table-column>
    </el-table>
    <span style="font-weight: bold;">购物车：</span>
    <el-table :data="Object.values(cart)" style="width: 100%; margin-bottom: 40px" empty-text="暂无订单！">
      <el-table-column label="菜品" prop="name"></el-table-column>
      <el-table-column label="数量" prop="quantity"></el-table-column>
      <el-table-column label="单价" prop="price"></el-table-column>
      <el-table-column label="总价" :formatter="formatTotalPrice"></el-table-column>
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="danger" size="mini" @click="removeFromCart(row.id)">移除</el-button>
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
        <el-button type="primary" :disabled="isPaid || totalAmount === 0 || cart.length === 0" @click="submit">提交订单</el-button>
      </el-col>
      <el-col :span="2">
        <el-button type="primary" :disabled="isPaid || historyAmount === 0" @click="pay" style="margin-left: 20px;">结账</el-button>
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
//读取已经提交历史订单
const loadHistory = () => {
  const historyData = JSON.parse(localStorage.getItem('history') || '{}');
  // 加载当前桌号的历史记录，如果不存在则初始化为空数组
  history.value = historyData[currentTable.value] || [];
};
const cart = ref<Array<{ id: string; name: string; quantity: number; price: number; table:string; state:boolean}>>([]);
//保存已提交的历史订单
const history = ref<Array<{ id: string; name: string; quantity: number; price: number; table:string; state:boolean}>>([]);
const currentTable = ref(sessionStorage.getItem('table'));
const selectedTable = ref(sessionStorage.getItem('table'));
const isVip = ref(sessionStorage.getItem('isVip'));
// 是否支付（保存在 sessionStorage 中）
const isPaid = ref(sessionStorage.getItem('isPaid') === 'true' || false);
//提交订单（保存在session中）
const submit = () => {
  // 提交订单前确认
  ElMessageBox.confirm(
    "确定要提交订单吗？提交后无法再进行修改！", // 提示信息
    "确认提交", // 标题
    {
      confirmButtonText: "提交",
      cancelButtonText: "取消",
      type: "warning",
    }
  )
    .then(() => {
      // 确认提交后的逻辑

      // 临时转换 cart 为数组结构
      const cartArray = Object.values(cart.value); // 不修改原始 cart

      // 从 localStorage 获取所有用户的订单
      const allOrders = JSON.parse(localStorage.getItem("orders") || "{}");

      // 将新订单添加到当前桌号订单中
      allOrders[currentTable.value] = allOrders[currentTable.value] || [];
      allOrders[currentTable.value].push(...cartArray); // 添加所有购物车内容

      // 更新所有用户的新订单并保存到 localStorage
      localStorage.setItem("orders", JSON.stringify(allOrders));

      // 获取按桌号保存的 history 数据
      const historyByTable = JSON.parse(localStorage.getItem("history") || "{}");

      // 遍历购物车中的菜品，更新 history 数据
      cartArray.forEach((cartItem) => {
        if (!historyByTable[currentTable.value]) {
          historyByTable[currentTable.value] = [];
        }

        const existingIndex = historyByTable[currentTable.value].findIndex(
          (historyItem) =>
            historyItem.id === cartItem.id &&
            historyItem.name === cartItem.name &&
            historyItem.price === cartItem.price
        );

        if (existingIndex !== -1) {
          historyByTable[currentTable.value][existingIndex].quantity += cartItem.quantity;
        } else {
          historyByTable[currentTable.value].push({ ...cartItem });
        }
      });

      // 保存按桌号的 history 数据到 localStorage
      localStorage.setItem("history", JSON.stringify(historyByTable));

      // 清空购物车
      cart.value = []; // 保持购物车为对象格式
      sessionStorage.removeItem("cartForm");

      ElMessage.success("订单已提交！");
    })
    .catch(() => {
      // 用户取消提交时的逻辑
      ElMessage.info("已取消订单提交。");
    });
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
const historyAmount = computed(() => {
  // 如果当前桌号的历史记录不存在，返回 0
  if (!Array.isArray(history.value)) {
    return 0;
  }
  const isVipDiscount = isVip.value === '1' ? 0.5 : 1; // 判断是否会员，设置折扣系数
  // 遍历当前桌号的所有菜品记录，计算总金额
  return history.value.reduce(
    (total, item) => total + item.price * item.quantity,
    0
  ) * isVipDiscount;
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
// 收集统计数据
const addForm = async () => {
  // 确保 history 和 selectedTable 有效
  if (!selectedTable.value || !history.value) {
    console.error("无效的桌号或历史订单为空");
    return { items: [] }; // 返回空结构
  }

  // 获取当前桌号的历史订单
  const tableHistory = history.value;

  const items = await Promise.all(
    tableHistory.map(async (item) => {
      try {
        // 获取菜品分类信息
        const response = await axios.get(`product/get-one-product/${item.id}`);
        const sort = response.data.data.sort; // 从后端获取分类信息

        if (!sort) {
          throw new Error(`分类信息未找到，菜品 ID: ${item.id}`);
        }

        return {
          sort, // 菜品分类
          earning: item.price * item.quantity, // 收益
        };
      } catch (error) {
        console.error(`获取菜品 ${item.id} 的分类失败：`, error);

        // 如果请求失败，返回默认值
        return {
          sort: '未知', // 默认分类
          earning: item.price * item.quantity, // 收益
        };
      }
    })
  );

  // 组织最终表单数据
  const form = {
    items, // 统计后的菜品信息
  };

  return form; // 返回表单数据
};

const pay = async () => {
  // 1. 检查订单是否已提交
  if (cart.value.length !== 0) {
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
    });
  });
    //保存到json文件中
    const data = {
      table: selectedTable.value, // 桌号
      user: sessionStorage.getItem('name'), // 用户名
      vip: isVip.value, // 是否会员
      items: history.value.map(item => ({
        id: item.id,
        name: item.name,
        price: item.price,
        quantity: item.quantity,
      })), // 菜品信息
      total: history.value.reduce(
      (total, item) => total + item.price * item.quantity,
      0
      ) * (isVip.value === "1" ? 0.5 : 1), // 总金额，会员打折
      timestamp: new Date().toISOString(), // 时间戳
    };
    console.log('订单数据：', data);

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
        price: historyAmount.value,
      };
      const { data: res } = await axios.post('/indent/add-one-indent', orderData);

      if (res.message === '新增产品成功！') {
        ElMessage.success('支付成功');
        isPaid.value = true;

        // 释放桌子
        const tableList = JSON.parse(localStorage.getItem('tableList') || '[]');
        const currentTableNumber = sessionStorage.getItem('table');
        const currentTable = tableList.find((table) => table.id === currentTableNumber);

        if (currentTable) {
          currentTable.isOccupied = false; // 假设桌子有 isOccupied 状态，表示是否被占用
          currentTable.user = null;
        }

        localStorage.setItem('tableList', JSON.stringify(tableList));
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
//删除用户提示
import { ElMessageBox } from 'element-plus'
// 在组件挂载时调用 getProductList
let intervalId;

onMounted(() => {
  loadCart();
  loadHistory();
  intervalId = setInterval(() => {
    loadHistory();
  }, 500);
});

onUnmounted(() => {
  clearInterval(intervalId);
});
watch(isPaid, (newVal) => {
  sessionStorage.setItem('isPaid', newVal.toString());
});
</script>
<style lang="less" scoped></style>

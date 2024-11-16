<template>
  <!--面包屑导航区域-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>前台服务</el-breadcrumb-item>
    <el-breadcrumb-item>点菜</el-breadcrumb-item>
  </el-breadcrumb>
  <!--卡片视图-->
  <el-card >

    <el-row :gutter="20">
      <el-col :span="7">
      <!--搜索-->
        <el-input placeholder="请输入名称搜索"
          v-model="searchQuery"
          @input="handleSearch"
          clearable
          @clear="getProductList"
        >
        <template #append>
          <el-button :icon="Search" @click="getProductList"/>
        </template>
        </el-input>
      </el-col>
      <el-col :span="4">
        <el-button
        type="primary"
        icon="ShoppingCart"
        @click="openCart"
        >
        购物车 ({{ totalItems }})
      </el-button>
      </el-col>
    </el-row>
    <!--用户列表-->
    <el-container style="height: 100vh;margin-top: 10px;">
            <!-- 左侧菜单 -->
        <el-aside width="250px" style="background: #f5f5f5;margin-top: 20px;">
          <el-menu
            :default-active="activeIndex"
            class="el-menu-vertical-demo"
            @select="handleMenuSelect"
          >
            <el-menu-item v-for="item in menuItems" :key="item.index" :index="item.index">
              <template #title>
                {{ item.label }}
              </template>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
            <el-row :gutter="20">
              <!-- 使用 v-for 遍历 productList，每个产品生成一个卡片 -->
              <el-col :span="6" v-for="product in productList" :key="product.id">
                <el-card shadow="hover">
                  <div class="product-card">
                    <!-- 图片展示 -->
                    <el-image
                      v-if="product.picture"
                      :src="product.picture"
                      class="product-img"
                      @click="openModifyDialog(product.id)"
                      alt="product"
                      fit="cover"
                    />
                    <!-- 产品名称 -->
                    <h3 class="product-name">{{ product.name }}</h3>
                    <!-- 价格和会员价 -->
                    <div class="product-prices">
                      <span class="price">¥{{ product.price }}</span>
                    </div>
                    <!-- 动态按钮 -->
                    <div class="product-actions">
                      <template v-if="cart[product.id]">
                        <!-- 已添加商品，显示数量与操作按钮 -->
                        <el-button @click="decreaseQuantity(product.id)" type="danger" size="mini">-</el-button>
                        <span class="quantity" style="padding-left:10px; padding-right: 10px;">{{ cart[product.id].quantity }}</span>
                        <el-button @click="increaseQuantity(product.id)" type="success" size="mini">+</el-button>
                      </template>
                      <template v-else>
                        <!-- 未添加商品，显示添加按钮 -->
                        <el-button @click="addToCart(product.id)" type="primary" size="mini">+</el-button>
                      </template>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
        </el-main>
    </el-container>
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
  <!--添加-->
  <el-dialog
    title="购物车"
    v-model="CartVisible"
    width="750px"
    @close="closeCart"
  >
  <el-table
    :data="Object.values(cart)"
    style="width: 100%"
    height="400"
    border>
    <el-table-column
      prop="name"
      label="商品名称"
      width="180">
    </el-table-column>
    <el-table-column
      prop="price"
      label="价格"
      width="180">
    </el-table-column>
    <el-table-column
      label="数量"
      prop="quantity"
      width="180">
      <template #default="{ row }">
        <el-button @click="decreaseQuantity(row.id)" type="danger" size="mini">-</el-button>
        <span class="quantity" style="padding-left:10px; padding-right: 10px;">{{ row.quantity }}</span>
        <el-button @click="increaseQuantity(row.id)" type="success" size="mini">+</el-button>
      </template>
    </el-table-column>
  </el-table>
    <template #footer>
        <el-button @click="router.push('/pay')" type="primary" v-if="Object.keys(cart).length > 0">结算</el-button>
    </template>
  </el-dialog>
  <!--修改-->
  <el-dialog
  title="详情"
  v-model="modifyDialogVisible"
  width="750px"
  @close="closeModifyDialog"
>
  <el-card shadow="hover">
    <el-row :gutter="20" class="product-card">
      <!-- 图片展示 -->
      <el-col :span="10">
        <el-image
          v-if="productForm.picture"
          :src="productForm.picture"
          class="product-img"
          alt="product"
          fit="contain"
        />
      </el-col>
      <!-- 文字信息 -->
      <el-col :span="14">
        <h3 class="product-name">{{ productForm.name }}</h3>
        <div class="product-prices">
          <span class="price" style="font-weight: bold;">价格: ¥{{ productForm.price }}</span>
          <br />
        </div>
        <span class="product-introduce" style="font-size: 18px;">{{ productForm.introduce }}</span>
        <!-- 操作按钮 -->
        <div class="product-actions">
          <template v-if="cart[productForm.id]">
            <el-button @click="decreaseQuantity(productForm.id)" type="danger" size="mini">-</el-button>
            <span class="quantity" style="padding-left:10px; padding-right: 10px;">{{ cart[productForm.id].quantity }}</span>
            <el-button @click="increaseQuantity(productForm.id)" type="success" size="mini">+</el-button>
          </template>
          <template v-else>
            <el-button @click="addToCart(productForm.id)" type="primary" size="mini">添加到购物车</el-button>
          </template>
        </div>
      </el-col>
    </el-row>
  </el-card>
</el-dialog>

</template>
<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'; // 导入 Vue 的功能
import { ArrowRight, Search, Delete, Edit} from '@element-plus/icons-vue'; // 导入图标
import { ElMessage } from 'element-plus'; // 导入 ElMessage
import axios from 'axios'; // 导入 axios
import router from '@/router';

// 使用 ref 来定义响应式数据
//默认搜索方式
const menuQuery = ref('');
const searchQuery = ref('');
const queryInfo = ref({
  query: '',
  //菜系
  sort: '',
  //当前页码
  pagenum: 1,
  //每页显示多少条
  pagesize: 10
});
const productList = ref([]); // 定义用户列表
const total = ref(0);
//用户表单

const productForm = ref({
  id: '',
  picture:'',
  name: '',
  introduce: '',
  price: '',
  vipPrice: '',
  isDeleted: 0,
  sort: '',
});
const uploadForm = ref({
  picture: ''
});
//购物车
interface CartItem {
  id: string;
  quantity: number;
  price: number;
  name: string;
}

interface Cart {
  [productId: string]: CartItem;
}

const textarea = ref('');
//是否选择了图片
const isUpload= ref(false);
//是否提交了图片
const isSelect = ref(false);
//页面信息
//一页多少条
const handleSizeChange = (newSize) => {
  queryInfo.value.pagesize = newSize;
  getProductList();
};
//当前页
const handleCurrentChange = (newPage) => {
  queryInfo.value.pagenum = newPage;
  getProductList();
};
//树形菜单
const menuItems = ref([
  {
    label: '全部',
    index: '',
  },
  {
    label: '荤菜',
    index: '荤菜',
  },
  {
    label: '素菜',
    index: '素菜',
  },
  {
    label: '凉菜',
    index: '凉菜',
  },
  {
    label: '主食',
    index: '主食',
  },
  {
    label: '甜点',
    index: '甜点',
  },
  {
    label: '汤',
    index: '汤',
  },
]);
const activeIndex = ref(''); // 默认选中的菜单项

const handleMenuSelect = (index) => {
    menuQuery.value = index;
    applyFilters();
    activeIndex.value = index; // 更新选中状态
};
//处理搜索
const handleSearch = () => {
  applyFilters();
};

const applyFilters = () => {
  queryInfo.value.query = searchQuery.value;
  queryInfo.value.sort = menuQuery.value;
  getProductList();
};

// 添加用户对话框是否可见
const CartVisible = ref(false);
const productFormRef = ref(null);
const openCart = () => {
  CartVisible.value = true;
};
const closeCart = () => {

  CartVisible.value = false;
};
//对话框
const modifyDialogVisible = ref(false);
const openModifyDialog = async (id) => {
  console.log(id);
  const { data: res } = await axios.get(`/product/get-one-product/${id}`);
  if(res.message === '查询成功'){
    productForm.value = res.data;
    textarea.value = res.data.introduce;
    uploadForm.value.picture = res.data.picture;

    modifyDialogVisible.value = true;
  }else{
    ElMessage.error('获取产品失败！');
  }
};
const closeModifyDialog = () => {
  productForm.value = {
    id: '',
    picture:'',
    name: '',
    introduce: '',
    price: '',
    vipPrice: '',
    isDeleted: 0,
    sort: '',
  },
  uploadForm.value.picture = '';
  modifyDialogVisible.value = false;
};
//添加用户
const addproduct = async () => {
  try {
    // 校验表单
    await productFormRef.value.validate();
    // 创建一个新的表单数据对象
    const formData = {
      ...productForm.value,
      introduce: textarea.value,  // 合并描述信息
      picture: uploadForm.value.picture,  // 合并图片信息
    };


    // 发送请求到后端
    const { data: res } = await axios.post('/product/add-one-product', formData);

    // 根据返回的消息进行提示
    if (res.message === "新增产品成功！") {
      ElMessage.success('添加菜品成功！');
      //清空
      isSelect.value = false;
      isUpload.value = false;
      productFormRef.value.resetFields();
      closeAddDialog();  // 关闭对话框
      getProductList();  // 更新用户列表
    } else {
      ElMessage.error('添加菜品失败！');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('添加菜品失败！');
  }finally{
    getProductList();
  }
};
//购物车
const cart = ref<Cart>({});

// 添加商品到购物车，初始数量为 1
const addToCart = (productId: string) => {
  const product = productList.value.find(p => p.id === productId);
  if (product && !cart.value[productId]) {
    cart.value[productId] = {
      id: productId,
      quantity: 1,
      price: product.price,
      name: product.name
    };
  }
};

// 增加商品数量
const increaseQuantity = (productId: string) => {
  console.log(productId);
  if (cart.value[productId]) {
    cart.value[productId].quantity += 1;
  }
};

// 减少商品数量，若数量为 0 则移除商品
const decreaseQuantity = (productId: string) => {
  console.log(productId);
  if (cart.value[productId] && cart.value[productId].quantity > 1) {
    cart.value[productId].quantity -= 1;
  } else {
    delete cart.value[productId];
  }
};
//计算购物车中商品总数
const totalItems = computed(() => {
  return Object.values(cart.value).reduce((total, item) => total + item.quantity, 0);
});
// 跳转到购物车页面
const goToCartPage = () => {
  // 这里可以添加跳转到购物车页面的逻辑
  console.log('跳转到购物车页面');
};
const getProductList = async () => {
  // 构造查询参数
  const queryParam = {
    name: queryInfo.value.query,
    sort: queryInfo.value.sort,
  };
  console.log(queryParam);

  try {
    // 调用后端接口，传递分页和查询参数
    const { data: res } = await axios.post(
      `/product/list/${queryInfo.value.pagenum}/${queryInfo.value.pagesize}`,
      queryParam
    );

    // 过滤掉已被删除的商品
    const filteredProducts = res.data.records.filter(product => product.is_deleted !== 1);

    // 更新前端展示的数据
    productList.value = filteredProducts;
    total.value = res.data.total;
  } catch (error) {
    console.error('获取产品列表失败', error);
  }
};

//修改用户信息
const modifyProduct = async (row) => {
  try {
    // 校验表单
    await productFormRef.value.validate();
    // 判断是否提交了图片
    if(isSelect.value && !isUpload.value){
      console.log(isUpload.value);
      ElMessage.error('有图片未提交！请提交图片！');
      return;
    }

    // 创建一个新的表单数据对象
    const formData = {
      ...productForm.value,
    };


    // 发送请求到后端
    const { data: res } = await axios.post('/product/upd-one-product', formData);

    // 根据返回的消息进行提示
    if (res.message === "修改产品信息成功！") {
      ElMessage.success('修改成功！');
      isSelect.value = false;
      isUpload.value = false;
      productFormRef.value.resetFields();
      closeModifyDialog();  // 关闭对话框
      getProductList();  // 更新用户列表
    } else {
      ElMessage.error('修改失败！');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('修改失败！');
  }finally{
    getProductList();
  }
};


// 在组件挂载时调用 getProductList
onMounted(() => {
  getProductList();
});


</script>
<style lang="less" scoped>
    .product-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.product-prices {
  color: #ff4d4f;
  font-size: 16px;
}

.product-actions {
  margin-top: 20px;
}

</style>

<template>
  <!--是因为和元素重名了导致面包屑显示错误。-->
  <!--面包屑导航区域-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>后台服务</el-breadcrumb-item>
    <el-breadcrumb-item>菜品管理</el-breadcrumb-item>
  </el-breadcrumb>
  <!--卡片视图-->
  <el-card >

    <el-row :gutter="20">
      <el-col :span="7">
      <!--搜索-->
        <el-input placeholder="请输入名称搜索"
          v-model="queryInfo.query"
          clearable
          @clear="getproductList"
        >
          <template #prepend>
            <el-select v-model="Select" placeholder="选择" style="width: 80px">
              <el-option label="菜品" value="1" />
              <el-option label="菜系" value="2" />
              <el-option label="id" value="3" />
            </el-select>
          </template>
          <template #append>
            <el-button :icon="Search" @click="getproductList"/>
          </template>
        </el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="openAddDialog">添加菜品</el-button>
      </el-col>
    </el-row>
    <!--用户列表-->
    <el-table :data="productList" stripe>
      <el-table-column prop="id" label="id"></el-table-column>
      <el-table-column label="图片">
        <!-- 使用 scoped-slot 来显示图片 -->
        <template #default="{ row }">
          <el-image
          v-if="row.picture"
          :src="row.picture"
          style="width: 50px; height: 50px; border-radius: 50%;"
          alt="avatar"
          fit="cover"
        />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称"></el-table-column>
      <el-table-column prop="introduce" label="描述"></el-table-column>
      <el-table-column prop="price" label="价格"></el-table-column>
      <el-table-column prop="vipPrice" label="会员价"></el-table-column>
      <el-table-column prop="sort" label="菜系"></el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button type="primary" :icon="Edit"  @click="openModifyDialog(scope.row.id)"/>
          <el-button type="danger" :icon="Delete" @click="remove(scope.row.id)"/>
        </template>
      </el-table-column>
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
  <!--添加-->
  <el-dialog
    title="添加菜品"
    v-model="addDialogVisible"
    width="750px"
    @close="closeAddDialog"
  >
      <el-form
       ref="productFormRef"
       style="width: 500px;"
       :model="productForm"
       :rules="productFormRules"
       label-width="200px"
       status-icon
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="productForm.name" />
        </el-form-item>
        <el-form-item label="定价" prop="price">
          <el-input v-model="productForm.price" />
        </el-form-item>
        <el-form-item label="会员价" prop="vipPrice">
          <el-input  v-model="productForm.vipPrice" />
        </el-form-item>
        <el-form-item label="菜系" prop="sort">
          <el-input  v-model="productForm.sort" />
        </el-form-item>
        <el-form-item label="描述" prop="introduce">
          <el-input
            v-model="textarea"
            maxlength="30"
            style="width: 240px"
            placeholder="请输入描述"
            show-word-limit
            type="textarea"
          />
        </el-form-item>
        <el-form-item label="图片" prop="picture">
          <el-upload
            ref="upload"
            :action="uploadUrl"
            :limit="1"
            :beforeUpload="beforeUpload"
            :auto-upload="false"
            v-model:file-list="fileList"
            :on-exceed="handleExceed"
            @change="handleFileChange"
            style="width: 300px;"
          >
            <!-- 触发选择文件按钮 -->
            <template #trigger>
              <el-button type="primary" style="margin-right: 35px;">选择文件</el-button>
            </template>

            <!-- 上传按钮 -->
            <el-button class="ml-3" type="success" @click="submitUpload">
              上传
            </el-button>
          </el-upload>
        </el-form-item>
        </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeAddDialog">取消</el-button>
        <el-button type="primary" @click="addproduct">
            确定
        </el-button>
      </div>
    </template>
  </el-dialog>
  <el-dialog
    title="修改菜品"
    v-model="modifyDialogVisible"
    width="750px"
    @close="closeModifyDialog"
  >
      <el-form
       ref="productFormRef"
       style="width: 500px;"
       :model="productForm"
       :rules="productFormRules"
       label-width="200px"
       status-icon
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="productForm.name" />
        </el-form-item>
        <el-form-item label="定价" prop="price">
          <el-input v-model="productForm.price" />
        </el-form-item>
        <el-form-item label="会员价" prop="vipPrice">
          <el-input  v-model="productForm.vipPrice" />
        </el-form-item>
        <el-form-item label="菜系" prop="sort">
          <el-input  v-model="productForm.sort" />
        </el-form-item>
        <el-form-item label="描述" prop="introduce">
          <el-input
            v-model="textarea"
            maxlength="30"
            style="width: 240px"
            placeholder="请输入描述"
            show-word-limit
            type="textarea"
          />
        </el-form-item>
        <el-form-item label="图片" prop="picture">
          <el-upload
            ref="upload"
            :action="uploadUrl"
            :limit="1"
            :beforeUpload="beforeUpload"
            :auto-upload="false"
            v-model:file-list="fileList"
            :on-exceed="handleExceed"
            @change="handleFileChange"
          >
            <!-- 触发选择文件按钮 -->
            <template #trigger>
              <el-button type="primary" style="margin-right: 35px;">选择文件</el-button>
            </template>

            <!-- 上传按钮 -->
            <el-button class="ml-3" type="success" @click="submitUpload">
              上传
            </el-button>
          </el-upload>
        </el-form-item>
        </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeModifyDialog">取消</el-button>
        <el-button type="primary" @click="modifyProduct">
            确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue'; // 导入 Vue 的功能
import { ArrowRight, Search, Delete, Edit} from '@element-plus/icons-vue'; // 导入图标
import { ElMessage } from 'element-plus'; // 导入 ElMessage
import axios from 'axios'; // 导入 axios

// 使用 ref 来定义响应式数据
const Select = ref(null);
const queryInfo = ref({
  query: '',
  //当前页码
  pagenum: 1,
  //每页显示多少条
  pagesize: 10
});
const productList = ref([]); // 定义用户列表
const total = ref(0);
// 添加用户对话框是否可见
const addDialogVisible = ref(false);
const productFormRef = ref(null);
const openAddDialog = () => {
  console.log('打开');
  addDialogVisible.value = true;
  console.log(addDialogVisible.value);
};
const closeAddDialog = () => {
  productFormRef.value.resetFields();
  addDialogVisible.value = false;
};
const modifyDialogVisible = ref(false);
const openModifyDialog = async (id) => {
  modifyDialogVisible.value = true;
  const { data: res } = await axios.get(`/product/get-one-product/${ id }`);
  if(res.message==='查询成功'){
    productForm.value = res.data;
    textarea.value = res.data.introduce;
    fileList.value = [{ name: res.data.picture }];
  }
  console.log(modifyDialogVisible.value);
};
const closeModifyDialog = () => {
  productFormRef.value.resetFields();
  modifyDialogVisible.value = false;
};
//添加用户表单
const productForm = ref({
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
const textarea = ref('');
const validateproductName = async (rule, value, callback)=> {
  if (!value) {
    callback(new Error('请输入名称'));
    return;
  }

  try {
    // 调用 getproductList 函数来检查是否有相同用户名
    const exists = await checkproductNameExists(value);
    if (exists) {
      callback(new Error('菜品已存在'));
    } else {
      callback();
    }
  } catch (error) {
    console.error(error);
    callback(new Error('检查名称时出错'));
  }
};
// 检查用户名是否存在
const checkproductNameExists = async (name: string): Promise<boolean> => {
  console.log(name);
  const { data: res } = await axios.post("product/list/1/9999", { name : productForm.value.name });
  // 检查 records 是否存在且有元素
  if (res.data.records && res.data.records.length > 0) {
    return res.data.records[0].name === productForm.value.name;
  } else {
    // 如果没有找到用户，返回 false
    return false;
  }
};
//规则不能是函数。
const productFormRules = ref({
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { validator: validateproductName, trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入定价', trigger: 'blur' }
  ],
  vipPrice: [
    { required: true, message: '请输入会员价', trigger: 'blur' },
  ],
  sort: [
    { required: true, message: '请选择菜系', trigger: 'blur' }
  ]
});
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
      closeAddDialog();  // 关闭对话框
      getproductList();  // 更新用户列表
    } else {
      ElMessage.error('添加菜品失败！');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('添加菜品失败！');
  }finally{
    productFormRef.value.resetFields();
    getproductList();
  }
};
//修改列表
const getproductList = async () => {
  // 定义获取用户列表的方法
  const queryParam = {
    name: queryInfo.value.query  // 将查询条件直接作为用户名字段传递
  };

  try {
    const { data: res } = await axios.post(`/product/list/${queryInfo.value.pagenum}/${queryInfo.value.pagesize}`, queryParam);

    const filteredProducts = res.data.records.filter(product => product.is_deleted !== 1);

    productList.value = filteredProducts;  // 将过滤后的产品列表数据赋值给 productList
    total.value = res.data.total;  // 将总记录数赋值给 total

  } catch (error) {
    console.error('获取产品列表失败', error);
  }
};


//一页多少条
const handleSizeChange = (newSize) => {
  queryInfo.value.pagesize = newSize;
  getproductList();
};
const currentName = ref(sessionStorage.getItem("name"));
//当前页
const handleCurrentChange = (newPage) => {
  queryInfo.value.pagenum = newPage;
  getproductList();
};
//修改用户信息
const productChange = async (row) => {
  const changeParams = {
    id: row.id,
    isManager: row.isManager ? 1 : 0
  };
  console.log(changeParams);
  const response = await axios.post('/product/upd-one-product', changeParams);
  if(response.data.message === "修改用户信息成功！"){
    ElMessage.success('修改用户信息成功！');
  }else{
    row.isManager = !row.isManager;
    ElMessage.error('修改用户信息失败！');
  }
};
//删除用户提示
import { ElMessageBox } from 'element-plus'

const remove = (productId) => {
  ElMessageBox.confirm(
    '确认删除该产品吗？',
    '警告',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      // 假设 productList 是你的产品列表
      const index = productList.value.findIndex(product => product.id === productId);

      if (index !== -1) {
        // 发送删除请求到后台
        axios.post('/product/delete-one-product', { id : productId })
          .then((response) => {
            // 判断后台是否返回删除成功
            if (response.data.message === '删除产品成功！') {
              // 显示成功消息
              ElMessage({
                type: 'success',
                message: '删除成功',
              });
              getproductList();  // 重新获取产品列表
            } else {
              // 删除失败
              ElMessage.error('删除失败');
            }
          })
          .catch((error) => {
            console.error('删除产品失败', error);
            ElMessage.error('删除失败');
          });
      }
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消删除',
      });
    });
};
const uploadUrl = '/product/upload';  // 上传的接口URL
const upload= ref(null);  // 上传组件的引用
const fileList = ref<any[]>([]);
const beforeUpload = (file: File) => {
  const maxSize = 1 * 1024 * 1024;  // 限制为 1MB
  if (file.size > maxSize) {
    ElMessage.error('上传文件大小不能超过 1MB!');
    return false;  // 返回 false 阻止上传
  }
  return true;  // 允许上传
};

// 处理文件选择的变化
const handleFileChange = (file: any, fileList: any[]) => {
  console.log('文件变化:',fileList[0].name);
  fileList.value = [fileList[0]];
};
const handleExceed = (files: File[]) => {
  ElMessage.warning(`只能上传一个文件，当前选择了 ${files.length} 个文件`);
};
// 提交上传的方法
const submitUpload = async (): Promise<void> => {
  // 获取 el-upload 实例
  const uploadInstance = upload as any;

  if (!uploadInstance) {
    ElMessage.error('上传组件未找到');
    return;
  }

  // 获取当前上传的文件
  const file = fileList.value[0].raw;
  console.log('上传文件:', file);
  if (!file) {
    ElMessage.error('请先选择文件');
    return;
  }

  // 创建 FormData 对象
  const formData = new FormData();
  formData.append('file', file);  // 使用 .raw 来获取原始的文件对象


  try {
    // 使用 axios 发送文件上传请求
    const response = await axios.post(uploadUrl, formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    );

    // 上传成功处理
    if (response.data.message === '上传成功') {
      ElMessage.success('文件上传成功');
      uploadForm.value.picture = response.data.data;
    } else {
      ElMessage.error('上传失败');
    }
  } catch (error) {
    ElMessage.error('文件上传失败');
    console.error('上传错误:', error);
  }
};


// 在组件挂载时调用 getproductList
onMounted(() => {
  getproductList();
});
</script>
<style lang="less" scoped></style>

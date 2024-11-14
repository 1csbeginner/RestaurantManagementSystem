<template>
  <!--是因为和元素重名了导致面包屑显示错误。-->
  <!--面包屑导航区域-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>后台服务</el-breadcrumb-item>
    <el-breadcrumb-item>菜系管理</el-breadcrumb-item>
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
        <el-button type="primary" @click="openDialog">添加菜品</el-button>
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
      <el-table-column prop="vip_price" label="会员价"></el-table-column>
      <el-table-column prop="sort" label="菜系"></el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button type="primary" :icon="Edit"  @click="remove(scope.row.id)"/>
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
  <!--添加用户-->
  <el-dialog
    title="添加菜系"
    v-model="addDialogVisible"
    width="750px"
    @close="closeDialog"
  >
      <el-form
       ref="addFormRef"
       style="width: 500px;"
       :model="addForm"
       :rules="addFormRules"
       label-width="200px"
       status-icon
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="addForm.name" />
        </el-form-item>
        <el-form-item label="定价" prop="price">
          <el-input v-model="addForm.price" />
        </el-form-item>
        <el-form-item label="会员价" prop="vip_price">
          <el-input  v-model="addForm.vip_price" />
        </el-form-item>
        <el-form-item label="菜系" prop="sort">
          <el-input  v-model="addForm.sort" />
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
              action="/product/upload"
              list-type="picture-card"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :file-list="fileList"
              :disabled="disabled"
              :before-upload="beforeUpload"
            >
              <el-icon><Plus /></el-icon>

            </el-upload>

            <!-- 图片预览弹框 -->
            <el-dialog :visible.sync="dialogVisible" size="tiny">
              <img alt="image" width="100%" :src="dialogImageUrl" />
            </el-dialog>

            <!-- 修改图片按钮 -->
            <el-button @click="handleModifyImage">修改图片</el-button>
            </el-form-item>
        </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="addproduct">
            确定
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>
<script lang="ts" setup>
import { ref, onMounted } from 'vue'; // 导入 Vue 的功能
import { ArrowRight, Search, Delete, Edit, Download, Plus, ZoomIn} from '@element-plus/icons-vue'; // 导入图标
import { ElMessage, UploadFile } from 'element-plus'; // 导入 ElMessage
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
const addFormRef = ref(null);
const openDialog = () => {
  console.log('打开');
  addDialogVisible.value = true;
  console.log(addDialogVisible.value);
};
const closeDialog = () => {
  addFormRef.value.resetFields();
  addDialogVisible.value = false;
};
//用户表单
const addForm = ref({
  picture:'',
  name: '',
  introduce: '',
  price: '',
  vip_price: '',
  is_deleted: 0,
  sort: '',
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
  const { data: res } = await axios.post("product/list/1/9999", { name : addForm.value.name });
  // 检查 records 是否存在且有元素
  if (res.data.records && res.data.records.length > 0) {
    return res.data.records[0].name === addForm.value.name;
  } else {
    // 如果没有找到用户，返回 false
    return false;
  }
};
//规则不能是函数。
const addFormRules = ref({
  name: [
    { required: true, message: '请输入名称', trigger: 'blur' },
    { validator: validateproductName, trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入定价', trigger: 'blur' }
  ],
  vip_price: [
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
    await addFormRef.value.validate();

    // 创建一个新的表单数据对象
    const formData = {
      ...addForm.value,
      picture: uploadForm.value.picture,  // 合并图片信息
    };


    // 发送请求到后端
    const { data: res } = await axios.post('/product/add-one-product', formData);

    // 根据返回的消息进行提示
    if (res.message === "新增菜品成功！") {
      ElMessage.success('添加用户成功！');
      closeDialog();  // 关闭对话框
      getproductList();  // 更新用户列表
    } else {
      ElMessage.error('添加用户失败！');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('添加用户失败！');
  }
};

const getproductList = async () => {
  // 定义获取用户列表的方法
  const queryParam = {
    name: queryInfo.value.query  // 将查询条件直接作为用户名字段传递
  };

  try {
    const { data: res } = await axios.post(`/product/list/${queryInfo.value.pagenum}/${queryInfo.value.pagesize}`, queryParam);

    // 过滤掉 is_deleted 为 0 的产品
    const filteredProducts = res.data.records.filter(product => product.is_deleted !== 1);

    productList.value = filteredProducts;  // 将过滤后的产品列表数据赋值给 productList
    total.value = filteredProducts.length; // 更新显示的总数

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

//图片操作
const fileList = ref([]) // 用于保存上传的文件列表
const dialogImageUrl = ref('') // 用于保存预览图片的 URL
const dialogVisible = ref(false) // 控制预览弹窗的显示
const disabled = ref(false) // 是否禁用上传
const oldImageUrl = ref('') // 用于保存原来的图片 URL（如果有）
const uploadForm = ref({ file: '' }) // 假设这是你的表单数据


// 图片选择变化时调用
const handleFileChange = (event) => {
  const file = event.target.files[0];  // 获取选中的文件
  if (file) {
    uploadForm.value.picture = file;  // 将文件存储在 uploadForm 中
  }
};

// 图片上传
const uploadFile = async () => {
  // 判断是否选择了文件
  if (!uploadForm.value.picture) {
    ElMessage.error('请先选择一个文件');
    return;
  }

  const formData = new FormData();
  formData.append('file', uploadForm.value.picture);  // 将文件附加到 form-data
  console.log(formData)

  try {
    // 使用 axios 发送文件上传请求
    const response = await axios.post('/product/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data', // 确保上传时是 multipart 格式
      },
    });

    // 上传成功处理
    if (response.data.success) {
      ElMessage.success('文件上传成功');
      console.log('上传成功：', response.data);
    } else {
      ElMessage.error('上传失败');
    }
  } catch (error) {
    console.error('上传错误：', error);
    ElMessage.error('文件上传失败');
  }
};
// 在组件挂载时调用 getproductList
onMounted(() => {
  getproductList();
});
</script>
<style lang="less" scoped></style>

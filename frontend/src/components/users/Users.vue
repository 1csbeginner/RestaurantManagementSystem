<template>
  <!--面包屑导航区域（点击标签返回到指定页面）（。。竟然叫面包屑？？）-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>系统安全</el-breadcrumb-item>
    <el-breadcrumb-item>用户管理</el-breadcrumb-item>
  </el-breadcrumb>
  <!--卡片视图-->
  <el-card >

    <el-row :gutter="20">
      <el-col :span="7">
      <!--搜索-->
        <el-input placeholder="请输入用户名搜索"
          v-model="queryInfo.query"
          clearable
          @clear="getUserList"
        >
          <template #append>
            <el-button :icon="Search" @click="getUserList"/>
          </template>
        </el-input>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="openDialog">添加用户</el-button>
      </el-col>
    </el-row>
    <!--用户列表-->
    <el-table :data="userList" stripe>
      <el-table-column prop="id" label="id"></el-table-column>
      <el-table-column prop="name" label="用户名"></el-table-column>
      <el-table-column prop="isVip" label="会员"></el-table-column>
      <!--修改用户（需要提供用户id）-->
      <el-table-column prop="isManager" label="管理员">
        <template v-slot="scope">
          <!-- 使用 el-switch 控件，绑定 isManager -->
          <el-switch v-model="scope.row.isManager" @change="userChange(scope.row)" :disabled="scope.row.name === currentName"/>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template v-slot="scope">
          <el-button type="danger" :icon="Delete" :disabled="scope.row.name === currentName" @click="remove(scope.row.id)"/>
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
    title="添加用户"
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
        <el-form-item label="用户名" prop="name">
          <el-input v-model="addForm.name" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="addForm.password" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="addForm.confirmPassword" />
        </el-form-item>
        <el-form-item label="是否为会员" prop="isVip">
          <el-radio-group v-model="addForm.isVip">
            <el-radio value="是">是</el-radio>
            <el-radio value="否">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="是否为管理员" prop="isManager">
          <el-radio-group v-model="addForm.isManager">
            <el-radio value="是">是</el-radio>
            <el-radio value="否">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="addUser">
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
const queryInfo = ref({
  query: '',
  //当前页码
  pagenum: 1,
  //每页显示多少条
  pagesize: 10
});
const userList = ref([]); // 定义用户列表
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
  name: '',
  password: '',
  confirmPassword: '',
  isVip: false,
  isManager: false
});
const validateUserName = async (rule, value, callback)=> {
  if (!value) {
    callback(new Error('请输入用户名'));
    return;
  }

  try {
    // 调用 getUserList 函数来检查是否有相同用户名
    const exists = await checkUserNameExists(value);
    if (exists) {
      callback(new Error('用户名已存在'));
    } else {
      callback();
    }
  } catch (error) {
    console.error(error);
    callback(new Error('检查用户名时出错'));
  }
};
// 检查用户名是否存在
const checkUserNameExists = async (name: string): Promise<boolean> => {
  console.log(name);
  const { data: res } = await axios.post("user/list/1/9999", { name : addForm.value.name });
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
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { validator: validateUserName, trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请输入确认密码', trigger: 'blur' },
    { validator: (rule: any, value: string, callback: Function) => {
        if (value !== addForm.value.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  isVip:[
    { required: true, message: '请选择是否为会员', trigger: 'blur' }
  ],
  isManager:[
    { required: true, message: '请选择是否为管理员', trigger: 'blur' }
  ]
});
//添加用户
const addUser = async () => {
  try {
    // 校验表单
    await addFormRef.value.validate();

    // 创建一个新的表单数据对象
    const formData = { ...addForm.value };

    // 删除 confirmPassword 字段，避免传递到后台
    delete formData.confirmPassword;

    // 映射 isVip 和 isManager 为 1 或 0
    const mappedUser = {
      ...formData,
      //可以正常运行但是不知道为什么红了。。
      isVip: formData.isVip === '是' ? 1 : 0,  // 映射 isVip
      isManager: formData.isManager === '是' ? 1 : 0  // 映射 isManager
    };

    console.log(mappedUser);  // 输出修改后的数据以进行调试

    // 发送请求到后端
    const { data: res } = await axios.post('/user/add-one-user', mappedUser);

    // 根据返回的消息进行提示
    if (res.message === "新增用户成功！") {
      ElMessage.success('添加用户成功！');
      closeDialog();  // 关闭对话框
      getUserList();  // 更新用户列表
    } else {
      ElMessage.error('添加用户失败！');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('添加用户失败！');
  }
};

const getUserList = async () => {
  // 定义获取用户列表的方法
  const queryParam = {
    name: queryInfo.value.query,  // 将查询条件直接作为用户名字段传递
  };

  try {
    const { data: res } = await axios.post(
      `/user/list/${queryInfo.value.pagenum}/${queryInfo.value.pagesize}`,
      queryParam
    );

    // 从 localStorage 获取已删除的用户ID列表
    const removedUsers = JSON.parse(localStorage.getItem('removedUsers') || '[]');

    // 过滤掉已删除的用户
    const filteredUsers = res.data.records.filter(user => !removedUsers.includes(user.id));

    // 更新用户列表
    userList.value = filteredUsers;
    total.value = res.data.total; // 将用户总数赋值给 total

    // 映射 isManager 和 isVip 字段
    userList.value = userList.value.map(item => {
      return {
        ...item,
        isManager: item.isManager ? true : false,
        isVip: item.isVip ? '是' : '否',
      };
    });

  } catch (error) {
    console.error('获取用户列表失败', error);
  }
};

//一页多少条
const handleSizeChange = (newSize) => {
  queryInfo.value.pagesize = newSize;
  getUserList();
};
const currentName = ref(sessionStorage.getItem("name"));
//当前页
const handleCurrentChange = (newPage) => {
  queryInfo.value.pagenum = newPage;
  getUserList();
};
const userChange = async (row) => {
  const changeParams = {
    id: row.id,
    isManager: row.isManager ? 1 : 0
  };
  console.log(changeParams);
  const response = await axios.post(`/user/upd-one-user`, changeParams);
  if(response.data.message === "修改用户信息成功！"){
    ElMessage.success('修改用户信息成功！');
  }else{
    row.isManager = !row.isManager;
    ElMessage.error('修改用户信息失败！');
  }
};
//删除用户提示
import { ElMessageBox } from 'element-plus'

const remove = (userId) => {
  ElMessageBox.confirm(
    '确认删除用户吗？',
    '警告',
    {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      // 假设 userList 是你的用户列表
      const index = userList.value.findIndex(user => user.id === userId);

      if (index !== -1) {
        // 删除该条目
        userList.value.splice(index, 1);

        // 存储删除记录到 localStorage
        const removedUsers = JSON.parse(localStorage.getItem('removedUsers') || '[]');
        removedUsers.push(userId);
        localStorage.setItem('removedUsers', JSON.stringify(removedUsers));

        ElMessage({
          type: 'success',
          message: '删除成功',
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
// 在组件挂载时调用 getUserList
onMounted(() => {
  getUserList();
});
</script>
<style lang="less" scoped></style>

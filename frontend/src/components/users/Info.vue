<template>
  <!--面包屑导航区域-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>系统安全</el-breadcrumb-item>
    <el-breadcrumb-item>修改信息</el-breadcrumb-item>
  </el-breadcrumb>

  <!--卡片视图-->
  <el-card>
    <el-row :gutter="20">
      <el-col :span="4">
        <el-form
          ref="modifyFormRef"
          style="width: 500px;"
          :model="modifyForm"
          :rules="modifyFormRules"
          label-width="200px"
          status-icon
        >
          <el-form-item label="用户名" prop="name">
            <el-input v-model="modifyForm.name" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input type="password" v-model="modifyForm.password" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input type="password" v-model="modifyForm.confirmPassword" />
          </el-form-item>
          <el-form-item class="dialog-footer">
            <el-button @click="reset">取消</el-button>
            <el-button type="primary" @click="modifyUser">确定</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </el-card>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'; // 导入 Vue 的功能
import { ArrowRight } from '@element-plus/icons-vue'; // 导入图标
import { ElMessage } from 'element-plus'; // 导入 ElMessage
import axios from 'axios'; // 导入 axios
import { s } from 'vite/dist/node/types.d-aGj9QkWt';
import router from '@/router';

// 使用 ref 来定义响应式数据
const queryInfo = ref({
  query: '',
});
const modifyForm = ref({
  id: Number(sessionStorage.getItem('id')),
  name: sessionStorage.getItem('name'),
  password: sessionStorage.getItem('password'),
  confirmPassword: sessionStorage.getItem('password'),
});
const modifyFormRef = ref(null); // 表单引用

// 检查用户名是否存在
const checkUserNameExists = async (name: string): Promise<boolean> => {
  const { data: res } = await axios.post('user/list/1/9999', { name });
  return res.data.records.length != 0 && res.data.records[0].name === name;
};

// 用户名验证
const validateUserName = async (rule: any, value: string, callback: Function) => {
  if (value && !value.trim() || value === modifyForm.value.name) {  // 如果用户名为空，跳过验证
    callback();
    return;
  }

  try {
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

// 确认密码验证
const confirmPasswordValidator = (rule: any, value: string, callback: Function) => {
  if (value && value !== modifyForm.value.password) {  // 只有当密码有值时才验证
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
};

// 表单验证规则
const modifyFormRules = ref({
  name: [
    { required: false, message: '请输入用户名', trigger: 'blur' },
    { validator: validateUserName, trigger: 'blur' },
  ],
  password: [
    { required: false, message: '请输入密码', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: false, message: '请输入确认密码', trigger: 'blur' },
    { validator: confirmPasswordValidator, trigger: 'blur' },
  ],
});


const modifyUser = async () => {
  try {
    // 校验表单
    await modifyFormRef.value.validate();

    const formData = { ...modifyForm.value };

    // 删除 confirmPassword 字段，避免传递到后台
    delete formData.confirmPassword;

    // 获取用户 ID
    formData.id = Number(sessionStorage.getItem('id'));
    if (formData.id === null) {
      ElMessage.error('用户不存在');
      return;
    }

    // 发送请求到后端修改用户信息
    const { data: res } = await axios.post('/user/upd-one-user', formData);

    if (res.message === '修改用户信息成功！') {
      ElMessage.success('修改成功！');
      sessionStorage.setItem('name', formData.name);
    } else {
      console.error(res.message);
      ElMessage.error('修改失败！');
    }
  } catch (error) {
    console.error(error);
    ElMessage.error('修改失败！');
  }
};

// 重置表单
const reset = () => {
  modifyForm.value = {
    id: Number(sessionStorage.getItem('id')),
    name: sessionStorage.getItem('name'),
    password: sessionStorage.getItem('password'),
    confirmPassword: sessionStorage.getItem('password'),
  };
  router.push('/home');
};
</script>

<style lang="less" scoped></style>

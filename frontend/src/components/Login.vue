<template>
  <div class="login_container">
    <div class="login_box">
      <div class="avatar_box">
        <img src="../assets/logo.svg" alt="logo" />
      </div>
      <!--登录表单-->
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginFormRules"
       class="login_form" label-width="0px">
        <!--用户名-->
        <el-form-item prop="name">
          <el-input v-model="loginForm.name">
            <!--引入图标-->
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <!--密码-->
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password">
          <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <!--登录按钮-->
        <el-form-item class="btns">
          <el-button type="primary" @click="login">登录</el-button>
          <el-button type="info" @click="resetLoginForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script>
import { ElMessage } from 'element-plus';

export default {
  data(){
    return {
      //登录表单数据
      loginForm: {
        name: 'zz',
        password: '123123'
      },
      //验证规则
      loginFormRules: {
        name:[
          {required:true, message:'请输入用户名', trigger:'blur'},
          // {min:3, max:10, message:'长度在3到10个字符', trigger:'blur'}
        ],
        password:[
          {required:true, message:'请输入密码', trigger:'blur'}
        ]
      }
    }
  },
  methods:{
    //重置表单
    resetLoginForm(){
      // console.log(this) $refs有loginFormRef，所以可以调用resetFields方法
      this.$refs.loginFormRef.resetFields()
    },
    login(){
      this.$refs.loginFormRef.validate(async valid => {
        //console.log(valid) 验证成功返回true，验证失败返回false
        if (valid) {
          const {data : res} = await this.$http.post("user/list/1/9999", this.loginForm);
          console.log(res)
          if(res.data.total === 1){
            ElMessage({
              message: '登陆成功',
              type: 'success',
            })
            //保存登录状态
            window.sessionStorage.setItem("isLogin", 'true');
            //跳转到首页
            this.$router.push("/home");
          } else {
            ElMessage.error("登录失败");
          }
        } else {
          return
        }
      })
    }
  }
}
</script>
<style  lang="less" scoped>
  .login_container {
    background-color: #2b4b6b;
    height: 100%;
  }
  .login_box{
    width: 450px;
    height: 300px;
    background-color: #ffff;
    border-radius: 3px;
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
  }
  .avatar_box{
    width: 130px;
    height: 130px;
    border: 1px solid #eee;
    border-radius: 50%;
    padding : 10px;
    box-shadow: 0 0 10px #ddd;
    position:absolute;
    left: 50%;
    transform: translate(-50%, -50%);
    background-color: lightblue;
    img{
      width: 100%;
      height: 100%;
      border-radius: 50%;
    }
  }
  .login_form{
    position: absolute;
    bottom: 0;
    width: 100%;
    padding:0 20px;
    box-sizing: border-box;
  }
  .btns{
    display: flex;
    justify-content: flex-end;
  }
</style>

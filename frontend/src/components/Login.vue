<template>
  <div class="login_container">
    <div class="login_box">
      <div class="avatar_box">
        <img src="../assets/logo.svg" alt="logo" />
      </div>
      <!--登录表单-->
      <el-form v-if="isLoginMode" ref="loginFormRef" :model="loginForm" :rules="loginFormRules"
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
          <el-button type="info" @click="resetForm">重置</el-button>
          <el-button type="primary" link @click="toggleMode">没有账号？立即注册</el-button>
        </el-form-item>
      </el-form>

      <el-form v-else ref="registerFormRef" :model="registerForm" :rules="registerFormRules"
       class="login_form" label-width="0px">
        <!--用户名-->
        <el-form-item prop="name">
          <el-input v-model="registerForm.name" placeholder="用户名">
            <!--引入图标-->
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <!--密码-->
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码">
          <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <!--确认密码-->
        <el-form-item prop="confirmPassword">
         <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码">
         <template #prefix>
             <el-icon><Lock /></el-icon>
           </template>
         </el-input>
        </el-form-item>
        <!--注册按钮-->
        <el-form-item class="btns">
          <el-button type="primary" @click="register">注册</el-button>
          <el-button type="info" @click="resetForm">重置</el-button>
          <el-button type="primary" link @click="toggleMode">已有账号？立即登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>

export default {
  data(){
    return {
      isLoginMode: true,
      //登录表单数据
      loginForm: {
        name: '',
        password: ''
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
      },
      //注册表单数据
      registerForm: {
        name: '',
        password: '',
        confirmPassword: ''
      },
      existUser: false,
      registerFormRules: {
        name:[
          {required:true, message:'请输入用户名', trigger:'blur'},
          {validator:this.validateUserName, trigger:'blur'}
        ],
        password:[
          {required:true, message:'请输入密码', trigger:'blur'}
        ],
        confirmPassword:[
          {required:true, message:'请输入确认密码', trigger:'blur'},
          {validator: (rule, value, callback) => {
              if (value !== this.registerForm.password) {
                callback(new Error('两次输入的密码不一致'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods:{
    async validateUserName(rule, value, callback) {
    if (!value) {
      callback(new Error('请输入用户名'));
      return;
    }
    const { data: res } = await this.$http.post("user/list/1/9999", { name: value });
    if (res.data.total >= 1 && res.data.records[0]["name"] === this.registerForm.name) {
      callback(new Error('用户名已存在'));
    } else {
      callback();
    }
  },
    toggleMode(){
      this.isLoginMode = !this.isLoginMode;
      this.loginForm = { name: '', password: '' };
      this.registerForm = { name: '', password: '', confirmPassword: '' };
      //重置表单
      this.resetForm();
    },
    //重置表单
     resetForm() {
      if (this.isLoginMode) {
        if (this.$refs.loginFormRef) {
          this.$refs.loginFormRef.resetFields();
        }
      } else {
        if (this.$refs.registerFormRef) {
          this.$refs.registerFormRef.resetFields();
        }
      }
    },
    login(){
      this.$refs.loginFormRef.validate(async valid => {
        //console.log(valid) 验证成功返回true，验证失败返回false
        if (valid) {
          const {data : res} = await this.$http.post("user/list/1/9999", this.loginForm);
          console.log(res);
          //同时验证用户名和密码（因为模糊匹配会导致只要用户名正确就可以登录）
          if(res.data.total === 1 && res.data.records[0].password === this.loginForm.password && res.data.records[0].name === this.loginForm.name){
            ElMessage({
              message: '登陆成功',
              type: 'success',
            })
            //保存登录状态
            window.sessionStorage.setItem("id", res.data.records[0].id);
            window.sessionStorage.setItem("name", this.loginForm.name);
            window.sessionStorage.setItem("password", this.loginForm.password);
            window.sessionStorage.setItem("isLogin", 'true');
            //跳转到首页
            this.$router.push("/home");
          } else {
            ElMessage.error("登录失败，用户不存在或密码错误！");
          }
        } else {
          return
        }
      })
    },
    register() {
      this.$refs.registerFormRef.validate(async valid => {
        // 验证成功返回 true，验证失败返回 false
        if (valid) {
          // 只上传用户名和密码
          const userData = {
            name: this.registerForm.name,
            password: this.registerForm.password
          };

          // 发送注册请求
          const { data: res } = await this.$http.post("user/add-one-user", userData);
          console.log(res);

          // 检查后端返回的数据，判断注册是否成功
          if (res.code === '00000') {
            ElMessage({
              message: '注册成功',
              type: 'success'
            });

            // 保存登录状态
            window.sessionStorage.setItem("name", this.registerForm.name);
            window.sessionStorage.setItem("password", this.registerForm.password);
            window.sessionStorage.setItem("isLogin", 'true');

            // 跳转到首页
            this.$router.push("/home");
          } else {
            ElMessage.error("注册失败");
          }
        } else {
          // 验证未通过
          return;
        }
      });
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

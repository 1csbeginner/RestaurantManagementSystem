
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/global.css'

import {ElForm, ElFormItem, ElInput, ElButton, ElMessage} from 'element-plus'
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import axios from 'axios'

const app = createApp(App)
//全局挂载axios(vue3)
app.config.globalProperties.$http = axios
//请求的根路径
axios.defaults.baseURL = 'http://localhost:8877'
for(const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(router)
app.use(ElForm)
app.use(ElFormItem)
app.use(ElInput)
app.use(ElButton)
app.use(ElMessage)

app.mount('#app')

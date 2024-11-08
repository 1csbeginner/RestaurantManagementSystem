import  { createRouter, createWebHistory } from 'vue-router'
import Login from '../components/Login.vue'
import Home from '../components/Home.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path : '', redirect: '/login'},
    {path : '/login', component: Login},
    {path : '/home', component: Home}
  ],
})

//挂载守卫（防止绕过登录直接访问）
router.beforeEach((to, from, next) => {
  /**
   * to: 将要访问的路径
   * from: 从哪个路径跳转而来
   * next: 放行函数
   */
  if(to.path === '/login') return next()
  const loginStr = window.sessionStorage.getItem('isLogin')
  if(!loginStr) return next('/login')
  next()
})
export default router

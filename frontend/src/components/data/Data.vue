<!--从p187开始看教程！！！-->
<template>
  <!--面包屑导航区域-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>结账报表</el-breadcrumb-item>
  </el-breadcrumb>
  <div id="chart" style="width: 100%; height: 400px;"></div>

</template>
<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'; // 导入 Vue 的功能
import { ArrowRight, Search, Delete, Edit} from '@element-plus/icons-vue'; // 导入图标
//导入echarts
import * as echarts from 'echarts';
import { ElMessage } from 'element-plus'; // 导入 ElMessage
import axios from 'axios'; // 导入 axios

// 使用 ref 来定义响应式数据
//渲染完成，加载
onMounted(() => {
  //选择作用域
  const chartDom = document.getElementById('chart')!;
  //初始化
  const myChart = echarts.init(chartDom);

  //配置
  const option = {
    title:{
      text:'销售统计'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend:{
      data:['销售额']
    },
    xAxis:{
      type:'category',
      data:['1月','2月','3月','4月','5月','6月']
    },
    yAxis:{
      type:'value'
    },
    series:[{
      name:'销售额',
      data:[100,200,300,400,500,600],
      type:'bar'
    }]
  };
  //加载配置
  myChart.setOption(option);
});
onUnmounted(() => {
  // 组件卸载时销毁
  if(myChart.value){
    mychart.dispose();
  }
});
</script>
<style lang="less" scoped></style>

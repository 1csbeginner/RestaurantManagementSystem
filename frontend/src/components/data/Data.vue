<template>
  <!--面包屑导航区域-->
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>结账报表</el-breadcrumb-item>
  </el-breadcrumb>
  <div id="chart" ref="chart" style="width: 100%; height: 400px;"></div>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue'; // 导入 Vue 的功能
import { ArrowRight } from '@element-plus/icons-vue'; // 导入图标
import * as echarts from 'echarts'; // 导入 ECharts
import axios from 'axios'; // 导入 axios

// DOM 引用
const chart = ref<null | HTMLElement>(null);
// 图表实例
const chartInstance = ref<null | echarts.ECharts>(null);

// 初始化图表
const initChart = () => {
  if (chart.value) {
    chartInstance.value = echarts.init(chart.value);
    chartInstance.value.setOption({
      title: {
        text: '统计',
      },
      tooltip: {
        trigger: 'axis',
      },
      legend: {
        data: ['销售额'],
      },
      xAxis: {
        type: 'category',
        data: [],
        name: '月份',
      },
      yAxis: {
        type: 'value',
        name: '销售额',
      },
      series: [
        {
          name: '销售额',
          data: [],
          type: 'bar',
        },
      ],
    });
  } else {
    console.error('chart 元素未挂载');
  }
};

// 获取数据并更新图表
const fetchChartData = async () => {
  try {
    const query = { };
    const response = await axios.post(`/statistic/list/1/9999`, query);

    // 确保访问正确的数据结构
    const rawData = response.data.data?.records;
    if (Array.isArray(rawData)) {
      const months = rawData.map(item => item.createmonth); // 提取月份
      const earnings = rawData.map(item => item.earning);   // 提取销售额

      // 更新图表数据
      if (chartInstance.value) {
        chartInstance.value.setOption({
          xAxis: { data: months },
          series: [{ data: earnings }],
        });
      }
    } else {
      console.error('后端返回的 records 数据格式不正确', rawData);
    }
  } catch (error) {
    console.error('获取数据失败', error);
  }
};

// 生命周期钩子
onMounted(() => {
  initChart(); // 初始化图表
  fetchChartData(); // 加载数据
});

onUnmounted(() => {
  if (chartInstance.value) {
    chartInstance.value.dispose(); // 销毁图表实例
    chartInstance.value = null;
  }
});
</script>

<style scoped>
</style>

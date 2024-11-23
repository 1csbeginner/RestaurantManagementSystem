<template>
  <el-breadcrumb :separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>结账报表</el-breadcrumb-item>
  </el-breadcrumb>

  <el-card>
    <!-- 三张图表：每张图表对应不同的筛选条件 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <el-date-picker
            v-model="selectedYear"
            type="year"
            placeholder="选择年份"
            @change="onYearChange"
          />
          <div id="yearChart" ref="yearChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <el-date-picker
            v-model="selectedMonth"
            type="month"
            placeholder="选择月份"
            @change="onMonthChange"
          />
          <div id="monthChart" ref="monthChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <el-date-picker
            v-model="selectedDate"
            type="date"
            placeholder="选择日期"
            @change="onDateChange"
          />
          <div id="dayChart" ref="dayChart" style="width: 100%; height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script lang="ts" setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { ArrowRight } from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import axios from 'axios';

// 状态管理
const selectedYear = ref<string>(''); // 选择的年份
const selectedMonth = ref<string>(''); // 选择的月份
const selectedDate = ref<string>(''); // 选择的日期

// 图表实例
const yearChart = ref<null | HTMLElement>(null);
const monthChart = ref<null | HTMLElement>(null);
const dayChart = ref<null | HTMLElement>(null);

const yearChartInstance = ref<null | echarts.ECharts>(null);
const monthChartInstance = ref<null | echarts.ECharts>(null);
const dayChartInstance = ref<null | echarts.ECharts>(null);

// 数据存储
const yearlyData = ref<Array<any>>([]);
const monthlyData = ref<Array<any>>([]);
const dailyData = ref<Array<any>>([]);

// 初始化图表
const initChart = () => {
  if (yearChart.value) {
    yearChartInstance.value = echarts.init(yearChart.value);
    yearChartInstance.value.setOption({
      title: {
        text: '年度统计',
      },
      tooltip: {
        trigger: 'axis',
      },
      legend: {
        data: ['销售额'],
      },
      xAxis: {
        type: 'category',
        data: ['荤菜', '素菜', '凉菜', '主食', '甜点', '汤'],
        name: '菜品',
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
  }

  if (monthChart.value) {
    monthChartInstance.value = echarts.init(monthChart.value);
    monthChartInstance.value.setOption({
      title: {
        text: '月度统计',
      },
      tooltip: {
        trigger: 'axis',
      },
      legend: {
        data: ['销售额'],
      },
      xAxis: {
        type: 'category',
        data: ['荤菜', '素菜', '凉菜', '主食', '甜点', '汤'],
        name: '菜品',
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
  }

  if (dayChart.value) {
    dayChartInstance.value = echarts.init(dayChart.value);
    dayChartInstance.value.setOption({
      title: {
        text: '日度统计',
      },
      tooltip: {
        trigger: 'axis',
      },
      legend: {
        data: ['销售额'],
      },
      xAxis: {
        type: 'category',
        data: ['荤菜', '素菜', '凉菜', '主食', '甜点', '汤'],
        name: '菜品',
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
  }
};

// 数据请求方法
const fetchData = async (query: object, type: string) => {
  try {
    const response = await axios.post('statistic/list/1/9999', query);
    const rawData = response.data.data?.records || [];
    const earnings = rawData.map(item => ({ sort: item.sort, earnings: item.earning }));

    if (type === 'year') {
      yearlyData.value = earnings;
      updateChart(yearChartInstance.value as echarts.ECharts, earnings);
    } else if (type === 'month') {
      monthlyData.value = earnings;
      updateChart(monthChartInstance.value as echarts.ECharts, earnings);
    } else if (type === 'day') {
      dailyData.value = earnings;
      updateChart(dayChartInstance.value as echarts.ECharts, earnings);
    }
  } catch (error) {
    console.error(`${type} 数据获取失败`, error);
  }
};

// 更新图表数据
const updateChart = (chartInstance: echarts.ECharts | null, data: Array<any>) => {
  if (chartInstance) {
    chartInstance.setOption({
      series: [
        {
          data: data.map(item => item.earnings),
        },
      ],
    });
  }
};

// 选择变化时触发的事件
const onYearChange = () => {
  if (selectedYear.value) {
    fetchData({ createyear: new Date(selectedYear.value).getFullYear() }, 'year');
  }
};

const onMonthChange = () => {
  if (selectedYear.value && selectedMonth.value) {
    fetchData({ createyear: new Date(selectedYear.value).getFullYear(), createmonth: new Date(selectedMonth.value).getMonth() + 1 }, 'month');
  }
};

const onDateChange = () => {
  if (selectedDate.value) {
    const date = new Date(selectedDate.value);
    fetchData({ createyear: date.getFullYear(), createmonth: date.getMonth() + 1, createday: date.getDate() }, 'day');
  }
};

// 生命周期钩子
onMounted(() => {
  initChart();
  if (selectedYear.value) {
    fetchData({ createyear: selectedYear.value }, 'year');
  }
});

onUnmounted(() => {
  yearChartInstance.value?.dispose();
  monthChartInstance.value?.dispose();
  dayChartInstance.value?.dispose();
});
</script>

<style scoped>
.filter-section {
  margin: 20px 0;
}

.el-select, .el-date-picker {
  margin-right: 10px;
}

#yearChart, #monthChart, #dayChart {
  margin: 20px 0;
}
</style>

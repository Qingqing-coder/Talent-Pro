<template>
  <div class="knowledge-graph">
    <div class="header">
      <h1>知识图谱</h1>
      <button @click="loadData" :disabled="loading">
        {{ loading ? '加载中...' : '刷新数据' }}
      </button>
    </div>
    
    <div id="graph" class="graph-container"></div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from "echarts";
import axios from "axios";

// 响应式变量
const loading = ref(false)
const graphInfo = ref(null)
let chart = null

// 初始化图表
const initChart = () => {
  const dom = document.getElementById("graph");
  if (!dom) return;
  
  chart = echarts.init(dom);
  
  // 设置初始空图表
  const emptyOption = {
    title: { text: '知识图谱', left: 'center' },
    series: [{
      type: "graph",
      layout: "force",
      roam: true,
      label: { show: true },
      force: { repulsion: 200 },
      data: [],
      links: [],
      categories: []
    }]
  };
  
  chart.setOption(emptyOption);
}

// 加载数据
const loadData = async () => {
  if (!chart) return;
  
  loading.value = true;
  
  try {
    // 尝试多个后端地址
    const backendUrls = [
      "http://127.0.0.1:8080/graph/data",
      "http://localhost:8080/graph/data"
    ];
    
    let response = null;
    for (const url of backendUrls) {
      try {
        response = await axios.get(url);
        break;
      } catch (error) {
        console.log(`尝试 ${url} 失败:`, error.message);
      }
    }
    
    if (!response) {
      throw new Error('所有后端地址都连接失败');
    }
    const data = response.data;
    
    console.log('获取数据成功:', data);
    
    // 更新图表信息
    graphInfo.value = {
      totalNodes: data.totalNodes || 0,
      totalLinks: data.totalLinks || 0
    };
    
    // 处理节点数据
    const nodes = data.nodes || [];
    const links = data.links || [];
    
    // 生成类别
    const categories = [...new Set(nodes.map(n => n.category))].map(c => ({ name: c }));
    
    // 更新图表
    const option = {
      title: { 
        text: `知识图谱 `,
        left: 'center' 
      },
      legend: { 
        show: categories.length > 0,
        data: categories.map(c => c.name),
        top: 'bottom'
      },
      series: [{
        type: "graph",
        layout: "force",
        roam: true,
        label: { show: true },
        force: { repulsion: 200 },
        data: nodes,
        links: links.map(l => ({
          source: l.source,
          target: l.target,
          label: { show: true, formatter: l.label }
        })),
        categories
      }]
    };
    
    chart.setOption(option);
    
  } catch (error) {
    console.error('加载数据失败:', error);
    
    // 显示错误信息
    const errorOption = {
      title: { 
        text: '数据加载失败', 
        left: 'center',
        textStyle: { color: '#ff4444' }
      },
      series: [{
        type: "graph",
        layout: "force",
        roam: true,
        label: { show: true },
        force: { repulsion: 200 },
        data: [],
        links: [],
        categories: []
      }]
    };
    
    chart.setOption(errorOption);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  initChart();
  loadData();
});
</script>

<style scoped>
.knowledge-graph {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h1 {
  margin: 0;
  color: #333;
}

.header button {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.header button:hover:not(:disabled) {
  background: #0056b3;
}

.header button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.graph-container {
  width: 100%;
  height: 600px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fff;
  margin-bottom: 20px;
}

.info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 5px;
  border-left: 4px solid #007bff;
}

.info p {
  margin: 5px 0;
  color: #666;
}
</style>
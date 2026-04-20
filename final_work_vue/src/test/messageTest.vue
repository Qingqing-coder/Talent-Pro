<script setup>
import { ref, reactive, computed, onBeforeUnmount } from 'vue'

const socket = ref(null)
const isConnected = ref(false)

const message = reactive({
  id: null,
  from: '',
  to: '',
  sendTime: null,
  content: ''
})

const messageLogs = ref([])

const connectionStatus = computed(() => (isConnected.value ? '已连接' : '已断开'))

const addLog = (msg) => {
  messageLogs.value.unshift({
    timestamp: new Date().toLocaleTimeString(),
    message: msg
  })
}

const connectWebSocket = () => {
  const wsUrl = `ws://127.0.0.1:8080/channel/echo?username=${message.from}`

  socket.value = new WebSocket(wsUrl)

  socket.value.onopen = () => {
    isConnected.value = true
    addLog('WebSocket连接已建立')
  }

  socket.value.onmessage = (event) => {
    addLog(`收到消息: ${event.data}`)
  }

  socket.value.onerror = (error) => {
    addLog(`连接错误: ${error}`)
  }

  socket.value.onclose = () => {
    isConnected.value = false
    addLog('WebSocket连接已关闭')
  }
}

const disconnectWebSocket = () => {
  if (socket.value) {
    socket.value.close()
  }
}

const sendMessage = () => {
  if (!isConnected.value) {
    addLog('错误: 未连接到WebSocket')
    return
  }

  const messageToSend = {
    ...message,
    id: 1, // 模拟ID
    sendTime: new Date().toISOString()
  }

  try {
    socket.value.send(JSON.stringify(messageToSend))
    addLog(`已发送消息: ${JSON.stringify(messageToSend)}`)
  } catch (error) {
    addLog(`发送消息失败: ${error}`)
  }
}

onBeforeUnmount(() => {
  disconnectWebSocket()
})
</script>


<template>
  <div class="websocket-test">
    <h2>WebSocket 测试</h2>

    <div class="connection-controls">
      <button @click="connectWebSocket" :disabled="isConnected">连接</button>
      <button @click="disconnectWebSocket" :disabled="!isConnected">断开</button>
      <span>状态: {{ connectionStatus }}</span>
    </div>

    <div class="message-form">
      <h3>发送消息</h3>
      <div>
        <label>发送者ID:</label>
        <input v-model="message.from" type="text">
      </div>
      <div>
        <label>接收者ID:</label>
        <input v-model="message.to" type="text">
      </div>
      <div>
        <label>消息内容:</label>
        <textarea v-model="message.content"></textarea>
      </div>
      <button @click="sendMessage" :disabled="!isConnected">发送消息</button>
    </div>

    <div class="message-log">
      <h3>消息日志</h3>
      <ul>
        <li v-for="(log, index) in messageLogs" :key="index">
          [{{ log.timestamp }}] {{ log.message }}
        </li>
      </ul>
    </div>
  </div>
</template>
<!--<template>-->
<!--  <div>-->
<!--    <h1>文件上传</h1>-->
<!--    <input type="file" @change="handleFileChange" />-->
<!--    <button @click="uploadFile" :disabled="!file">上传文件</button>-->
<!--    <p v-if="message" :class="{ 'success': isSuccess, 'error': !isSuccess }">-->
<!--      {{ message }}-->
<!--    </p>-->
<!--    <p v-if="progress > 0">上传进度: {{ progress }}%</p>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup>-->
<!--import { ref } from 'vue';-->
<!--import axios from 'axios';-->

<!--const file = ref(null);-->
<!--const message = ref('');-->
<!--const isSuccess = ref(false);-->
<!--const progress = ref(0);-->

<!--const handleFileChange = (event) => {-->
<!--  file.value = event.target.files[0];-->
<!--  message.value = '';-->
<!--  progress.value = 0;-->
<!--};-->

<!--const uploadFile = async () => {-->
<!--  if (!file.value) {-->
<!--    message.value = '请先选择文件';-->
<!--    isSuccess.value = false;-->
<!--    return;-->
<!--  }-->

<!--  const formData = new FormData();-->
<!--  formData.append('file', file.value);-->

<!--  try {-->
<!--    const response = await axios.post('http://localhost:8080/upload', formData, {-->
<!--      headers: {-->
<!--        'Content-Type': 'multipart/form-data'-->
<!--      },-->
<!--      onUploadProgress: (progressEvent) => {-->
<!--        if (progressEvent.total) {-->
<!--          progress.value = Math.round(-->
<!--            (progressEvent.loaded * 100) / progressEvent.total-->
<!--          );-->
<!--        }-->
<!--      }-->
<!--    });-->

<!--    message.value = response.data.data; // 假设后端返回的Result.success("上传成功")-->
<!--    isSuccess.value = true;-->
<!--  } catch (error) {-->
<!--    message.value = '上传失败: ' + (error.response?.data?.message || error.message);-->
<!--    isSuccess.value = false;-->
<!--    console.error('上传错误:', error);-->
<!--  } finally {-->
<!--    progress.value = 100;-->
<!--  }-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--.success {-->
<!--  color: green;-->
<!--}-->
<!--.error {-->
<!--  color: red;-->
<!--}-->
<!--</style>-->
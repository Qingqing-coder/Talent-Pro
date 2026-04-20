import axios from 'axios'; // 假设你项目中用 axios 发请求，没有的话需要先安装：npm install axios

// 人脸验证接口：参数为用户名和人脸图片（base64 格式）
export const verifyFace = async (params: {
  username: string;
  faceImage: string; // 摄像头拍摄的人脸图片，转成 base64 字符串
}) => {
  try {
    // 替换成你的后端人脸验证接口地址
    const response = await axios.post('/api/auth/face-verify', params);
    return response.data; // 假设后端返回 { success: boolean, data: { role: number } } 等数据
  } catch (error) {
    console.error('人脸验证接口请求失败:', error);
    throw error; // 抛出错误，让调用方处理
  }
};
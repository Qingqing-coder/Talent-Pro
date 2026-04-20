// 自定义消息提示工具
export const showCustomMessage = (
  message: string,
  type: 'success' | 'error' | 'warning' | 'info',
) => {
  const messageBox = document.createElement('div')
  messageBox.className = `custom-message custom-message-${type}`
  messageBox.innerHTML = `
    <div class="custom-message-content">
      <span class="custom-message-text">${message}</span>
    </div>
  `

  document.body.appendChild(messageBox)

  // 2秒后自动移除
  setTimeout(() => {
    if (messageBox.parentNode) {
      messageBox.parentNode.removeChild(messageBox)
    }
  }, 2000)
}

// 添加全局样式
const addGlobalStyles = () => {
  if (document.getElementById('custom-message-styles')) {
    return // 样式已存在
  }

  const style = document.createElement('style')
  style.id = 'custom-message-styles'
  style.textContent = `
    /* 自定义消息提示样式 */
    .custom-message {
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      z-index: 9999;
      animation: messageFadeIn 0.3s ease-in-out;
    }

    .custom-message-content {
      background-color: rgba(255, 255, 255, 0.95);
      border-radius: 8px;
      padding: 16px 24px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.2);
      min-width: 200px;
      text-align: center;
    }

    .custom-message-text {
      font-size: 14px;
      font-weight: 500;
      color: #333;
    }

    /* 不同类型的消息样式 */
    .custom-message-success .custom-message-content {
      background-color: rgba(103, 194, 58, 0.1);
      border-color: rgba(103, 194, 58, 0.3);
    }

    .custom-message-success .custom-message-text {
      color: #67c23a;
    }

    .custom-message-error .custom-message-content {
      background-color: rgba(245, 108, 108, 0.1);
      border-color: rgba(245, 108, 108, 0.3);
    }

    .custom-message-error .custom-message-text {
      color: #f56c6c;
    }

    .custom-message-warning .custom-message-content {
      background-color: rgba(230, 162, 60, 0.1);
      border-color: rgba(230, 162, 60, 0.3);
    }

    .custom-message-warning .custom-message-text {
      color: #e6a23c;
    }

    .custom-message-info .custom-message-content {
      background-color: rgba(144, 147, 153, 0.1);
      border-color: rgba(144, 147, 153, 0.3);
    }

    .custom-message-info .custom-message-text {
      color: #909399;
    }

    /* 消息动画 */
    @keyframes messageFadeIn {
      from {
        opacity: 0;
        transform: translate(-50%, -50%) scale(0.8);
      }
      to {
        opacity: 1;
        transform: translate(-50%, -50%) scale(1);
      }
    }
  `

  document.head.appendChild(style)
}

// 初始化全局样式
addGlobalStyles()

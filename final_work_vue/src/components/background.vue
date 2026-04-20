<template>
  <div class="login-bg" @mousemove="handleMouseMove" @mouseleave="handleMouseLeave">
    <canvas ref="canvasRef" class="particle-canvas"></canvas>
    
    <div class="bg-glow glow-orange"></div>
    <div class="bg-glow glow-blue"></div>
    
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'

// ==========================================
// Canvas 背景互动：静态星空 + 鼠标打散排斥回弹
// ==========================================
const canvasRef = ref<HTMLCanvasElement | null>(null)
let ctx: CanvasRenderingContext2D | null = null
let animationFrameId: number
let particlesArray: Particle[] = []

const mouse = reactive({
  x: -1000,
  y: -1000,
  radius: 180 // 鼠标打散的影响半径
})

const handleMouseMove = (event: MouseEvent) => {
  mouse.x = event.clientX
  mouse.y = event.clientY
}

const handleMouseLeave = () => {
  mouse.x = -1000
  mouse.y = -1000
}

class Particle {
  x: number
  y: number
  baseX: number
  baseY: number
  size: number
  density: number
  color: string

  constructor(x: number, y: number) {
    this.x = x
    this.y = y
    // 记录初始位置，用于静止时复位
    this.baseX = x
    this.baseY = y
    this.size = Math.random() * 2 + 0.8 // 颗粒大小
    this.density = (Math.random() * 20) + 5 // 决定被打散时的重量/速度
    this.color = 'rgba(253, 186, 116, 0.4)' // 减小橙色颗粒透明度
  }

  draw() {
    if (!ctx) return
    ctx.fillStyle = this.color
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2)
    ctx.closePath()
    ctx.fill()
  }

  update() {
    // 鼠标排斥打散逻辑
    let dx = mouse.x - this.x
    let dy = mouse.y - this.y
    let distance = Math.sqrt(dx * dx + dy * dy)
    
    if (distance < mouse.radius) {
      const forceDirectionX = dx / distance
      const forceDirectionY = dy / distance
      const force = (mouse.radius - distance) / mouse.radius
      const directionX = forceDirectionX * force * this.density
      const directionY = forceDirectionY * force * this.density

      this.x -= directionX
      this.y -= directionY
    } else {
      // 鼠标不在附近时，平滑回弹到初始固定位置
      if (this.x !== this.baseX) {
        let dxBase = this.x - this.baseX
        this.x -= dxBase / 20 
      }
      if (this.y !== this.baseY) {
        let dyBase = this.y - this.baseY
        this.y -= dyBase / 20
      }
    }
  }
}

const initCanvas = () => {
  if (!canvasRef.value) return
  canvasRef.value.width = window.innerWidth
  canvasRef.value.height = window.innerHeight
  ctx = canvasRef.value.getContext('2d')
  particlesArray = []
  
  // 颗粒密度计算
  let numberOfParticles = (canvasRef.value.width * canvasRef.value.height) / 10000 
  for (let i = 0; i < numberOfParticles; i++) {
    let x = Math.random() * canvasRef.value.width
    let y = canvasRef.value.height * (1 - Math.sqrt(Math.random()))
    particlesArray.push(new Particle(x, y))
  }
}

const connect = () => {
  if (!ctx || !canvasRef.value) return
  let maxDistance = 150 

  for (let a = 0; a < particlesArray.length; a++) {
    for (let b = a + 1; b < particlesArray.length; b++) {
      const p1 = particlesArray[a]
      const p2 = particlesArray[b]
      if (!p1 || !p2) continue

      let dx = p1.x - p2.x
      let dy = p1.y - p2.y
      let distance = Math.sqrt(dx * dx + dy * dy)
      
      if (distance < maxDistance) {
        let opacityValue = 1 - (distance / maxDistance)
        ctx.strokeStyle = `rgba(209, 213, 219, ${opacityValue * 0.25})` 
        ctx.lineWidth = 0.6 
        ctx.beginPath()
        ctx.moveTo(p1.x, p1.y)
        ctx.lineTo(p2.x, p2.y)
        ctx.stroke()
      }
    }
  }
}

const animate = () => {
  if (!ctx || !canvasRef.value) return
  ctx.clearRect(0, 0, canvasRef.value.width, canvasRef.value.height)
  
  for (const particle of particlesArray) {
    if (particle) {
      particle.draw()
      particle.update()
    }
  }
  
  connect()
  animationFrameId = requestAnimationFrame(animate)
}

const handleResize = () => {
  if(canvasRef.value) {
    canvasRef.value.width = window.innerWidth
    canvasRef.value.height = window.innerHeight
    initCanvas()
  }
}

// 生命周期
onMounted(() => {
  initCanvas()
  animate()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  cancelAnimationFrame(animationFrameId)
})
</script>

<style scoped>
/* 页面背景：保持深色调渐变 */
.login-bg {
  min-height: 100vh;
  background: linear-gradient(180deg, #15181c 0%, #434955 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  position: relative;
  overflow: hidden;
}

/* 粒子画布 */
.particle-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none; 
}

/* 背景模糊光晕 */
.bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(140px);
  z-index: 0;
  pointer-events: none;
}
.glow-orange {
  width: 700px;
  height: 700px;
  background: rgba(249, 115, 22, 0.05);
  top: -200px;
  left: -100px;
}
.glow-blue {
  width: 800px;
  height: 800px;
  background: rgba(56, 189, 248, 0.03);
  bottom: -300px;
  right: -200px;
}
</style>
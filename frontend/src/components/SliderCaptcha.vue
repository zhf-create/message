<template>
  <div class="captcha-overlay" v-if="visible" @click.self="handleClose">
    <div class="captcha-dialog" @click.stop>
      <div class="captcha-header">
        <span class="captcha-title">
          <el-icon :size="18"><Lock /></el-icon>
          安全验证
        </span>
        <button class="close-btn" @click="handleClose">
          <el-icon :size="16"><Close /></el-icon>
        </button>
      </div>

      <div class="captcha-body">
        <div class="captcha-hint">请完成下方验证，拖动滑块将拼图还原到正确位置</div>

        <div class="image-panel" ref="panelRef">
          <canvas ref="bgCanvas" :width="panelWidth" :height="panelHeight" class="bg-canvas"></canvas>
          <canvas
            ref="pieceCanvas"
            :width="pieceSize"
            :height="panelHeight"
            class="piece-canvas"
            :style="{ left: pieceX + 'px' }"
          ></canvas>
          <div
            class="shadow-mark"
            :style="{
              left: targetX + 'px',
              width: pieceSize + 'px',
              height: panelHeight + 'px'
            }"
          ></div>
        </div>

        <div class="slider-area">
          <div class="slider-track" ref="trackRef">
            <div class="slider-fill" :style="{ width: sliderPercent + '%' }"></div>
            <div
              class="slider-thumb"
              :class="{ success: verified, fail: failed }"
              :style="{ left: sliderPercent + '%' }"
              @mousedown.prevent="startSlide"
              @touchstart.prevent="startSlide"
            >
              <span v-if="!verified && !failed" class="thumb-icon">→</span>
              <span v-if="verified" class="thumb-icon check">✓</span>
              <span v-if="failed" class="thumb-icon error">✕</span>
            </div>
            <span class="slider-text" v-if="!verified && !failed">{{ sliderText }}</span>
            <span class="slider-text success-text" v-if="verified">验证通过 ✓</span>
            <span class="slider-text fail-text" v-if="failed">验证失败</span>
          </div>
        </div>
      </div>

      <div class="captcha-footer">
        <span class="refresh-btn" @click="refreshCaptcha">
          <el-icon :size="14"><Refresh /></el-icon>
          换一张
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { Lock, Close, Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  visible: { type: Boolean, default: false },
  panelWidth: { type: Number, default: 340 },
  panelHeight: { type: Number, default: 200 },
  tolerance: { type: Number, default: 5 }
})

const emit = defineEmits(['verified', 'close'])

const panelRef = ref(null)
const bgCanvas = ref(null)
const pieceCanvas = ref(null)
const trackRef = ref(null)

const pieceSize = ref(50)
const targetX = ref(0)
const pieceX = ref(0)
const sliderPercent = ref(0)

const verified = ref(false)
const failed = ref(false)
const sliding = ref(false)

const sliderText = ref('拖动滑块完成拼图验证')

let startX = 0
let startPercent = 0

function randomRange(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

function drawCaptcha() {
  const bg = bgCanvas.value
  const piece = pieceCanvas.value
  if (!bg || !piece) return

  const ctx = bg.getContext('2d')
  const pctx = piece.getContext('2d')
  const w = props.panelWidth
  const h = props.panelHeight
  const ps = pieceSize.value

  targetX.value = randomRange(ps + 10, w - ps - 10)

  ctx.clearRect(0, 0, w, h)

  const gradient = ctx.createLinearGradient(0, 0, w, h)
  const hue1 = randomRange(0, 360)
  const hue2 = (hue1 + randomRange(30, 60)) % 360
  gradient.addColorStop(0, `hsl(${hue1}, 60%, 40%)`)
  gradient.addColorStop(0.5, `hsl(${(hue1 + hue2) / 2}, 55%, 50%)`)
  gradient.addColorStop(1, `hsl(${hue2}, 60%, 40%)`)
  ctx.fillStyle = gradient
  ctx.fillRect(0, 0, w, h)

  for (let i = 0; i < 30; i++) {
    ctx.beginPath()
    ctx.arc(
      randomRange(0, w),
      randomRange(0, h),
      randomRange(5, 25),
      0, Math.PI * 2
    )
    ctx.fillStyle = `hsla(${randomRange(0, 360)}, 50%, 60%, ${Math.random() * 0.15})`
    ctx.fill()
  }

  for (let i = 0; i < 20; i++) {
    const x = randomRange(0, w)
    const y = randomRange(0, h)
    ctx.fillStyle = `rgba(255, 255, 255, ${Math.random() * 0.05})`
    ctx.fillRect(x, y, randomRange(3, 8), randomRange(3, 8))
  }

  for (let i = 0; i < 8; i++) {
    ctx.beginPath()
    const cx = randomRange(0, w)
    const cy = randomRange(0, h)
    ctx.moveTo(cx, cy)
    for (let j = 0; j < 6; j++) {
      ctx.lineTo(
        cx + randomRange(-20, 20),
        cy + randomRange(-20, 20)
      )
    }
    ctx.strokeStyle = `rgba(255, 255, 255, ${Math.random() * 0.06})`
    ctx.lineWidth = 1
    ctx.stroke()
  }

  const tx = targetX.value
  const ty = (h - ps) / 2

  pctx.clearRect(0, 0, ps, h)
  pctx.drawImage(bg, tx, ty, ps, ps, 0, ty, ps, ps)

  ctx.clearRect(tx, ty, ps, ps)
  ctx.fillStyle = 'rgba(0, 0, 0, 0.3)'
  ctx.fillRect(tx, ty, ps, ps)

  ctx.strokeStyle = 'rgba(255, 255, 255, 0.5)'
  ctx.lineWidth = 1.5
  ctx.setLineDash([5, 3])
  ctx.strokeRect(tx, ty, ps, ps)
  ctx.setLineDash([])

  pctx.strokeStyle = 'rgba(108, 99, 255, 0.6)'
  pctx.lineWidth = 2
  pctx.strokeRect(0, ty, ps, ps)

  pieceX.value = 0
}

function refreshCaptcha() {
  verified.value = false
  failed.value = false
  sliderPercent.value = 0
  pieceX.value = 0
  sliderText.value = '拖动滑块完成拼图验证'
  nextTick(() => drawCaptcha())
}

function startSlide(e) {
  if (verified.value) return
  sliding.value = true
  failed.value = false
  sliderText.value = '拖动滑块完成拼图验证'
  const clientX = e.clientX || e.touches[0].clientX
  startX = clientX
  startPercent = sliderPercent.value

  document.addEventListener('mousemove', onSlide)
  document.addEventListener('mouseup', endSlide)
  document.addEventListener('touchmove', onSlide, { passive: false })
  document.addEventListener('touchend', endSlide)
}

function onSlide(e) {
  if (!sliding.value) return
  const clientX = e.clientX || e.touches[0].clientX
  const track = trackRef.value
  if (!track) return
  const rect = track.getBoundingClientRect()
  const delta = clientX - startX
  const trackWidth = rect.width - 44
  const percent = Math.max(0, Math.min(100, startPercent + (delta / trackWidth) * 100))
  sliderPercent.value = percent

  const maxPieceX = props.panelWidth - pieceSize.value
  pieceX.value = (percent / 100) * maxPieceX
}

function endSlide() {
  if (!sliding.value) return
  sliding.value = false

  document.removeEventListener('mousemove', onSlide)
  document.removeEventListener('mouseup', endSlide)
  document.removeEventListener('touchmove', onSlide)
  document.removeEventListener('touchend', endSlide)

  const diff = Math.abs(pieceX.value - targetX.value)
  if (diff <= props.tolerance) {
    verified.value = true
    sliderText.value = '验证通过 ✓'
    setTimeout(() => emit('verified'), 400)
  } else {
    failed.value = true
    sliderText.value = '验证失败，请重试'
    setTimeout(() => {
      failed.value = false
      sliderPercent.value = 0
      pieceX.value = 0
      sliderText.value = '拖动滑块完成拼图验证'
    }, 600)
  }
}

function handleClose() {
  if (!verified.value) {
    emit('close')
  } else {
    emit('close')
  }
}

watch(() => props.visible, (val) => {
  if (val) {
    nextTick(() => drawCaptcha())
  }
})

onMounted(() => {
  if (props.visible) {
    nextTick(() => drawCaptcha())
  }
})
</script>

<style scoped>
.captcha-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.25s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.captcha-dialog {
  background: #1A1A2E;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 18px;
  width: 400px;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.6);
  animation: dialogPop 0.35s ease;
  overflow: hidden;
}

@keyframes dialogPop {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.captcha-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 22px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.captcha-title {
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--primary-light);
}

.close-btn {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  border: none;
  background: rgba(255, 255, 255, 0.04);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.close-btn:hover {
  background: rgba(255, 82, 82, 0.15);
  color: var(--danger);
}

.captcha-body {
  padding: 20px 22px;
}

.captcha-hint {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 14px;
  text-align: center;
}

.image-panel {
  position: relative;
  width: v-bind(panelWidth + 'px');
  height: v-bind(panelHeight + 'px');
  border-radius: 10px;
  overflow: hidden;
  margin: 0 auto;
  background: rgba(0, 0, 0, 0.2);
}

.bg-canvas {
  display: block;
  width: 100%;
  height: 100%;
}

.piece-canvas {
  position: absolute;
  top: 0;
  transition: left 0.05s linear;
  pointer-events: none;
  width: v-bind(pieceSize + 'px');
  height: 100%;
}

.shadow-mark {
  position: absolute;
  top: 0;
  pointer-events: none;
  border: 2px dashed rgba(255, 255, 255, 0.4);
  border-radius: 2px;
  animation: pulseBorder 1.5s ease-in-out infinite;
}

@keyframes pulseBorder {
  0%, 100% { border-color: rgba(255, 255, 255, 0.3); }
  50% { border-color: rgba(255, 255, 255, 0.6); }
}

.slider-area {
  margin-top: 18px;
  padding: 0 4px;
}

.slider-track {
  position: relative;
  height: 44px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 22px;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
}

.slider-fill {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: linear-gradient(90deg, rgba(108, 99, 255, 0.3), rgba(108, 99, 255, 0.5));
  border-radius: 22px;
  transition: width 0.05s linear;
}

.slider-thumb {
  position: absolute;
  top: 2px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  z-index: 2;
  transform: translateX(-50%);
  box-shadow: 0 2px 12px rgba(108, 99, 255, 0.4);
  transition: background 0.3s, box-shadow 0.3s;
}

.slider-thumb:hover {
  box-shadow: 0 4px 20px rgba(108, 99, 255, 0.6);
}

.slider-thumb.success {
  background: linear-gradient(135deg, #00C853, #009624);
  box-shadow: 0 2px 12px rgba(0, 200, 83, 0.4);
}

.slider-thumb.fail {
  background: linear-gradient(135deg, #FF5252, #D32F2F);
  box-shadow: 0 2px 12px rgba(255, 82, 82, 0.4);
  animation: shake 0.4s ease;
}

.slider-thumb:active {
  cursor: grabbing;
}

.thumb-icon {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}

.slider-text {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 13px;
  color: var(--text-muted);
  pointer-events: none;
  transition: color 0.3s;
}

.slider-text.success-text {
  color: var(--success);
}

.slider-text.fail-text {
  color: var(--danger);
}

.captcha-footer {
  padding: 14px 22px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
  display: flex;
  justify-content: center;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 6px 16px;
  border-radius: 8px;
  transition: all 0.3s;
}

.refresh-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  color: var(--primary-light);
}

@keyframes shake {
  0%, 100% { transform: translateX(-50%) rotate(0); }
  20% { transform: translateX(-50%) rotate(-8deg); }
  40% { transform: translateX(-50%) rotate(8deg); }
  60% { transform: translateX(-50%) rotate(-4deg); }
  80% { transform: translateX(-50%) rotate(4deg); }
}
</style>
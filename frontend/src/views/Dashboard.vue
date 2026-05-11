<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>工作台</h2>
      <p>欢迎回来，{{ username }}！今天是 {{ today }}</p>
    </div>

    <div class="stats-grid">
      <div
        class="stat-card glass-card"
        v-for="(stat, index) in stats"
        :key="index"
        @mouseenter="stat.hover = true"
        @mouseleave="stat.hover = false"
        :style="{ animationDelay: index * 0.1 + 's' }"
      >
        <div class="stat-icon" :style="{ background: stat.iconBg }">
          <el-icon :size="24"><component :is="stat.icon" /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ animatedValue(stat) }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
        <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
          <el-icon :size="12"><component :is="stat.trend > 0 ? Top : Bottom" /></el-icon>
          {{ Math.abs(stat.trend) }}%
        </div>
        <div class="stat-bar">
          <div class="stat-bar-fill" :style="{ width: stat.hover ? '100%' : stat.barWidth + '%', background: stat.iconBg }"></div>
        </div>
      </div>
    </div>

    <div class="dashboard-grid">
      <div class="glass-card chart-card">
        <div class="card-header">
          <h3><el-icon :size="18"><TrendCharts /></el-icon> 本周预约趋势</h3>
          <div class="chart-legend">
            <span><span class="dot" style="background:var(--primary)"></span>预约数</span>
          </div>
        </div>
        <div class="chart-container">
          <div class="chart-bars">
            <div
              class="bar-group"
              v-for="(item, idx) in weeklyData"
              :key="idx"
              @mouseenter="item.hover = true"
              @mouseleave="item.hover = false"
            >
              <div class="bar-tooltip" v-show="item.hover">{{ item.value }} 单</div>
              <div class="bar-wrapper">
                <div
                  class="bar"
                  :style="{
                    height: (item.value / maxWeekly) * 180 + 'px',
                    animationDelay: idx * 0.08 + 's'
                  }"
                >
                </div>
              </div>
              <div class="bar-label">{{ item.day }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="glass-card chart-card">
        <div class="card-header">
          <h3><el-icon :size="18"><PieChart /></el-icon> 服务项目分布</h3>
        </div>
        <div class="pie-container">
          <div class="pie-canvas">
            <svg viewBox="0 0 100 100" class="pie-svg">
              <circle cx="50" cy="50" r="40" fill="none" stroke="rgba(255,255,255,0.05)" stroke-width="12"/>
              <circle
                cx="50" cy="50" r="40" fill="none"
                :stroke="serviceColors[0]"
                stroke-width="12"
                stroke-dasharray="251.2"
                :stroke-dashoffset="251.2 - 251.2 * servicePercentages[0] / 100"
                stroke-linecap="round"
                transform="rotate(-90, 50, 50)"
                class="pie-segment"
              />
              <circle
                cx="50" cy="50" r="40" fill="none"
                :stroke="serviceColors[1]"
                stroke-width="12"
                :stroke-dasharray="251.2"
                :stroke-dashoffset="251.2 - 251.2 * servicePercentages[1] / 100"
                stroke-linecap="round"
                transform="rotate(-90, 50, 50)"
                class="pie-segment"
                style="transform-origin: 50px 50px; transform: rotate(calc(-90deg + 360deg * var(--offset1, 0)));"
                :style="{ '--offset1': serviceOffsets[0] / 100 }"
              />
              <circle
                cx="50" cy="50" r="40" fill="none"
                :stroke="serviceColors[2]"
                stroke-width="12"
                :stroke-dasharray="251.2"
                :stroke-dashoffset="251.2 - 251.2 * servicePercentages[2] / 100"
                stroke-linecap="round"
                transform="rotate(-90, 50, 50)"
                class="pie-segment"
                :style="{ '--offset2': serviceOffsets[1] / 100 }"
              />
            </svg>
            <div class="pie-center">
              <span class="pie-total">{{ serviceTotal }}</span>
              <span class="pie-label">总单数</span>
            </div>
          </div>
          <div class="pie-legend">
            <div class="legend-item" v-for="(s, i) in serviceData" :key="i">
              <span class="legend-dot" :style="{ background: serviceColors[i] }"></span>
              <span class="legend-name">{{ s.name }}</span>
              <span class="legend-value">{{ s.value }}</span>
              <span class="legend-percent">{{ s.percent }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="glass-card activity-card">
      <div class="card-header">
        <h3><el-icon :size="18"><Timer /></el-icon> 最近操作</h3>
      </div>
      <div class="activity-timeline">
        <div class="timeline-item" v-for="(item, i) in activities" :key="i">
          <div class="timeline-dot" :style="{ background: item.color }">
            <el-icon :size="12"><component :is="item.icon" /></el-icon>
          </div>
          <div class="timeline-content">
            <div class="timeline-title">{{ item.title }}</div>
            <div class="timeline-time">{{ item.time }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, markRaw } from 'vue'
import { getUserInfo } from '../api/user'
import { getRecentLogs } from '../api/operation'
import { getStats, getWeeklySchedule, getServiceDistribution } from '../api/dashboard'
import { eventBus } from '../utils/eventBus'
import {
  User, Calendar, Notebook, Setting,
  TrendCharts, PieChart, Timer,
  Top, Bottom
} from '@element-plus/icons-vue'

const username = ref('用户')
const today = ref('')

const stats = ref([
  { icon: markRaw(User), label: '总用户数', value: 0, target: 128, trend: 12, iconBg: 'linear-gradient(135deg, #6C63FF, #8B83FF)', barWidth: 0, hover: false },
  { icon: markRaw(Calendar), label: '本月预约', value: 0, target: 56, trend: -3, iconBg: 'linear-gradient(135deg, #FF6B9D, #FF8FB3)', barWidth: 0, hover: false },
  { icon: markRaw(Notebook), label: '客户档案', value: 0, target: 89, trend: 8, iconBg: 'linear-gradient(135deg, #00D2FF, #4DDCFF)', barWidth: 0, hover: false },
  { icon: markRaw(Setting), label: '系统设置项', value: 0, target: 24, trend: 0, iconBg: 'linear-gradient(135deg, #FFB300, #FFCA28)', barWidth: 0, hover: false }
])

const weeklyData = ref([
  { day: '周一', value: 8, hover: false },
  { day: '周二', value: 12, hover: false },
  { day: '周三', value: 6, hover: false },
  { day: '周四', value: 15, hover: false },
  { day: '周五', value: 10, hover: false },
  { day: '周六', value: 18, hover: false },
  { day: '周日', value: 14, hover: false }
])

const maxWeekly = computed(() => Math.max(...weeklyData.value.map(d => d.value), 1))

const serviceData = ref([
  { name: '用户数', value: 42 },
  { name: '预约数', value: 28 },
  { name: '客户档案数', value: 19 }
])

const serviceColors = ['#6C63FF', '#FF6B9D', '#00D2FF']

const serviceTotal = computed(() => serviceData.value.reduce((s, i) => s + i.value, 0))

const servicePercentages = computed(() =>
  serviceData.value.map(s => Math.round((s.value / serviceTotal.value) * 100))
)

const serviceOffsets = computed(() => {
  let offset = 0
  return serviceData.value.map((s, i) => {
    if (i === 0) return 0
    offset += servicePercentages.value[i - 1]
    return offset
  })
})

const activities = ref([])

const operationIcons = {
  user: markRaw(User),
  schedule: markRaw(Calendar),
  customer: markRaw(Notebook),
  setting: markRaw(Setting)
}

const operationColors = {
  user: 'var(--primary)',
  schedule: 'var(--secondary)',
  customer: 'var(--accent)',
  setting: 'var(--warning)'
}

function getOperationIcon(type) {
  return operationIcons[type] || markRaw(User)
}

function getOperationColor(type) {
  return operationColors[type] || 'var(--primary)'
}

function formatTime(dateStr) {
  if (!dateStr) return '未知时间'
  const now = new Date()
  const logTime = new Date(dateStr)
  const diff = now.getTime() - logTime.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  if (hours < 24) return `${hours} 小时前`
  return `${days} 天前`
}

function animatedValue(stat) {
  return stat.value
}

function animateStats() {
  stats.value.forEach((stat, index) => {
    setTimeout(() => {
      const target = stat.value || stat.target
      const duration = 1500
      const start = performance.now()
      function update(now) {
        const elapsed = now - start
        const progress = Math.min(elapsed / duration, 1)
        const easeOut = 1 - Math.pow(1 - progress, 3)
        stat.value = Math.round(easeOut * target)
        stat.barWidth = easeOut * 70
        if (progress < 1) {
          requestAnimationFrame(update)
        }
      }
      requestAnimationFrame(update)
    }, index * 200)
  })
}

function formatDate() {
  const d = new Date()
  const options = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  return d.toLocaleDateString('zh-CN', options)
}

async function fetchActivities() {
  try {
    const res = await getRecentLogs()
    if (res.data && res.data.length > 0) {
      activities.value = res.data.map(log => ({
        icon: getOperationIcon(log.operationType),
        title: log.operationName,
        time: formatTime(log.createTime),
        color: getOperationColor(log.operationType)
      }))
    } else {
      activities.value = [
        { icon: markRaw(User), title: '新用户注册', time: '刚刚', color: 'var(--primary)' },
        { icon: markRaw(Calendar), title: '新增排班记录', time: '5 分钟前', color: 'var(--secondary)' },
        { icon: markRaw(Notebook), title: '添加客户档案', time: '10 分钟前', color: 'var(--accent)' },
        { icon: markRaw(Setting), title: '修改系统配置', time: '15 分钟前', color: 'var(--warning)' }
      ]
    }
  } catch {
    activities.value = [
      { icon: markRaw(User), title: '新用户注册', time: '刚刚', color: 'var(--primary)' },
      { icon: markRaw(Calendar), title: '新增排班记录', time: '5 分钟前', color: 'var(--secondary)' },
      { icon: markRaw(Notebook), title: '添加客户档案', time: '10 分钟前', color: 'var(--accent)' },
      { icon: markRaw(Setting), title: '修改系统配置', time: '15 分钟前', color: 'var(--warning)' }
    ]
  }
}

async function fetchStats() {
  try {
    const res = await getStats()
    if (res.data) {
      stats.value[0].value = res.data.userCount || 0
      stats.value[1].value = res.data.scheduleCount || 0
      stats.value[2].value = res.data.customerCount || 0
      stats.value[3].value = res.data.settingCount || 0
    }
  } catch (e) {
    console.error('Failed to fetch stats:', e)
  }
}

async function fetchWeeklyData() {
  try {
    const res = await getWeeklySchedule()
    if (res.data && res.data.length > 0) {
      weeklyData.value = res.data.map(item => ({
        day: item.day,
        value: item.value,
        hover: false
      }))
    }
  } catch (e) {
    console.error('Failed to fetch weekly data:', e)
  }
}

async function fetchServiceDistribution() {
  try {
    const res = await getServiceDistribution()
    if (res.data && res.data.length > 0) {
      serviceData.value = res.data
    }
  } catch (e) {
    console.error('Failed to fetch service distribution:', e)
  }
}

onMounted(async () => {
  today.value = formatDate()
  try {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    username.value = user.username || '用户'
  } catch {}
  await fetchStats()
  await fetchWeeklyData()
  await fetchServiceDistribution()
  animateStats()
  await fetchActivities()

  eventBus.on('dataChanged', async () => {
    await fetchStats()
    await fetchActivities()
  })
})
</script>

<style scoped>
.dashboard {
  animation: fadeUp 0.5s ease;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-header {
  margin-bottom: 28px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
}

.page-header p {
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 6px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  padding: 24px;
  position: relative;
  overflow: hidden;
  animation: statSlide 0.6s ease backwards;
  display: flex;
  flex-direction: column;
}

@keyframes statSlide {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.stat-card .stat-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: rgba(255,255,255,0.05);
  border-radius: 0 0 var(--radius) var(--radius);
  overflow: hidden;
}

.stat-card .stat-bar-fill {
  height: 100%;
  border-radius: 0 0 0 var(--radius);
  transition: width 0.5s ease;
  opacity: 0.5;
}

.stat-trend.up {
  color: var(--success);
  background: rgba(0, 200, 83, 0.1);
}

.stat-trend.down {
  color: var(--danger);
  background: rgba(255, 82, 82, 0.1);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

@media (max-width: 900px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

.chart-card, .activity-card {
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.chart-legend {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--text-secondary);
}

.chart-legend .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
  margin-right: 6px;
}

.chart-bars {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 220px;
  padding-top: 20px;
}

.bar-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.bar-tooltip {
  position: absolute;
  top: -32px;
  background: rgba(0,0,0,0.8);
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  white-space: nowrap;
  z-index: 10;
}

.bar-wrapper {
  width: 60%;
  height: 180px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.bar {
  width: 100%;
  max-width: 36px;
  background: linear-gradient(to top, var(--primary), var(--primary-light));
  border-radius: 6px 6px 2px 2px;
  transition: all 0.3s;
  min-height: 4px;
  animation: barGrow 1s ease backwards;
  cursor: pointer;
  position: relative;
}

@keyframes barGrow {
  from { height: 0 !important; }
}

.bar:hover {
  opacity: 0.8;
  filter: brightness(1.2);
}

.bar-label {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 8px;
}

.pie-container {
  display: flex;
  align-items: center;
  gap: 24px;
}

.pie-canvas {
  position: relative;
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}

.pie-svg {
  width: 100%;
  height: 100%;
}

.pie-segment {
  transition: stroke-dashoffset 1s ease;
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.pie-total {
  font-size: 22px;
  font-weight: 700;
  display: block;
}

.pie-label {
  font-size: 11px;
  color: var(--text-muted);
}

.pie-legend {
  flex: 1;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  font-size: 13px;
  border-bottom: 1px solid rgba(255,255,255,0.04);
}

.legend-item:last-child {
  border-bottom: none;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.legend-name {
  flex: 1;
  color: var(--text-secondary);
}

.legend-value {
  font-weight: 600;
}

.legend-percent {
  color: var(--text-muted);
  min-width: 36px;
  text-align: right;
}

.activity-timeline {
  position: relative;
}

.timeline-item {
  display: flex;
  gap: 14px;
  padding: 12px 0;
  position: relative;
}

.timeline-item::before {
  content: '';
  position: absolute;
  left: 16px;
  top: 36px;
  bottom: -12px;
  width: 1px;
  background: rgba(255,255,255,0.06);
}

.timeline-item:last-child::before {
  display: none;
}

.timeline-dot {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.timeline-content {
  flex: 1;
  padding-top: 4px;
}

.timeline-title {
  font-size: 14px;
  font-weight: 500;
}

.timeline-time {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 2px;
}
</style>
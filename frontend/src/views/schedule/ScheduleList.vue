<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2>排班管理</h2>
        <p>管理员工的排班与服务时间</p>
      </div>
      <div class="header-actions">
        <el-date-picker
          v-model="dateFilter"
          type="date"
          placeholder="按日期筛选"
          value-format="YYYY-MM-DD"
          @change="fetchByDate"
          clearable
          style="width: 160px"
        />
        <el-button type="primary" @click="showDialog = true" :icon="Plus">添加排班</el-button>
      </div>
    </div>

    <div class="table-container glass-card">
      <el-table :data="schedules" v-loading="loading" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="staffName" label="员工姓名" min-width="120" />
        <el-table-column prop="date" label="排班日期" min-width="120" />
        <el-table-column label="排班时间" min-width="200">
          <template #default="{ row }">
            <span>{{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="plain" size="small">
              {{ row.status || '待确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">
              <el-icon :size="14"><Edit /></el-icon> 编辑
            </el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">
              <el-icon :size="14"><Delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="showDialog" :title="dialogTitle" width="500px" :close-on-click-modal="false">
      <el-form :model="form" label-position="top" ref="formRef">
        <el-form-item label="员工姓名" prop="staffName">
          <el-input v-model="form.staffName" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="排班日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width:100%"
          />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker
                v-model="form.startTime"
                placeholder="选择开始时间"
                value-format="HH:mm:ss"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker
                v-model="form.endTime"
                placeholder="选择结束时间"
                value-format="HH:mm:ss"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width:100%">
            <el-option label="待确认" value="待确认" />
            <el-option label="已确认" value="已确认" />
            <el-option label="休息" value="休息" />
            <el-option label="请假" value="请假" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="notes">
          <el-input v-model="form.notes" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">
          {{ isEdit ? '保存' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getAllSchedules, getSchedulesByDate, insertSchedule, updateSchedule, deleteSchedule } from '../../api/schedule'
import { eventBus } from '../../utils/eventBus'

const loading = ref(false)
const schedules = ref([])
const showDialog = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const dateFilter = ref('')

const form = ref({
  staffName: '',
  date: '',
  startTime: '',
  endTime: '',
  status: '待确认',
  notes: ''
})

const dialogTitle = computed(() => isEdit.value ? '编辑排班' : '添加排班')

function formatTime(timeStr) {
  if (!timeStr) return '-'
  return timeStr.substring(0, 5)
}

async function fetchSchedules() {
  loading.value = true
  try {
    const res = await getAllSchedules()
    schedules.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

async function fetchByDate(date) {
  if (!date) {
    await fetchSchedules()
    return
  }
  loading.value = true
  try {
    const res = await getSchedulesByDate(date)
    schedules.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

function getStatusType(status) {
  const types = {
    '待确认': 'info',
    '已确认': 'success',
    '休息': 'warning',
    '请假': 'danger'
  }
  return types[status] || 'info'
}

function handleEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  showDialog.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除「${row.staffName}」的排班记录吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSchedule(row.id)
      ElMessage.success('删除成功')
      await fetchSchedules()
      eventBus.emit('dataChanged')
    } catch {}
  }).catch(() => {})
}

async function submitForm() {
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateSchedule(form.value)
      ElMessage.success('更新成功')
    } else {
      await insertSchedule(form.value)
      ElMessage.success('添加成功')
    }
    showDialog.value = false
    form.value = { staffName: '', date: '', startTime: '', endTime: '', status: '待确认', notes: '' }
    isEdit.value = false
    await fetchSchedules()
    eventBus.emit('dataChanged')
  } catch {} finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchSchedules()
})
</script>

<style scoped>
.page-container {
  animation: fadeUp 0.5s ease;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 4px 0;
}

.page-header p {
  color: var(--text-secondary);
  font-size: 14px;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
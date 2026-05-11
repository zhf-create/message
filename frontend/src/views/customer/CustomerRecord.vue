<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2>客户档案</h2>
        <p>管理到店消费的用户个人信息（区别于系统登录账户）</p>
      </div>
      <div class="header-actions">
        <el-input
          v-model="phoneSearch"
          placeholder="按手机号搜索"
          clearable
          style="width: 200px"
          @keyup.enter="searchByPhone"
          @clear="fetchRecords"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="showDialog = true" :icon="Plus">添加客户</el-button>
      </div>
    </div>

    <div class="table-container glass-card">
      <el-table :data="records" v-loading="loading" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === '男' ? 'primary' : 'success'" effect="dark" size="small">
              {{ row.gender || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
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

    <el-dialog v-model="showDialog" :title="dialogTitle" width="480px" :close-on-click-modal="false">
      <el-form :model="form" label-position="top" ref="formRef">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择" style="width:100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
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
import { Plus, Edit, Delete, Search } from '@element-plus/icons-vue'
import { getAllRecords, getRecordByPhone, insertRecord, updateRecord, deleteRecord } from '../../api/customer'
import { eventBus } from '../../utils/eventBus'

const loading = ref(false)
const records = ref([])
const showDialog = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const phoneSearch = ref('')

const form = ref({
  name: '',
  phone: '',
  gender: '',
  notes: ''
})

const dialogTitle = computed(() => isEdit.value ? '编辑客户' : '添加客户')

async function fetchRecords() {
  loading.value = true
  try {
    const res = await getAllRecords()
    records.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

async function searchByPhone() {
  if (!phoneSearch.value.trim()) {
    await fetchRecords()
    return
  }
  loading.value = true
  try {
    const res = await getRecordByPhone(phoneSearch.value.trim())
    records.value = res.data ? [res.data] : []
  } catch {
    records.value = []
  } finally {
    loading.value = false
  }
}

function handleEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  showDialog.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除「${row.name}」的档案吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRecord(row.id)
      ElMessage.success('删除成功')
      await fetchRecords()
      eventBus.emit('dataChanged')
    } catch {}
  }).catch(() => {})
}

async function submitForm() {
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateRecord(form.value)
      ElMessage.success('更新成功')
    } else {
      await insertRecord(form.value)
      ElMessage.success('添加成功')
    }
    showDialog.value = false
    form.value = { name: '', phone: '', gender: '', notes: '' }
    isEdit.value = false
    await fetchRecords()
    eventBus.emit('dataChanged')
  } catch {} finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchRecords()
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
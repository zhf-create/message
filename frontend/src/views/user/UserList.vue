<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2>用户管理</h2>
        <p>管理系统中的所有用户账户</p>
      </div>
      <el-button type="primary" @click="showAddDialog = true" :icon="Plus">添加用户</el-button>
    </div>

    <div class="table-container glass-card">
      <el-table :data="users" v-loading="loading" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column prop="nickname" label="昵称" min-width="120">
          <template #default="{ row }">
            <span class="nickname-text">{{ row.nickname || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" min-width="170">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
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

    <el-dialog v-model="showAddDialog" title="添加用户" width="450px" :close-on-click-modal="false">
      <el-form :model="addForm" label-position="top" ref="addFormRef" :rules="addRules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="addForm.username" placeholder="5-16位非空字符" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="addForm.password" type="password" placeholder="5-16位非空字符" show-password />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="addForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAdd" :loading="adding">添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEditDialog" title="编辑用户" width="450px" :close-on-click-modal="false">
      <el-form :model="editForm" label-position="top" ref="editFormRef">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="editing">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getAllUsers, updateUser, addUser, deleteUser } from '../../api/user'
import { eventBus } from '../../utils/eventBus'

const loading = ref(false)
const users = ref([])
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const adding = ref(false)
const editing = ref(false)

const addForm = ref({ username: '', password: '', email: '' })
const editForm = ref({ id: null, username: '', nickname: '', email: '' })

const addRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^\S{5,16}$/, message: '用户名5-16位非空字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{5,16}$/, message: '密码5-16位非空字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { pattern: /^[\w.-]+@[\w.-]+\.\w+$/, message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

async function fetchUsers() {
  loading.value = true
  try {
    const res = await getAllUsers()
    users.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ')
}

function handleEdit(row) {
  editForm.value = { ...row }
  showEditDialog.value = true
}

async function submitEdit() {
  editing.value = true
  try {
    await updateUser(editForm.value)
    ElMessage.success('更新成功')
    showEditDialog.value = false
    await fetchUsers()
    eventBus.emit('dataChanged')
  } catch {} finally {
    editing.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除用户「${row.username}」吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      await fetchUsers()
      eventBus.emit('dataChanged')
    } catch {}
  }).catch(() => {})
}

async function submitAdd() {
  adding.value = true
  try {
    await addUser(addForm.value)
    ElMessage.success('添加成功')
    showAddDialog.value = false
    addForm.value = { username: '', password: '', email: '' }
    await fetchUsers()
    eventBus.emit('dataChanged')
  } catch {} finally {
    adding.value = false
  }
}

onMounted(() => {
  fetchUsers()
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

.nickname-text {
  color: var(--text-muted);
}

.el-button.is-link {
  padding: 4px 8px;
}
</style>
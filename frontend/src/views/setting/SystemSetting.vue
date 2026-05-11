<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h2>系统设置</h2>
        <p>配置系统运行参数，如营业时间、预约规则、价格标准等</p>
      </div>
      <el-button type="primary" @click="showDialog = true" :icon="Plus">添加设置</el-button>
    </div>

    <div class="setting-intro glass-card">
      <el-icon :size="20"><InfoFilled /></el-icon>
      <span>系统设置用于管理用户管理系统的运行参数，例如：<b>系统名称</b>、<b>登录超时时间</b>、<b>数据备份周期</b> 等。设置名称为配置项的名称，设置值为配置项的具体内容。</span>
    </div>

    <div class="table-container glass-card">
      <el-table :data="settings" v-loading="loading" stripe style="width: 100%">
        <el-table-column type="index" label="#" width="60" align="center" />
        <el-table-column prop="settingKey" label="设置名称" min-width="180">
          <template #default="{ row }">
            <div class="setting-key-cell">
              <el-tag effect="dark" color="rgba(108,99,255,0.15)" style="color:var(--primary-light);border:none;font-weight:600">
                {{ row.settingKey }}
              </el-tag>
              <span class="setting-desc">{{ getSettingDescription(row.settingKey) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="settingValue" label="设置值" min-width="260" show-overflow-tooltip>
          <template #default="{ row }">
            <span class="setting-value-text">{{ row.settingValue }}</span>
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

    <el-dialog v-model="showDialog" :title="dialogTitle" width="520px" :close-on-click-modal="false">
      <el-form :model="form" label-position="top" ref="formRef" :rules="rules">
        <el-form-item label="设置名称" prop="settingKey">
          <el-select v-model="form.settingKey" placeholder="请选择或输入设置名称" :disabled="isEdit" filterable allow-create clearable style="width:100%">
            <el-option-group label="营业相关">
              <el-option label="营业开始时间" value="business_hours_start" />
              <el-option label="营业结束时间" value="business_hours_end" />
              <el-option label="休息日" value="rest_days" />
            </el-option-group>
            <el-option-group label="预约相关">
              <el-option label="提前预约时间(分钟)" value="advance_booking_minutes" />
              <el-option label="单次服务时长(分钟)" value="service_duration_minutes" />
              <el-option label="每日最大预约数" value="max_bookings_per_day" />
            </el-option-group>
            <el-option-group label="价格相关">
              <el-option label="中式按摩价格(元)" value="price_chinese_massage" />
              <el-option label="泰式按摩价格(元)" value="price_thai_massage" />
              <el-option label="足底按摩价格(元)" value="price_foot_massage" />
              <el-option label="精油推背价格(元)" value="price_oil_back" />
              <el-option label="头部舒缓价格(元)" value="price_head_relax" />
            </el-option-group>
            <el-option-group label="系统相关">
              <el-option label="系统名称" value="system_name" />
              <el-option label="联系人电话" value="contact_phone" />
              <el-option label="联系地址" value="contact_address" />
            </el-option-group>
          </el-select>
          <div class="setting-hint" v-if="form.settingKey">
            说明：{{ getSettingDescription(form.settingKey) }}
          </div>
        </el-form-item>
        <el-form-item label="设置值" prop="settingValue">
          <el-input
            v-model="form.settingValue"
            :type="getSettingInputType(form.settingKey)"
            :rows="getSettingKeyType(form.settingKey) === 'textarea' ? 4 : 1"
            :placeholder="getSettingPlaceholder(form.settingKey)"
          />
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
import { Plus, Edit, Delete, InfoFilled } from '@element-plus/icons-vue'
import { getAllSettings, insertSetting, updateSetting, deleteSetting } from '../../api/setting'
import { eventBus } from '../../utils/eventBus'

const loading = ref(false)
const settings = ref([])
const showDialog = ref(false)
const submitting = ref(false)
const isEdit = ref(false)

const form = ref({
  settingKey: '',
  settingValue: ''
})

const rules = {
  settingKey: [{ required: true, message: '请选择或输入设置名称', trigger: 'blur' }],
  settingValue: [{ required: true, message: '请输入设置值', trigger: 'blur' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑设置' : '添加设置')

const settingDescriptions = {
  business_hours_start: '每天的营业开始时间，如 09:00',
  business_hours_end: '每天的营业结束时间，如 22:00',
  rest_days: '每周的休息日，如 周一',
  advance_booking_minutes: '客户需要提前多久预约，单位：分钟',
  service_duration_minutes: '每个服务项目的标准时长，单位：分钟',
  max_bookings_per_day: '每天最多可接受的预约数量',
  price_chinese_massage: '中式按摩的收费标准，单位：元',
  price_thai_massage: '泰式按摩的收费标准，单位：元',
  price_foot_massage: '足底按摩的收费标准，单位：元',
  price_oil_back: '精油推背的收费标准，单位：元',
  price_head_relax: '头部舒缓的收费标准，单位：元',
  system_name: '系统显示的店铺名称',
  contact_phone: '客户联系使用的电话号码',
  contact_address: '店铺的详细地址'
}

function getSettingDescription(key) {
  return settingDescriptions[key] || '自定义配置项'
}

function getSettingPlaceholder(key) {
  const placeholders = {
    business_hours_start: '例如：09:00',
    business_hours_end: '例如：22:00',
    rest_days: '例如：周一',
    advance_booking_minutes: '例如：60',
    service_duration_minutes: '例如：60',
    max_bookings_per_day: '例如：50',
    price_chinese_massage: '例如：128',
    price_thai_massage: '例如：168',
    price_foot_massage: '例如：88',
    price_oil_back: '例如：158',
    price_head_relax: '例如：98',
    system_name: '例如：舒心按摩',
    contact_phone: '例如：13888888888',
    contact_address: '例如：XX市XX区XX路XX号'
  }
  return placeholders[key] || '请输入设置值'
}

function getSettingInputType(key) {
  return 'text'
}

function getSettingKeyType(key) {
  const textareaKeys = ['contact_address', 'rest_days']
  return textareaKeys.includes(key) ? 'textarea' : 'text'
}

async function fetchSettings() {
  loading.value = true
  try {
    const res = await getAllSettings()
    settings.value = res.data || []
  } catch {} finally {
    loading.value = false
  }
}

function handleEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  showDialog.value = true
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除设置项「${row.settingKey}」吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteSetting(row.id)
      ElMessage.success('删除成功')
      await fetchSettings()
      eventBus.emit('dataChanged')
    } catch {}
  }).catch(() => {})
}

async function submitForm() {
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateSetting(form.value)
      ElMessage.success('更新成功')
    } else {
      await insertSetting(form.value)
      ElMessage.success('添加成功')
    }
    showDialog.value = false
    form.value = { settingKey: '', settingValue: '' }
    isEdit.value = false
    await fetchSettings()
    eventBus.emit('dataChanged')
  } catch {} finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchSettings()
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
  margin-bottom: 16px;
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

.setting-intro {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px 20px;
  margin-bottom: 20px;
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.setting-intro .el-icon {
  color: var(--accent);
  margin-top: 2px;
  flex-shrink: 0;
}

.setting-intro b {
  color: var(--text-primary);
}

.setting-key-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.setting-desc {
  font-size: 11px;
  color: var(--text-muted);
}

.setting-value-text {
  font-size: 14px;
  font-weight: 500;
}

.setting-hint {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 6px;
  padding: 6px 10px;
  background: rgba(108, 99, 255, 0.08);
  border-radius: 6px;
}
</style>
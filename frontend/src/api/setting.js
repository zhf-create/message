import request from './index'

export function getAllSettings() {
  return request.get('/system/setting')
}

export function getSettingById(id) {
  return request.get(`/system/setting/${id}`)
}

export function getSettingByKey(key) {
  return request.get(`/system/setting/key/${key}`)
}

export function insertSetting(setting) {
  return request.post('/system/setting', setting)
}

export function updateSetting(setting) {
  return request.put('/system/setting', setting)
}

export function deleteSetting(id) {
  return request.delete(`/system/setting/${id}`)
}
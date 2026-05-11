import request from './index'

export function getAllRecords() {
  return request.get('/customer/record')
}

export function getRecordById(id) {
  return request.get(`/customer/record/${id}`)
}

export function getRecordByPhone(phone) {
  return request.get(`/customer/record/phone/${phone}`)
}

export function insertRecord(record) {
  return request.post('/customer/record', record)
}

export function updateRecord(record) {
  return request.put('/customer/record', record)
}

export function deleteRecord(id) {
  return request.delete(`/customer/record/${id}`)
}
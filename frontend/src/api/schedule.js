import request from './index'

export function getAllSchedules() {
  return request.get('/schedule')
}

export function getScheduleById(id) {
  return request.get(`/schedule/${id}`)
}

export function getSchedulesByStaffId(staffId) {
  return request.get(`/schedule/staff/${staffId}`)
}

export function getSchedulesByDate(date) {
  return request.get(`/schedule/date/${date}`)
}

export function insertSchedule(schedule) {
  return request.post('/schedule', schedule)
}

export function updateSchedule(schedule) {
  return request.put('/schedule', schedule)
}

export function deleteSchedule(id) {
  return request.delete(`/schedule/${id}`)
}
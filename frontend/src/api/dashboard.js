import request from './index'

export function getStats() {
  return request.get('/dashboard/stats')
}

export function getWeeklySchedule() {
  return request.get('/dashboard/weekly-schedule')
}

export function getServiceDistribution() {
  return request.get('/dashboard/service-distribution')
}
import request from './index'

export function getRecentLogs() {
  return request.get('/operation/log/recent')
}
import request from './index'

export function login(username, password) {
  return request.post('/user/login', null, {
    params: { username, password }
  })
}

export function register(username, password, email) {
  return request.post('/user/register', null, {
    params: { username, password, email }
  })
}

export function getUserInfo() {
  return request.get('/user/info')
}

export function getAllUsers() {
  return request.get('/user')
}

export function getUserById(id) {
  return request.get(`/user/${id}`)
}

export function updateUser(user) {
  return request.put('/user', user)
}

export function addUser(user) {
  return request.post('/user', user)
}

export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}
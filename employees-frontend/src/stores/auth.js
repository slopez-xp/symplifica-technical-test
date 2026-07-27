import { ref } from 'vue'

const token = ref(localStorage.getItem('token') || null)

export function useAuth() {
  function setToken(t) {
    token.value = t
    localStorage.setItem('token', t)
  }

  function clearToken() {
    token.value = null
    localStorage.removeItem('token')
  }

  function getToken() {
    return token.value
  }

  function isLoggedIn() {
    return !!token.value
  }

  return { setToken, clearToken, getToken, isLoggedIn }
}
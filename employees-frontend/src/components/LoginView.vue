<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="login-brand">
        <i class="fas fa-building"></i>
        <span>Symplifica</span>
      </div>
      <h2>Welcome back</h2>
      <p class="login-sub">Sign in to access the dashboard</p>

      <div class="login-form">
        <div class="input-group">
          <i class="fas fa-user"></i>
          <input v-model="form.username" placeholder="Username" />
        </div>
        <div class="input-group">
          <i class="fas fa-lock"></i>
          <input v-model="form.password" placeholder="Password" type="password" />
        </div>
        <p v-if="error" class="error">{{ error }}</p>
        <button @click="login" :disabled="loading">
          <i class="fas fa-arrow-right-to-bracket"></i>
          {{ loading ? 'Signing in...' : 'Sign in' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuth } from '../stores/auth'

const emit = defineEmits(['logged-in'])
const { setToken } = useAuth()

const form = ref({ username: '', password: '' })
const error = ref(null)
const loading = ref(false)

async function login() {
  if (!form.value.username || !form.value.password) {
    error.value = 'All fields are required'
    return
  }
  loading.value = true
  error.value = null
  try {
    const res = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value)
    })
    const data = await res.json()
    if (res.ok) {
      setToken(data.token)
      emit('logged-in')
    } else {
      error.value = 'Invalid username or password'
    }
  } catch (e) {
    error.value = 'Connection error'
  }
  loading.value = false
}
</script>

<style>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0f1117;
}

.login-card {
  background: #1a1d27;
  border: 1px solid #2d3148;
  border-radius: 16px;
  padding: 40px;
  width: 380px;
}

.login-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 600;
  color: #f1f5f9;
  margin-bottom: 28px;
}

.login-brand i { color: #6366f1; }

.login-card h2 {
  font-size: 22px;
  color: #f1f5f9;
  margin-bottom: 4px;
}

.login-sub {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 28px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 10px;
  background: #0f1117;
  border: 1px solid #2d3148;
  border-radius: 8px;
  padding: 0 14px;
}

.input-group i { color: #64748b; font-size: 14px; }

.input-group input {
  background: transparent;
  border: none;
  padding: 12px 0;
  flex: 1;
  min-width: 0;
}

.input-group input:focus { border: none; }

.error {
  color: #ef4444;
  font-size: 13px;
  margin: 0;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
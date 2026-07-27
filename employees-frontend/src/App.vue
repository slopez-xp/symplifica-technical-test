<template>
  <div>
    <LoginView v-if="!loggedIn" @logged-in="onLoggedIn" />

    <div v-else>
      <nav class="topbar">
        <div class="topbar-brand">
          <i class="fas fa-building"></i>
          <span>Symplifica</span>
        </div>
        <button class="logout-btn" @click="logout">
          <i class="fas fa-arrow-right-from-bracket"></i> Logout
        </button>
      </nav>

      <div>
        <div class="content">
          <div class="top-grid">
            <div class="image-card">
              <img src="../src/assets/Greeting.png" alt="Dashboard" />
            </div>

            <div class="map-card">
              <EmployeeMap :employees="mapEmployees" :selected-id="selectedId" />
            </div>
          </div>

          <EmployeeList 
            @employee-selected="onEmployeeSelected" 
            @employees-loaded="onEmployeesLoaded"
            @data-changed="loadMapData"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import EmployeeList from './components/EmployeeList.vue'
import EmployeeMap from './components/EmployeeMap.vue'
import LoginView from './components/LoginView.vue'
import { useAuth } from './stores/auth'

const { isLoggedIn, clearToken, getToken } = useAuth()
const loggedIn = ref(isLoggedIn())
const mapEmployees = ref([])
const selectedId = ref(null)
const totalEmployees = ref(0)
const totalBenefits = ref(0)
const mappedEmployees = ref(0)

function authHeaders() {
  return {
    'Authorization': `Bearer ${getToken()}`
  }
}

onMounted(async () => {
  if (loggedIn.value) {
    await loadMapData()
  }
})

async function loadMapData() {
  const res = await fetch('http://localhost:8080/api/employees', {
    headers: authHeaders()
  })
  const list = await res.json()
  totalEmployees.value = list.length

  const details = await Promise.all(
    list.map(emp =>
      fetch(`http://localhost:8080/api/employees/${emp.id}`, {
        headers: authHeaders()
      }).then(r => r.json())
    )
  )

  const withLocation = details.filter(d => d.location)
  mapEmployees.value = withLocation
  mappedEmployees.value = withLocation.length

  const benefits = await Promise.all(
    list.map(emp =>
      fetch(`http://localhost:8080/api/employees/${emp.id}/benefits`, {
        headers: authHeaders()
      }).then(r => r.json())
    )
  )
  totalBenefits.value = benefits.flat().length
}

async function onEmployeeSelected(emp) {
  selectedId.value = emp ? emp.id : null
  if (emp) {
    const res = await fetch(`http://localhost:8080/api/employees/${emp.id}`, {
      headers: authHeaders()
    })
    const detail = await res.json()
    if (detail.location && !mapEmployees.value.find(e => e.employee.id === emp.id)) {
      mapEmployees.value.push(detail)
    }
  }
}

function onEmployeesLoaded(count) {
  totalEmployees.value = count
}

async function onLoggedIn() {
  loggedIn.value = true
  await loadMapData()
}

function logout() {
  clearToken()
  loggedIn.value = false
}
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }

body {
  font-family: "Schoolbell", cursive;
  background: #0f1117;
  color: #e2e8f0;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  padding: 0 24px;
  height: 56px;
  background: #1a1d27;
  border-bottom: 1px solid #2d3148;
  border-radius: 0 0 12px 12px;  
  position: sticky;
  top: 0;
  z-index: 1000;
}

.topbar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #f1f5f9;
}

.topbar-brand i { color: #6366f1; }

.content {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.top-grid {
  display: grid;
  grid-template-columns: 35% 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.map-card {
  background: #1a1d27;
  border: 1px solid #2d3148;
  border-radius: 12px;
  height: 320px;
  display: flex;
  flex-direction: column;
}

.map-card .section-header {
  margin-bottom: 12px;
}

#map {
  flex: 1;
  border-radius: 8px;
  min-height: 0;
}

.image-card {
  background: #1a1d27;
  border: 1px solid #2d3148;
  border-radius: 12px;
  height: 320px;
  overflow: hidden;
}

.image-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: 0% center; 
}

input {
  padding: 10px 14px;
  background: #1a1d27;
  border: 1px solid #2d3148;
  border-radius: 8px;
  color: #e2e8f0;
  font-size: 14px;
  font-family: "Schoolbell", cursive;
  flex: 1;
  min-width: 140px;
  outline: none;
}

input:focus { border-color: #6366f1; }
input::placeholder { color: #475569; }

button {
  padding: 10px 18px;
  background: #6366f1;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-family: "Schoolbell", cursive;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

button:hover { background: #4f46e5; }
ul { list-style: none; padding: 0; margin-top: 12px; }

.card {
  background: #1a1d27;
  border: 1px solid #2d3148;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
}

.form {
  display: flex;
  gap: 8px;
  margin: 12px 0;
  flex-wrap: wrap;
}

h2, h3, h4 { color: #f1f5f9; font-weight: 600; }

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-header h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
}

.section-header h2 i { color: #6366f1; }

.logout-btn {
  background: transparent;
  border: 1px solid #2d3148;
  color: #64748b;
  padding: 8px 14px;
  font-size: 13px;
}

.logout-btn:hover {
  border-color: #ef4444;
  color: #ef4444;
  background: transparent;
}
</style>
<template>
  <div class="card">
    <div class="section-header">
      <h2><i class="fas fa-users"></i> Employees</h2>
      <button @click="openCreateModal">
        <i class="fas fa-plus"></i>
      </button>
    </div>

    <!-- Create / Edit Modal -->
    <div v-if="showForm" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editingId ? 'Edit Employee' : 'New Employee' }}</h3>
          <button class="close-btn" @click="closeModal">
            <i class="fas fa-xmark"></i>
          </button>
        </div>
        <div class="form">
          <input v-model="form.name" placeholder="Full name" />
          <input v-model="form.email" placeholder="Email" />
          <input v-model="form.city" placeholder="City" />
          <button @click="editingId ? updateEmployee() : createEmployee()">
            <i class="fas fa-save"></i> {{ editingId ? 'Update' : 'Save' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Benefits Modal -->
    <div v-if="showBenefits" class="modal-overlay" @click.self="showBenefits = false">
      <div class="modal modal-wide">
        <div class="modal-header">
          <h3><i class="fas fa-gift"></i> Benefits — {{ benefitEmployee?.name }}</h3>
          <button class="close-btn" @click="showBenefits = false">
            <i class="fas fa-xmark"></i>
          </button>
        </div>

        <div class="location-bar" v-if="benefitLocation">
          <i class="fas fa-location-dot"></i>
          {{ benefitEmployee.city }} — {{ benefitLocation.latitude }}, {{ benefitLocation.longitude }}
        </div>
        <div class="location-bar no-location" v-else>
          <i class="fas fa-circle-exclamation"></i> Location not found
        </div>

        <div class="form">
          <input v-model="benefitForm.name" placeholder="Benefit name" />
          <input v-model="benefitForm.amount" placeholder="Amount" type="number" />
          <button @click="createBenefit">
            <i class="fas fa-plus"></i> Add
          </button>
        </div>

        <ul class="benefits-list">
          <li v-for="benefit in benefits" :key="benefit.id" class="benefit-item">
            <span><i class="fas fa-circle-check"></i> {{ benefit.name }}</span>
            <div class="benefit-right">
              <span class="amount">${{ benefit.amount.toLocaleString() }}</span>
              <button class="icon-btn delete" @click="deleteBenefit(benefit.id)">
                <i class="fas fa-trash"></i>
              </button>
            </div>
          </li>
        </ul>

        <p v-if="benefits.length === 0" class="empty">No benefits registered yet.</p>
      </div>
    </div>

    <table>
      <thead>
        <tr>
          <th><i class="fas fa-hashtag"></i> ID</th>
          <th><i class="fas fa-user"></i> Name</th>
          <th><i class="fas fa-envelope"></i> Email</th>
          <th><i class="fas fa-location-dot"></i> City</th>
          <th><i class="fas fa-sliders"></i> Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="emp in employees"
          :key="emp.id"
          :class="{ selected: selected && selected.id === emp.id }"
          @click="selectEmployee(emp)"
        >
          <td class="id-cell">#{{ emp.id }}</td>
          <td> {{ emp.name }}</td>
          <td class="muted">{{ emp.email }}</td>
          <td> {{ emp.city }}</td>
          <td class="actions-cell" @click.stop>
            <button class="icon-btn edit" @click="openEditModal(emp)">
              <i class="fas fa-pencil"></i>
            </button>
            <button class="icon-btn benefit" @click="openBenefits(emp)">
              <i class="fas fa-gift"></i>
            </button>
            <button class="icon-btn delete" @click="deleteEmployee(emp.id)">
              <i class="fas fa-trash"></i>
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <p v-if="employees.length === 0" class="empty">No employees registered yet.</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuth } from '../stores/auth'
const { getToken } = useAuth()

function authHeaders() {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${getToken()}`
  }
}

const emit = defineEmits(['employee-selected', 'employees-loaded', 'data-changed'])

const employees = ref([])
const selected = ref(null)
const showForm = ref(false)
const editingId = ref(null)
const form = ref({ name: '', email: '', city: '' })

const showBenefits = ref(false)
const benefitEmployee = ref(null)
const benefits = ref([])
const benefitForm = ref({ name: '', amount: '' })
const benefitLocation = ref(null)

onMounted(async () => { await loadEmployees() })

async function loadEmployees() {
  const res = await fetch('http://localhost:8080/api/employees', {
    headers: {
      'Authorization': `Bearer ${getToken()}`
    }
  })
  employees.value = await res.json()
  emit('employees-loaded', employees.value.length)
}

function openCreateModal() {
  editingId.value = null
  form.value = { name: '', email: '', city: '' }
  showForm.value = true
}

function openEditModal(emp) {
  editingId.value = emp.id
  form.value = { name: emp.name, email: emp.email, city: emp.city }
  showForm.value = true
}

function closeModal() {
  showForm.value = false
  editingId.value = null
  form.value = { name: '', email: '', city: '' }
}

function validateForm() {
  if (!form.value.name || !form.value.email || !form.value.city) {
    alert('All fields are required')
    return false
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(form.value.email)) {
    alert('Please enter a valid email address')
    return false
  }
  return true
}

async function createEmployee() {
  if (!validateForm()) return
  await fetch('http://localhost:8080/api/employees', {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(form.value)
  })
  closeModal()
  emit('data-changed')
  await loadEmployees()
}

async function updateEmployee() {
  if (!validateForm()) return
  await fetch(`http://localhost:8080/api/employees/${editingId.value}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(form.value)
  })
  closeModal()
  emit('data-changed')
  await loadEmployees()
}

async function deleteEmployee(id) {
  await fetch(`http://localhost:8080/api/employees/${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  })
  if (selected.value && selected.value.id === id) {
    selected.value = null
    emit('employee-selected', null)
  }
  await loadEmployees()
  emit('data-changed')
}

function selectEmployee(emp) {
  if (selected.value && selected.value.id === emp.id) {
    selected.value = null
    emit('employee-selected', null)
  } else {
    selected.value = emp
    emit('employee-selected', emp)
  }
}

async function openBenefits(emp) {
  benefitEmployee.value = emp
  await loadBenefits(emp.id)
  const res = await fetch(`http://localhost:8080/api/employees/${emp.id}`, {
    headers: {
      'Authorization': `Bearer ${getToken()}`
    }
  })
  const detail = await res.json()
  benefitLocation.value = detail.location
  showBenefits.value = true
}

async function loadBenefits(id) {
  const res = await fetch(`http://localhost:8080/api/employees/${id}/benefits`, {
    headers: {
      'Authorization': `Bearer ${getToken()}`
    }
  })
  benefits.value = await res.json()
}

async function createBenefit() {
  if (!benefitForm.value.name || !benefitForm.value.amount) {
    alert('All fields are required')
    return
  }
  await fetch(`http://localhost:8080/api/employees/${benefitEmployee.value.id}/benefits`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({
      name: benefitForm.value.name,
      amount: parseFloat(benefitForm.value.amount)
    })
  })
  benefitForm.value = { name: '', amount: '' }
  await loadBenefits(benefitEmployee.value.id)
}

async function deleteBenefit(benefitId) {
  await fetch(`http://localhost:8080/api/employees/${benefitEmployee.value.id}/benefits/${benefitId}`, {
    method: 'DELETE',
    headers: authHeaders()
  })
  await loadBenefits(benefitEmployee.value.id)
}
</script>

<style>
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
}

.section-header h2 i { color: #6366f1; }

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

thead tr { border-bottom: 1px solid #2d3148; }

th {
  text-align: center;
  padding: 10px 12px;
  color: #64748b;
  font-weight: 500;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

tbody tr {
  border-bottom: 1px solid #1e2235;
  cursor: pointer;
  transition: background 0.15s;
}

tbody tr:hover { background: #22263a; }

tbody tr.selected {
  background: #22263a;
  border-left: 2px solid #6366f1;
}

td { padding: 12px; color: #e2e8f0; }
.id-cell { color: #64748b; font-size: 13px; }
.muted { color: #64748b; }

.actions-cell {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
}

.icon-btn {
  padding: 6px 10px;
  background: transparent;
  border-radius: 6px;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.icon-btn.edit {
  border: 1px solid #6366f1;
  color: #6366f1;
}

.icon-btn.edit:hover {
  background: #6366f1;
  color: white;
}

.icon-btn.benefit {
  border: 1px solid #22c55e;
  color: #22c55e;
}

.icon-btn.benefit:hover {
  background: #22c55e;
  color: white;
}

.icon-btn.delete {
  border: 1px solid #ef4444;
  color: #ef4444;
}

.icon-btn.delete:hover {
  background: #ef4444;
  color: white;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 700;
}

.modal {
  background: #1a1d27;
  border: 1px solid #2d3148;
  border-radius: 12px;
  padding: 24px;
  width: 420px;
}

.modal-wide { width: 560px; }

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.close-btn {
  background: transparent;
  border: none;
  color: #64748b;
  padding: 4px 8px;
  font-size: 16px;
}

.close-btn:hover { background: transparent; color: #e2e8f0; }

.benefits-list { margin-top: 16px; }

.benefit-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 8px;
  background: #0f1117;
  border: 1px solid #2d3148;
  margin-bottom: 6px;
  font-size: 14px;
  cursor: default;
}

.benefit-item > span > i { color: #22c55e; margin-right: 6px; }
.benefit-item .icon-btn.delete:hover i { color: white; }
.benefit-right { display: flex; align-items: center; gap: 12px; }
.amount { color: #6366f1; font-weight: 600; }
.empty { color: #475569; font-size: 13px; margin-top: 12px; }

.location-bar {
  background: #0f1117;
  border: 1px solid #2d3148;
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 13px;
  color: #6366f1;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.location-bar.no-location { color: #ef4444; border-color: #ef4444; }
</style>
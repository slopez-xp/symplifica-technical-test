<template>
  <div class="detail">
    <div v-if="detail">
      <div class="info-grid">
        <div class="info-item">
          <span><i class="fas fa-user"></i> Name</span>
          <strong>{{ detail.employee.name }}</strong>
        </div>
        <div class="info-item">
          <span><i class="fas fa-envelope"></i> Email</span>
          <strong>{{ detail.employee.email }}</strong>
        </div>
        <div class="info-item">
          <span><i class="fas fa-city"></i> City</span>
          <strong>{{ detail.employee.city }}</strong>
        </div>
        <div class="info-item coords" v-if="detail.location">
          <span><i class="fas fa-location-dot"></i> Coordinates</span>
          <strong>{{ detail.location.latitude }}, {{ detail.location.longitude }}</strong>
        </div>
        <div class="info-item no-location" v-else>
          <span><i class="fas fa-circle-exclamation"></i> Coordinates</span>
          <strong>Location not found</strong>
        </div>
      </div>

      <div class="benefits-header">
        <h4><i class="fas fa-gift"></i> Benefits</h4>
        <button @click="showBenefitForm = !showBenefitForm">
          <i class="fas fa-plus"></i>
        </button>
      </div>

      <div v-if="showBenefitForm" class="form">
        <input v-model="form.name" placeholder="Benefit name" />
        <input v-model="form.amount" placeholder="Amount" type="number" />
        <button @click="createBenefit">
          <i class="fas fa-save"></i>
        </button>
      </div>

      <ul class="benefits-list">
        <li v-for="benefit in benefits" :key="benefit.id" class="benefit-item">
          <span><i class="fas fa-circle-check"></i> {{ benefit.name }}</span>
          <div class="benefit-right">
            <span class="amount">${{ benefit.amount.toLocaleString() }}</span>
            <button class="delete-btn" @click="deleteBenefit(benefit.id)">
              <i class="fas fa-trash"></i>
            </button>
          </div>
        </li>
      </ul>

      <p v-if="benefits.length === 0" class="empty">No benefits registered yet.</p>
    </div>

    <p v-else class="empty"><i class="fas fa-spinner fa-spin"></i> Loading...</p>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({ employeeId: Number })
const detail = ref(null)
const benefits = ref([])
const showBenefitForm = ref(false)
const form = ref({ name: '', amount: '' })

watch(() => props.employeeId, async (id) => {
  await loadDetail(id)
  await loadBenefits(id)
}, { immediate: true })

async function loadDetail(id) {
  const res = await fetch(`http://localhost:8080/api/employees/${id}`)
  detail.value = await res.json()
}

async function loadBenefits(id) {
  const res = await fetch(`http://localhost:8080/api/employees/${id}/benefits`)
  benefits.value = await res.json()
}

async function createBenefit() {
  if (!form.value.name || !form.value.amount) {
    alert('All fields are required')
    return
  }
  await fetch(`http://localhost:8080/api/employees/${props.employeeId}/benefits`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      name: form.value.name,
      amount: parseFloat(form.value.amount)
    })
  })
  form.value = { name: '', amount: '' }
  showBenefitForm.value = false
  await loadBenefits(props.employeeId)
}

async function deleteBenefit(benefitId) {
  await fetch(`http://localhost:8080/api/employees/${props.employeeId}/benefits/${benefitId}`, {
    method: 'DELETE'
  })
  await loadBenefits(props.employeeId)
}
</script>

<style>
.detail {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #2d3148;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin-bottom: 20px;
}

.info-item {
  background: #0f1117;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #2d3148;
}

.info-item span {
  display: block;
  font-size: 11px;
  color: #64748b;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.info-item span i { color: #6366f1; }

.info-item strong {
  font-size: 14px;
  color: #e2e8f0;
  font-weight: 500;
}

.info-item.coords strong { color: #6366f1; font-size: 13px; }

.benefits-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.benefits-header h4 {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
}

.benefits-header h4 i { color: #6366f1; }

.benefits-list { margin-top: 8px; }

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

.benefit-item:hover { background: #0f1117; }

.benefit-item i { color: #22c55e; margin-right: 6px; }

.amount { color: #6366f1; font-weight: 600; }

.empty { color: #475569; font-size: 13px; margin-top: 8px; }

.benefit-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.delete-btn {
  padding: 6px 5px 6px 10px;
  background: transparent;
  border: 1px solid #ef4444;
  color: #ef4444;
  border-radius: 6px;
  font-size: 12px;
  line-height: 1;
}

.delete-btn i {
  color: #ef4444;
}

.delete-btn:hover {
  background: #ef4444;
  color: white;
}

.delete-btn:hover i {
  color: white;
}

.no-location span i { color: #ef4444; }

.no-location strong { color: #ef4444; }

.no-location { color: #ef4444; border-color: #ef4444; }
</style>
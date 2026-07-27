<template>
  <div id="map" ref="mapRef"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import L from 'leaflet'

const props = defineProps({
  employees: Array,
  selectedId: Number
})

const mapRef = ref(null)
let map = null
let markers = {}

const colors = ['#6366f1', '#22c55e', '#ef4444', '#f59e0b', '#06b6d4', '#8b5cf6', '#ec4899', '#f97316', '#10b981', '#3b82f6']

function coloredIcon(color) {
  return L.divIcon({
    className: '',
    html: `<div style="
      width:14px;height:14px;
      background:${color};
      border-radius:50%;
      border:2px solid white;
      box-shadow:0 0 6px ${color}
    "></div>`,
    iconSize: [14, 14],
    iconAnchor: [7, 7]
  })
}

onMounted(() => {
  map = L.map(mapRef.value, { zoomControl: false, attributionControl: false }).setView([30, 0], 1)

  L.tileLayer('https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png').addTo(map)
})

watch(() => props.employees, (list) => {
  if (!map || !list) return
  list.forEach(detail => {
    if (!detail.location) return
    const id = detail.employee.id
    if (markers[id]) return

    const lat = parseFloat(detail.location.latitude)
    const lng = parseFloat(detail.location.longitude)

    const color = colors[id % colors.length]
    const marker = L.marker([lat, lng], { icon: coloredIcon(color) })
      .addTo(map)
      .bindPopup(`
        <strong>${detail.employee.name}</strong><br>
        ${detail.employee.city}<br>
        <small>${detail.employee.email}</small>
      `)

    markers[id] = { marker, lat, lng }
  })
}, { deep: true })

watch(() => props.selectedId, (id) => {
  if (!map) return
  if (!id || !markers[id]) {
    map.flyTo([30, 0], 1, { duration: 1 })
    return
  }
  const { marker, lat, lng } = markers[id]
  map.flyTo([lat, lng], 10, { duration: 1 })
  marker.openPopup()
})
</script>

<style>
#map {
  height: 100%;
  width: 100%;
  background: #0f1117;
}

.leaflet-popup-content-wrapper {
  background: #1a1d27;
  color: #e2e8f0;
  border: 1px solid #2d3148;
  border-radius: 8px;
}

.leaflet-popup-tip {
  background: #1a1d27;
}
</style>